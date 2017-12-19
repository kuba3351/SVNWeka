package com.mycompany.svmtrainandtest;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainWindowController implements Initializable {

    private static MainWindowController instance;

    @FXML
    private SubScene content;

    @FXML
    private Button back;

    @FXML
    private Button next;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label loadingLabel;

    @FXML
    private Label bottomLabel;

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public Label getLoadingLabel() {
        return loadingLabel;
    }

    private int currentStepNumber;
    private List<Step> steps;

    private MainWindowController() {

    }

    public static MainWindowController getInstance() {
        if (instance == null)
            instance = new MainWindowController();
        return instance;
    }

    public void onNextButtonClick() throws Exception {
        content.setVisible(false);
        progressBar.setVisible(true);
        loadingLabel.setVisible(true);
        back.setDisable(true);
        next.setDisable(true);
        currentStepNumber++;
        bottomLabel.setText(String.format("Krok %d z 6", currentStepNumber+1));
        Step step = steps.get(currentStepNumber);
        content.setRoot(step.getParent());
        Thread thread = new Thread(() -> {
            try {
                step.getController().runWhileEnterStep();
                Platform.runLater(() -> {
                    content.setVisible(true);
                    progressBar.setVisible(false);
                    loadingLabel.setVisible(false);
                    next.setDisable(!step.getController().canGoNext());
                    back.setDisable(!step.getController().canGoBack());
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    progressBar.setVisible(false);
                    loadingLabel.setText(e.getMessage()+"\nProszę wrócić wstecz!");
                    next.setDisable(true);
                    back.setDisable(false);
                });
            }
        });
        thread.start();
    }

    public void onPreviousButtonClick() {
        currentStepNumber--;
        bottomLabel.setText(String.format("Krok %d z 6", currentStepNumber+1));
        Step step = steps.get(currentStepNumber);
        content.setRoot(step.getParent());
        content.setVisible(true);
        loadingLabel.setVisible(false);
        next.setDisable(!step.getController().canGoNext());
        back.setDisable(!step.getController().canGoBack());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        progressBar.setVisible(false);
        loadingLabel.setVisible(false);
        steps = new ArrayList<>();
        steps.add(new Step("/fxml/splunkForm.fxml", DatabaseConnectController.getInstance()));
        steps.add(new Step("/fxml/trainDataSelection.fxml", PrepareDataStepController.getInstance()));
        steps.add(new Step("/fxml/viewDataWindow.fxml", TrainDataWindowController.getInstence()));
        steps.add(new Step("/fxml/viewDataWindow.fxml", TestDataWindowController.getInstance()));
        steps.add(new Step("/fxml/classifierConfig.fxml", ClassifierSettingsController.getInstance()));
        steps.add(new Step("/fxml/result.fxml", ResultController.getInstance()));
        steps.forEach(step -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(step.getXmlFile()));
            loader.setController(step.getController());
            try {
                step.setParent(loader.load());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        currentStepNumber = 0;
        Step step = steps.get(currentStepNumber);
        content.setRoot(step.getParent());
        next.setDisable(!step.getController().canGoNext());
        back.setDisable(!step.getController().canGoBack());
    }
}
