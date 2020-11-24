package library.assistant.ui.rfid;

import java.util.ArrayList;


public class RfidReader {
    
    
    public static void main(String[] args) {
        
//        ReadAsync.readasync_start(new String [] {"tmr:///com5", "--ant", "1"}, 5);
        Read readBook = new Read();
        readBook.read_start(new String [] {"tmr:///com5", "--ant", "1"}, 5);
        ArrayList<String> getEpcVals = readBook.getmEpcList();
        
        for(String epc : getEpcVals) {
            System.out.println(epc);
        }
        
        
    }
    
}
