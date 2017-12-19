package com.mycompany.svmtrainandtest;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.*;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.Tag;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.ResourceBundle;

/**
 * Created by jakub on 02.06.17.
 */
public class ResultController implements StepController {

    @FXML
    private TextArea resultArea;

    private Evaluation evaluation;

    private StringBuilder resultString;

    private static ResultController instance;

    public static ResultController getInstance() {
        if(instance == null)
            instance = new ResultController();
        return instance;
    }

    private ResultController() {

    }

    @Override
    public boolean canGoBack() {
        return true;
    }

    @Override
    public boolean canGoNext() {
        return false;
    }

    public void runWhileEnterStep() {
        TrainDataWindowController trainDataWindowController = TrainDataWindowController.getInstence();
        ClassifierSettingsController classifierSettingsController = ClassifierSettingsController.getInstance();
        Instances trainInstances = trainDataWindowController.getInstances();
        Instances testInstances = trainDataWindowController.getTestInstances();
        Enumeration attributes = trainInstances.enumerateAttributes();
        Attribute decision = null;
        while (attributes.hasMoreElements()) {
            Object attribute = attributes.nextElement();
            if (attribute.toString().contains(classifierSettingsController.getDecision().getValue())) {
                decision = (Attribute)attribute;
            }
        }
        trainInstances.setClass(decision);
        testInstances.setClass(decision);
        SMO smo = new SMO();
        smo.setDebug(classifierSettingsController.getDebugCheckBox().isSelected());
        smo.setEpsilon(Double.parseDouble(classifierSettingsController.getEpsilonField().getText()));
        SelectedTag tag = smo.getFilterType();
        Tag[] tags = tag.getTags();
        Tag normalize = null;
        Tag standarize = null;
        Tag disabled = null;
        for(Tag tag1 : tags) {
            if(tag1.getReadable().equals("Normalize training data"))
                normalize = tag1;
            else if(tag1.getReadable().equals("Standardize training data"))
                standarize = tag1;
            else if(tag1.getReadable().equals("No normalization/standardization"))
                disabled = tag1;
        }
        switch (classifierSettingsController.getFilterModeChoiceBox().getValue()) {
            case DISABLED:
                smo.setFilterType(new SelectedTag(disabled.getID(), tags));
                break;
            case NORMALIZE:
                smo.setFilterType(new SelectedTag(normalize.getID(), tags));
                break;
            case STANDARIZE:
                smo.setFilterType(new SelectedTag(standarize.getID(), tags));
                break;
        }
        switch (classifierSettingsController.getKernelChoiceBox().getValue()) {
            case PUK:
                smo.setKernel(new Puk());
                break;
            case RBF:
                smo.setKernel(new RBFKernel());
                break;
            case POLY:
                smo.setKernel(new PolyKernel());
                break;
            case STRING:
                smo.setKernel(new StringKernel());
                break;
            case NORMALIZED_POLY:
                smo.setKernel(new NormalizedPolyKernel());
                break;
        }
        try {
            smo.buildClassifier(trainInstances);
            evaluation = new Evaluation(trainInstances);
            evaluation.evaluateModel(smo, testInstances);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        File file = chooser.showSaveDialog(new Stage());
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
