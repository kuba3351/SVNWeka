package com.mycompany.svmtrainandtest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;


public class MainApp extends Application {

    final static Logger logger = Logger.getLogger(MainApp.class);

    @Override
    public void start(Stage stage) throws Exception {
        stage.setResizable(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainWindow.fxml"));
        MainWindowController controller = MainWindowController.getInstance();
        loader.setController(controller);
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        
        stage.setTitle("SVN Train and Test");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
