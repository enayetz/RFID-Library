package library.assistant.ui.main;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import library.assistant.database.DatabaseHandler;


public class Main extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
        
        DatabaseHandler.getInstance();
        myThread th = new myThread();
        Thread thread = new Thread(th);
        thread.start();

//        new Thread(() -> {
//            DatabaseHandler.getInstance();
//        });
        
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    class myThread implements Runnable {

        @Override
        public void run() {
            DatabaseHandler.getInstance();
        }
        
    }
    
}