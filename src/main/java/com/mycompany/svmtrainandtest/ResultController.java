package com.mycompany.svmtrainandtest;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import weka.classifiers.Evaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by jakub on 02.06.17.
 */
public class ResultController implements Initializable {

    @FXML
    private TextArea resultArea;

    private Stage stage;

    private Evaluation evaluation;

    private StringBuilder resultString;

    public ResultController(Evaluation evaluation, Stage stage) {
        this.evaluation = evaluation;
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        StringBuilder builder = new StringBuilder();
        try {
            builder.append(evaluation.toClassDetailsString("Klasy decyzyjne:"));
            builder.append("\n\n");
            builder.append(evaluation.toCumulativeMarginDistributionString());
            builder.append("\n\n");
            builder.append(evaluation.toMatrixString("Macierz:"));
            builder.append("\n\n");
            builder.append(evaluation.toSummaryString("Podsumowanie:", false));
            resultArea.setEditable(false);
            resultArea.setText(builder.toString());
            resultString = builder;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onFileSaveButtonClick() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Plik tekstowy txt", ".txt"));
        File file = chooser.showSaveDialog(stage);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(resultString.toString());
            writer.close();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText("Błąd zapisu pliku");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
