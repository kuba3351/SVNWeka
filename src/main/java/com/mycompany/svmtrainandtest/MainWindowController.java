package com.mycompany.svmtrainandtest;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jakub
 */
public class MainWindowController implements Initializable {

    @FXML
    private TabPane tabPane;

    private Stage stage;

    public MainWindowController(Stage stage) {
        this.stage = stage;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ChangeListener<Tab> listener = new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {
                if (t1.getText().equals("+")) {
                    Parent newContent = null;
                    Tab newTab = new Tab();
                    newTab.setText("Bez nazwy");
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/tab.fxml"));
                        TabController controller = new TabController(stage, newTab);
                        loader.setController(controller);
                        newContent = loader.load();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    newTab.setContent(newContent);
                    int numberOfTabs = tabPane.getTabs().size();
                    tabPane.getTabs().add(numberOfTabs - 1, newTab);
                    tabPane.getSelectionModel().select(newTab);
                }
            }
        };
        tabPane.getSelectionModel().selectedItemProperty().addListener(listener);
        listener.changed(null, null, tabPane.getTabs().get(0));
    }
}
