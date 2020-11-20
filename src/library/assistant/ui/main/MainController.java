package library.assistant.ui.main;

import com.jfoenix.effects.JFXDepthManager;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.database.DatabaseHandler;

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
            if(!flag) {
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

}
