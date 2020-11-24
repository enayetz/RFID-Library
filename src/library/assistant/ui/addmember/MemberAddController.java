package library.assistant.ui.addmember;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import library.assistant.database.DatabaseHandler;
import library.assistant.ui.rfid.Read;

public class MemberAddController implements Initializable {

    @FXML
    private JFXTextField name;
    @FXML
    private JFXTextField id;
    @FXML
    private JFXTextField mobile;
    @FXML
    private JFXTextField email;
    @FXML
    private JFXButton saveButton;
    @FXML
    private JFXButton cancelButton;

    DatabaseHandler databaseHandler;

    @FXML
    private AnchorPane memberRootPane;
    @FXML
    private JFXTextField epcCode;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();
    }

    @FXML
    private void addMember(ActionEvent event) {
        String mName = name.getText();
        String mId = id.getText();
        String mMobile = mobile.getText();
        String mEmail = email.getText();
        String mEpc = epcCode.getText();

        Boolean flag = mEpc.isEmpty() || mName.isEmpty() || mId.isEmpty() || mMobile.isEmpty() || mEmail.isEmpty();
        if (flag) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Enter in all fields");
            alert.showAndWait();
            return;
        }

        String st = "INSERT INTO MEMBER VALUES ("
                + "'" + mId + "',"
                + "'" + mEpc + "',"
                + "'" + mName + "',"
                + "'" + mMobile + "',"
                + "'" + mEmail + "'"
                + ")";
        System.out.println(st);

        if (databaseHandler.execAction(st)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Saved member information");
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
    private void cancelMember(ActionEvent event) {
        Stage stage = (Stage) memberRootPane.getScene().getWindow();
        stage.close();
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
