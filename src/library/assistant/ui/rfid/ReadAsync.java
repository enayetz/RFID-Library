// Import the API
package library.assistant.ui.rfid;

import com.thingmagic.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ReadAsync {

    SerialPrinter serialPrinter;
    StringPrinter stringPrinter;
    TransportListener currentListener;
    ArrayList<String> mEpcList = null;

    public ArrayList<String> getmEpcList() {
        return mEpcList;
    }

    void usage() {
        System.out.printf("Usage: Please provide valid arguments, such as:\n"
                + "readasync [-v] [reader-uri] [--ant n[,n...]] \n"
                + "-v  Verbose: Turn on transport listener\n"
                + "reader-uri  Reader URI: e.g., \"tmr:///COM1\", \"tmr://astra-2100d3\"\n"
                + "--ant  Antenna List: e.g., \"--ant 1\", \"--ant 1,2\"\n"
                + "e.g: tmr:///com1 --ant 1,2 ; tmr://10.11.115.32 --ant 1,2\n ");
        System.exit(1);
    }

    public void setTrace(Reader r, String args[]) {
        if (args[0].toLowerCase().equals("on")) {
            r.addTransportListener(Reader.simpleTransportListener);
            currentListener = Reader.simpleTransportListener;
        } else if (currentListener != null) {
            r.removeTransportListener(Reader.simpleTransportListener);
        }
    }

    class SerialPrinter implements TransportListener {

        public void message(boolean tx, byte[] data, int timeout) {
            System.out.print(tx ? "Sending: " : "Received:");
            for (int i = 0; i < data.length; i++) {
                if (i > 0 && (i & 15) == 0) {
                    System.out.printf("\n         ");
                }
                System.out.printf(" %02x", data[i]);
            }
            System.out.printf("\n");
        }
    }

    class StringPrinter implements TransportListener {

        public void message(boolean tx, byte[] data, int timeout) {
            System.out.println((tx ? "Sending:\n" : "Receiving:\n")
                    + new String(data));
        }
    }

    public void readasync_start(String argv[], int millSec) {
        // Program setup
        Reader r = null;
        int nextarg = 0;
        boolean trace = false;
        int[] antennaList = null;
        mEpcList = new ArrayList<>();
        

        if (argv.length < 1) {
            usage();
        }

        if (argv[nextarg].equals("-v")) {
            trace = true;
            nextarg++;
        }

        // Create Reader object, connecting to physical device
        try {
            String readerURI = argv[nextarg];
            nextarg++;

            for (; nextarg < argv.length; nextarg++) {
                String arg = argv[nextarg];
                if (arg.equalsIgnoreCase("--ant")) {
                    if (antennaList != null) {
                        System.out.println("Duplicate argument: --ant specified more than once");
                        usage();
                    }
                    antennaList = parseAntennaList(argv, nextarg);
                    nextarg++;
                } else {
                    System.out.println("Argument " + argv[nextarg] + " is not recognised");
                    usage();
                }
            }

            r = Reader.create(readerURI);
            if (trace) {
                setTrace(r, new String[]{"on"});
            }
            r.connect();
            if (Reader.Region.UNSPEC == (Reader.Region) r.paramGet("/reader/region/id")) {
                Reader.Region[] supportedRegions = (Reader.Region[]) r.paramGet(TMConstants.TMR_PARAM_REGION_SUPPORTEDREGIONS);
                if (supportedRegions.length < 1) {
                    throw new Exception("Reader doesn't support any regions");
                } else {
                    r.paramSet("/reader/region/id", supportedRegions[0]);
                }
            }

            if (r.isAntDetectEnabled(antennaList)) {
                System.out.println("Module doesn't has antenna detection support, please provide antenna list");
                r.destroy();
                usage();
            }

            SimpleReadPlan plan = new SimpleReadPlan(antennaList, TagProtocol.GEN2, null, null, 1000);
            r.paramSet(TMConstants.TMR_PARAM_READ_PLAN, plan);
            ReadExceptionListener exceptionListener = new TagReadExceptionReceiver();
            r.addReadExceptionListener(exceptionListener);
            // Create and add tag listener
            EpcValuesReadListener objEpcReadLi = new EpcValuesReadListener();
            ReadListener r1 = objEpcReadLi;
            r.addReadListener(r1);
            // search for tags in the background
            r.startReading();
            System.out.println("Do other work here");
            Thread.sleep(millSec);
            mEpcList = objEpcReadLi.getmEpcList();
            r.stopReading();

            r.removeReadListener(r1);
            r.removeReadExceptionListener(exceptionListener);

            // Shut down reader
            r.destroy();
        } catch (ReaderException re) {
            // Shut down reader
            if (r != null) {
                r.destroy();
            }
            System.out.println("ReaderException: " + re.getMessage());
        } catch (Exception re) {
            // Shut down reader
            if (r != null) {
                r.destroy();
            }
            System.out.println("Exception: " + re.getMessage());
        }
    }
    
    class EpcValuesReadListener implements ReadListener {

        ArrayList<String> mEpcList = new ArrayList<>();

        public ArrayList<String> getmEpcList() {
            return this.mEpcList;
        }
        
        @Override
        public void tagRead(Reader reader, TagReadData trd) {
            mEpcList.add(trd.epcString());
            System.out.print(trd);
        }
        
    }

    class PrintListener implements ReadListener {

        public void tagRead(Reader r, TagReadData tr) {
            System.out.println("Background read: " + tr.toString());
        }

    }

    class TagReadExceptionReceiver implements ReadExceptionListener {

        String strDateFormat = "M/d/yyyy h:m:s a";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);

        public void tagReadException(com.thingmagic.Reader r, ReaderException re) {
            String format = sdf.format(Calendar.getInstance().getTime());
            System.out.println("Reader Exception: " + re.getMessage() + " Occured on :" + format);
            if (re.getMessage().equals("Connection Lost")) {
                System.exit(1);
            }
        }
    }

    int[] parseAntennaList(String[] args, int argPosition) {
        int[] antennaList = null;
        try {
            String argument = args[argPosition + 1];
            String[] antennas = argument.split(",");
            int i = 0;
            antennaList = new int[antennas.length];
            for (String ant : antennas) {
                antennaList[i] = Integer.parseInt(ant);
                i++;
            }
        } catch (IndexOutOfBoundsException ex) {
            System.out.println("Missing argument after " + args[argPosition]);
            usage();
        } catch (Exception ex) {
            System.out.println("Invalid argument at position " + (argPosition + 1) + ". " + ex.getMessage());
            usage();
        }
        return antennaList;
    }

}
