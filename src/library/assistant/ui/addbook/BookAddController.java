package library.assistant.ui.addbook;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import library.assistant.database.DatabaseHandler;
import library.assistant.ui.rfid.Read;

/**
 * FXML Controller class
 *
 * @author Enayet
 */
public class BookAddController implements Initializable {

    @FXML
    private JFXTextField title;
    @FXML
    private JFXTextField id;
    @FXML
    private JFXTextField author;
    @FXML
    private JFXTextField publisher;

    @FXML
    private AnchorPane rootPaneAddBook;

    DatabaseHandler databaseHandler;

    @FXML
    private JFXTextField epcCode;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();

        checkData();
    }

    @FXML
    private void saveAddBook(ActionEvent event) {
        String bookId = id.getText();
        String bookEpc = epcCode.getText();
        String bookAuthor = author.getText();
        String bookTitle = title.getText();
        String bookPublisher = publisher.getText();

        if (bookId.isEmpty() || bookEpc.isEmpty() || bookAuthor.isEmpty() || bookTitle.isEmpty() || bookPublisher.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Enter in all fields");
            alert.showAndWait();
            return;
        }

        String qu = "INSERT INTO BOOK VALUES ("
                + "'" + bookId + "',"
                + "'" + bookEpc + "',"
                + "'" + bookTitle + "',"
                + "'" + bookAuthor + "',"
                + "'" + bookPublisher + "',"
                + "" + "true" + ""
                + ")";
        System.out.println(qu);

        if (databaseHandler.execAction(qu)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Success on writing data");
            alert.showAndWait();
        } else //Error
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Failed on writing data");
            alert.showAndWait();
        }
    }

    @FXML
    private void cancelBookAdd(ActionEvent event) {
        Stage stage = (Stage) rootPaneAddBook.getScene().getWindow();
        stage.close();
    }

    private void checkData() {
        String qu = "SELECT title FROM BOOK";
        ResultSet rs = databaseHandler.execQuery(qu);
        try {
            while (rs.next()) {
                String titlex = rs.getString("title");
                System.out.println(titlex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(BookAddController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void getEpcByRfid(ActionEvent event) {

        Read readBook = new Read();
        readBook.read_start(new String[]{"tmr:///com5", "--ant", "1"}, 200);
        ArrayList<String> getEpcVals = readBook.getmEpcList();

        if (getEpcVals.size() > 1) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Please Scan only one RFID Tag!");
            alert.showAndWait();
            return;
        }
        if(getEpcVals.size() <= 0) {
            epcCode.setText("");
            return;
        }
        epcCode.setText(getEpcVals.get(0));

    }

}
