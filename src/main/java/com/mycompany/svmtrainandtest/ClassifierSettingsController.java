package com.mycompany.svmtrainandtest;

/**
 * Created by jakub on 07.06.17.
 */
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.net.URL;
import java.util.ResourceBundle;

public class ClassifierSettingsController implements Initializable {

    @FXML
    private CheckBox debugCheckBox;

    @FXML
    private TextField epsilonField;

    @FXML
    private ChoiceBox<FilteringMode> filterModeChoiceBox;

    @FXML
    private ChoiceBox<KernelType> kernelChoiceBox;

    @FXML
    private Spinner<Integer> numDecimalSpinner;

    private ClassifierSettings classifierSettings;

    public ClassifierSettingsController(ClassifierSettings classifierSettings) {
        this.classifierSettings = classifierSettings;
    }

    public void onCancelButtonClick() {
        Stage window = (Stage)numDecimalSpinner.getScene().getWindow();
        window.close();
    }

    public void onOkButtonClick() {
        classifierSettings.setDebugMode(debugCheckBox.isSelected());
        classifierSettings.setEpsilon(epsilonField.getText());
        classifierSettings.setFilteringMode(filterModeChoiceBox.getSelectionModel().getSelectedItem());
        classifierSettings.setKernelType(kernelChoiceBox.getSelectionModel().getSelectedItem());
        classifierSettings.setNumDecimalPlaces(numDecimalSpinner.getValue());
        Stage window = (Stage)numDecimalSpinner.getScene().getWindow();
        window.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        debugCheckBox.setSelected(classifierSettings.getDebugMode());
        epsilonField.setText(classifierSettings.getEpsilon());
        filterModeChoiceBox.getItems().add(FilteringMode.NORMALIZE);
        filterModeChoiceBox.getItems().add(FilteringMode.STANDARIZE);
        filterModeChoiceBox.getItems().add(FilteringMode.DISABLED);
        filterModeChoiceBox.getSelectionModel().select(classifierSettings.getFilteringMode());
        kernelChoiceBox.getItems().add(KernelType.NORMALIZED_POLY);
        kernelChoiceBox.getItems().add(KernelType.POLY);
        kernelChoiceBox.getItems().add(KernelType.PUK);
        kernelChoiceBox.getItems().add(KernelType.RBF);
        kernelChoiceBox.getItems().add(KernelType.STRING);
        kernelChoiceBox.getSelectionModel().select(classifierSettings.getKernelType());
        SpinnerValueFactory factory = new SpinnerValueFactory() {
            @Override
            public void decrement(int i) {
                if((int)this.getValue() > 1)
                    this.setValue((int)this.getValue() - 1);
            }

            @Override
            public void increment(int i) {
                if((int) this.getValue() < 30) {
                    this.setValue((int)this.getValue() + 1);
                }
            }
        };
        numDecimalSpinner.setValueFactory(factory);
        numDecimalSpinner.getValueFactory().setValue(classifierSettings.getNumDecimalPlaces());
    }
}

