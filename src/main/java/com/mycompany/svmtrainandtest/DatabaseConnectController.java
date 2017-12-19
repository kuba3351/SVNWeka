package com.mycompany.svmtrainandtest;

import com.splunk.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.util.List;

public class DatabaseConnectController implements StepController {
    @FXML
    private AnchorPane onOkButtonClick;

    public TextField getHost() {
        return host;
    }

    public TextField getPort() {
        return port;
    }

    public TextField getLogin() {
        return login;
    }

    public PasswordField getPassword() {
        return password;
    }

    @FXML
    private TextField host;

    @FXML
    private TextField port;

    @FXML
    private TextField login;

    @FXML
    private PasswordField password;

    private static DatabaseConnectController instance;

    public static DatabaseConnectController getInstance() {
        if(instance == null)
            instance = new DatabaseConnectController();
        return instance;
    }

    private DatabaseConnectController() {

    }

    @Override
    public void runWhileEnterStep() {

    }

    @Override
    public boolean canGoBack() {
        return false;
    }

    @Override
    public boolean canGoNext() {
        return true;
    }
}
