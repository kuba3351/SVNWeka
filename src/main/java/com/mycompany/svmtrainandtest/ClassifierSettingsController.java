package com.mycompany.svmtrainandtest;

/**
 * Created by jakub on 07.06.17.
 */
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import weka.core.Attribute;
import weka.core.Instances;

import java.net.URL;
import java.util.Enumeration;
import java.util.ResourceBundle;

public class ClassifierSettingsController implements StepController {

    @FXML
    private CheckBox debugCheckBox;

    @FXML
    private TextField epsilonField;

    @FXML
    private ChoiceBox<FilteringMode> filterModeChoiceBox;

    @FXML
    private ChoiceBox<KernelType> kernelChoiceBox;

    public CheckBox getDebugCheckBox() {
        return debugCheckBox;
    }

    public TextField getEpsilonField() {
        return epsilonField;
    }

    public ChoiceBox<FilteringMode> getFilterModeChoiceBox() {
        return filterModeChoiceBox;
    }

    public ChoiceBox<KernelType> getKernelChoiceBox() {
        return kernelChoiceBox;
    }

    public ChoiceBox<String> getDecision() {
        return decision;
    }

    @FXML
    private ChoiceBox<String> decision;

    private static ClassifierSettingsController instance;

    public static ClassifierSettingsController getInstance() {
        if(instance == null)
            instance = new ClassifierSettingsController();
        return instance;
    }

    private ClassifierSettingsController() {

    }

    @Override
    public boolean canGoBack() {
        return true;
    }

    @Override
    public boolean canGoNext() {
        return true;
    }

    @Override
    public void runWhileEnterStep() {
        Platform.runLater(() -> {
            MainWindowController mainWindowController = MainWindowController.getInstance();
            mainWindowController.getLoadingLabel().setText("Uruchamiam klasyfikator...");
        });
        filterModeChoiceBox.getItems().add(FilteringMode.NORMALIZE);
        filterModeChoiceBox.getItems().add(FilteringMode.STANDARIZE);
        filterModeChoiceBox.getItems().add(FilteringMode.DISABLED);
        kernelChoiceBox.getItems().add(KernelType.NORMALIZED_POLY);
        kernelChoiceBox.getItems().add(KernelType.POLY);
        kernelChoiceBox.getItems().add(KernelType.PUK);
        kernelChoiceBox.getItems().add(KernelType.RBF);
        kernelChoiceBox.getItems().add(KernelType.STRING);
        epsilonField.setText("1.0E-12");
        filterModeChoiceBox.setValue(FilteringMode.NORMALIZE);
        kernelChoiceBox.setValue(KernelType.POLY);
        decision.getItems().clear();
        Enumeration attributes = TrainDataWindowController.getInstence().getTestInstances().enumerateAttributes();
        while(attributes.hasMoreElements()) {
            decision.getItems().add(attributes.nextElement().toString().split(" ")[1]);
        }
        decision.setValue(decision.getItems().get(0));
    }
}