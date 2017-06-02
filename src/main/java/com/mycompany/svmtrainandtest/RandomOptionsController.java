package com.mycompany.svmtrainandtest;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by jakub on 02.06.17.
 */
public class RandomOptionsController implements Initializable {

    @FXML
    private Slider percentSlider;

    @FXML
    private CheckBox seedCheckBox;

    @FXML
    private TextField seedField;

    @FXML
    private Label percentLabel;

    private FXMLController controller;
    private RandomSettings randomSettings;
    private Boolean randomPerformed;

    public RandomOptionsController(FXMLController controller, Boolean randomPerformed) {
        this.controller = controller;
        this.randomPerformed = randomPerformed;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        percentSlider.setMin(1);
        percentSlider.setMax(99);
        randomSettings = controller.getRandomSettings();
        if(randomSettings == null) {
            percentSlider.adjustValue(1);
            seedCheckBox.setSelected(false);
            seedField.setDisable(true);
        }
        else {
            percentSlider.adjustValue(randomSettings.getTestDataPercent());
            if(randomSettings.getSeed() == null) {
                seedCheckBox.setSelected(false);
                seedField.setDisable(true);
            }
            else {
                seedCheckBox.setSelected(true);
                seedField.setText(randomSettings.getSeed().toString());
            }
        }
        percentLabel.setText(Long.valueOf(Math.round(percentSlider.getValue())).toString()+"%");
        percentSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                percentLabel.setText(Long.valueOf(Math.round(t1.doubleValue())).toString()+"%");
            }
        });
    }
    public void onOkButtonClick() throws Exception {
        if(randomPerformed) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Informacja");
            alert.setHeaderText("Wymagane powtórne losowanie");
            alert.setContentText("Aby zastosować nowe opcje, wymagane jest powtórne losowanie. Aby to zrobić, kliknij przycisk 'Dalej' w głównym oknie programu.");
            alert.showAndWait();
        }
        if(randomSettings == null) {
            randomSettings = new RandomSettings();
        }
        randomSettings.setTestDataPercent(Long.valueOf(Math.round(percentSlider.getValue())).intValue());
        if(seedCheckBox.isSelected()) {
            randomSettings.setSeed(Integer.parseInt(seedField.getText()));
        }
        controller.setRandomSettings(randomSettings);
        Stage stage = (Stage)percentLabel.getScene().getWindow();
        stage.close();
    }

    public void onCancelButtonClick() {
        Stage stage = (Stage)percentLabel.getScene().getWindow();
        stage.close();
    }

    public void onSeedCheckBoxChange() {
        seedField.setDisable(!seedCheckBox.isSelected());
    }
}
