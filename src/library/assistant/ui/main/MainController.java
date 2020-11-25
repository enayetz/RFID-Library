package library.assistant.ui.main;

import com.jfoenix.effects.JFXDepthManager;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.database.DatabaseHandler;
import library.assistant.ui.rfid.Read;

public class MainController implements Initializable {

    @FXML
    private HBox book_info;
    @FXML
    private HBox member_info;
    @FXML
    private TextField bookIdInput;
    @FXML
    private Text bookName;
    @FXML
    private Text bookAuthor;
    @FXML
    private Text bookStatus;

    DatabaseHandler databaseHandler;

    @FXML
    private TextField memberIDInput;
    @FXML
    private Text memberName;
    @FXML
    private Text memberMobile;
    @FXML
    private Text rfidBookName;
    @FXML
    private Text rfidAuthor;
    @FXML
    private Text rfidStatus;
    @FXML
    private Text rfidMemberName;
    @FXML
    private Text rfidMobile;
    @FXML
    private HBox rfid_book_info;
    @FXML
    private HBox rfid_member_info;
    
    private String mRfidBookId;
    private String mRfidMemberId;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        JFXDepthManager.setDepth(book_info, 1);
        JFXDepthManager.setDepth(member_info, 1);

        databaseHandler = DatabaseHandler.getInstance();
    }

    @FXML
    private void loadAddMember(ActionEvent event) {
        loadWindow("/library/assistant/ui/addmember/member_add.fxml", "Add New Member");
    }

    @FXML
    private void loadAddBook(ActionEvent event) {
        loadWindow("/library/assistant/ui/addbook/add_book.fxml", "Add New Book");
    }

    @FXML
    private void loadMemberTable(ActionEvent event) {
        loadWindow("/library/assistant/ui/listmember/member_list.fxml", "Member List");
    }

    @FXML
    private void loadBookTable(ActionEvent event) {
        loadWindow("/library/assistant/ui/listbook/book_list.fxml", "Book List");
    }

    void loadWindow(String loc, String title) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource(loc));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void loadBookInfo(ActionEvent event) {

        clearBookInfo();
        String bookId = bookIdInput.getText();
        String qu = "SELECT * FROM BOOK WHERE id = '" + bookId + "'";

        ResultSet rs = databaseHandler.execQuery(qu);
        Boolean flag = false;

        try {
            while (rs.next()) {
                String bName = rs.getString("title");
                String bAuthor = rs.getString("author");
                Boolean bStatus = rs.getBoolean("isAvail");
                String status = bStatus ? "Available" : "Not Available";

                bookName.setText(bName);
                bookAuthor.setText(bAuthor);
                bookStatus.setText(status);

                flag = true;
            }
            if (!flag) {
                bookName.setText("No such Book is avalable");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void clearBookInfo() {
        bookName.setText("");
        bookAuthor.setText("");
        bookStatus.setText("");
    }

    @FXML
    private void loadMemberInfo(ActionEvent event) {

        clearMemberInfo();
        Boolean flag = false;
        String memberId = memberIDInput.getText();
        String qu = "SELECT * FROM MEMBER WHERE id = '" + memberId + "'";

        ResultSet rs = databaseHandler.execQuery(qu);
        try {
            while (rs.next()) {
                String memberName = rs.getString("name");
                String memberMobile = rs.getString("mobile");

                this.memberName.setText(memberName);
                this.memberMobile.setText(memberMobile);

                flag = true;
            }
            if (!flag) {
                memberName.setText("No such member available");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void clearMemberInfo() {
        memberName.setText("");
        memberMobile.setText("");
    }

    @FXML
    private void rfidBookScan(ActionEvent event) {
        clearRfidBookInfo();
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

        if (getEpcVals.size() <= 0) {
            clearRfidBookInfo();
            rfidBookName.setText("Please Bring RFID Book Nearby!");
            return;
        }

        String bookEpc = getEpcVals.get(0);
        String qu = "SELECT * FROM BOOK WHERE epcValue = '" + bookEpc + "'";

        ResultSet rs = databaseHandler.execQuery(qu);
        Boolean flag = false;

        try {
            while (rs.next()) {
                //for book issue operation
                mRfidBookId = rs.getString("id");
                String bName = rs.getString("title");
                String bAuthor = rs.getString("author");
                Boolean bStatus = rs.getBoolean("isAvail");
                String status = bStatus ? "Available" : "Not Available";

                rfidBookName.setText(bName);
                rfidAuthor.setText(bAuthor);
                rfidStatus.setText(status);

                flag = true;
            }
            if (!flag) {
                rfidBookName.setText("No such Book is avalable");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void rfidMemberScan(ActionEvent event) {
        clearRfidMemberInfo();
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

        if (getEpcVals.size() <= 0) {
            clearRfidMemberInfo();
            rfidMemberName.setText("Please Bring RFID Member Nearby!");
            return;
        }

        String memberEpc = getEpcVals.get(0);
        String qu = "SELECT * FROM MEMBER WHERE epcValue = '" + memberEpc + "'";

        ResultSet rs = databaseHandler.execQuery(qu);
        Boolean flag = false;

        try {
            while (rs.next()) {
                //for the use of issue operation
                mRfidMemberId = rs.getString("id");
                String mName = rs.getString("name");
                String mAuthor = rs.getString("mobile");

                rfidMemberName.setText(mName);
                rfidMobile.setText(mAuthor);

                flag = true;
            }
            if (!flag) {
                rfidMemberName.setText("No such Book is avalable");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void clearRfidBookInfo() {
        rfidBookName.setText("");
        rfidAuthor.setText("");
        rfidStatus.setText("");
    }

    private void clearRfidMemberInfo() {
        rfidMemberName.setText("");
        rfidMobile.setText("");
    }

    @FXML
    private void loadIssueOperation(ActionEvent event) {

        String memberId = memberIDInput.getText();
        String bookId = bookIdInput.getText();
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Issue Operation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure want to issue the book " + bookName.getText() + "\n to " + memberName.getText() + " ?");

        Optional<ButtonType> response = alert.showAndWait();
        if (response.get() == ButtonType.OK) {
            String str = "INSERT INTO ISSUE(memberId, bookId) VALUES ("
                    + "'" + memberId + "',"
                    + "'" + bookId + "')";
            String str2 = "UPDATE BOOK SET isAvail = false WHERE id = '" + bookId + "'";
            System.out.println(str + " and " + str2);

            if (databaseHandler.execAction(str) && databaseHandler.execAction(str2)) {
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Success");
                alert1.setHeaderText(null);
                alert1.setContentText("Book Issue Complete");

                alert1.showAndWait();
            } else {
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("Failed");
                alert1.setHeaderText(null);
                alert1.setContentText("Issue Operation Failed");
                alert1.showAndWait();
            }
        } else {
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Cancelled");
            alert1.setHeaderText(null);
            alert1.setContentText("Issue Operation cancelled");
            alert1.showAndWait();
        }

    }

    @FXML
    private void rfidBookIssueOperation(ActionEvent event) {
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Issue Operation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure want to issue the book " + rfidBookName.getText() + "\n to " + rfidMemberName.getText() + " ?");

        Optional<ButtonType> response = alert.showAndWait();
        if (response.get() == ButtonType.OK) {
            String str = "INSERT INTO ISSUE(memberId, bookId) VALUES ("
                    + "'" + mRfidMemberId + "',"
                    + "'" + mRfidBookId + "')";
            String str2 = "UPDATE BOOK SET isAvail = false WHERE id = '" + mRfidBookId + "'";
            System.out.println(str + " and " + str2);

            if (databaseHandler.execAction(str) && databaseHandler.execAction(str2)) {
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Success");
                alert1.setHeaderText(null);
                alert1.setContentText("Book Issue Complete");

                alert1.showAndWait();
            } else {
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("Failed");
                alert1.setHeaderText(null);
                alert1.setContentText("Issue Operation Failed");
                alert1.showAndWait();
            }
        } else {
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Cancelled");
            alert1.setHeaderText(null);
            alert1.setContentText("Issue Operation cancelled");
            alert1.showAndWait();
        }
        
    }

}
