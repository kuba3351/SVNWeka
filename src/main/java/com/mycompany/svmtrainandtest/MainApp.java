package com.mycompany.svmtrainandtest;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import weka.core.Option;
import weka.experiment.InstanceQuery;

import java.util.Enumeration;


public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainWindow.fxml"));
        MainWindowController controller = new MainWindowController(stage);
        loader.setController(controller);
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        
        stage.setTitle("SVN Train and Test");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        for(int i = 0;i<args.length;i++) {
            System.out.println("Parametr "+i+": "+args[i]);
        }
        launch(args);
    }

}
