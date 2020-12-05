/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.assistant.ui.listRfidScan;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import library.assistant.database.DatabaseHandler;
import library.assistant.ui.rfid.ReadAsync;

/**
 * FXML Controller class
 *
 * @author Enayet
 */
public class Book_list_rfidScanController implements Initializable {

    @FXML
    private TableView<Book> tableView;
    @FXML
    private TableColumn<Book, String> colTitle;
    @FXML
    private TableColumn<Book, String> colBookId;
    @FXML
    private TableColumn<Book, String> colAuthor;
    @FXML
    private TableColumn<Book, String> colPublisher;
    @FXML
    private TableColumn<Book, Boolean> colAvailability;
    @FXML
    private TableColumn<Book, String> colEPC;
    @FXML
    private Button btnScan;
    
    ObservableList<Book> list = FXCollections.observableArrayList();


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initCol();
    }

    private void initCol() {
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colBookId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colPublisher.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        colAvailability.setCellValueFactory(new PropertyValueFactory<>("availability"));
        colEPC.setCellValueFactory(new PropertyValueFactory<>("epc"));
    }

    @FXML
    private void onBtnRfidScan(ActionEvent event) {
        System.out.println("Hello RFID Click");

        //RFID Reading
        ReadAsync readAsync = new ReadAsync();
        readAsync.readasync_start(new String[]{"tmr:///com5", "--ant", "1"}, 5000);
        ArrayList<String> epcList = readAsync.getmEpcList();

        Set<String> uniqueEpc = new HashSet<String>(epcList);

        for (String aEpc : uniqueEpc) {
            System.out.println(aEpc);
        }
        
        System.out.println("============================================================================");
        
        for (String aEpc : uniqueEpc) {

            DatabaseHandler handler = DatabaseHandler.getInstance();
            String qu = "SELECT * FROM BOOK WHERE epcValue = '" + aEpc + "'";
            ResultSet rs = handler.execQuery(qu);

            try {
                while (rs.next()) {
                    String bookTitl = rs.getString("title");
                    String bookId = rs.getString("id");
                    String bookAuthor = rs.getString("author");
                    String bookPublisher = rs.getString("publisher");
                    Boolean bookAvail = rs.getBoolean("isAvail");
                    String bookEPC = rs.getString("epcValue");
                    
                    Book book = new Book(bookEPC, bookTitl, bookId, bookAuthor, bookPublisher, bookAvail);
                    list.add(book);

                }
            } catch (SQLException ex) {
                Logger.getLogger(Book_list_rfidScanController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        
        tableView.getItems().setAll(list);

    }

    @FXML
    private void onBtnClear(ActionEvent event) {
        list.clear();
        tableView.getItems().clear();
    }

    public static class Book {

        private SimpleStringProperty epc;
        private SimpleStringProperty title;
        private SimpleStringProperty id;
        private SimpleStringProperty author;
        private SimpleStringProperty publisher;
        private SimpleBooleanProperty availability;

        public Book(String epc, String title, String id, String author, String publisher, Boolean availability) {
            this.epc = new SimpleStringProperty(epc);
            this.title = new SimpleStringProperty(title);
            this.id = new SimpleStringProperty(id);;
            this.author = new SimpleStringProperty(author);;
            this.publisher = new SimpleStringProperty(publisher);
            this.availability = new SimpleBooleanProperty(availability);
        }

        public String getEpc() {
            return epc.get();
        }

        public String getTitle() {
            return title.get();
        }

        public String getId() {
            return id.get();
        }

        public String getAuthor() {
            return author.get();
        }

        public String getPublisher() {
            return publisher.get();
        }

        public Boolean getAvailability() {
            return availability.getValue();
        }

    }
}
