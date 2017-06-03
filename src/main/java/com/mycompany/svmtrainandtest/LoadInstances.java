package com.mycompany.svmtrainandtest;

import javafx.scene.control.Alert;
import weka.core.Instances;
import weka.core.converters.AbstractFileLoader;
import weka.core.converters.ArffLoader;
import weka.core.converters.CSVLoader;

import java.io.File;
import java.io.IOException;

public class LoadInstances {
    private boolean myResult;
    private String extension;
    private Instances data;

    public LoadInstances(String extension) {
        this.extension = extension;
        this.data = data;
    }

    boolean is() {
        return myResult;
    }

    public Instances getData() {
        return data;
    }

    public LoadInstances invoke(File file) {
        try {
            if (extension.equals("arff")) {
                ArffLoader loader = new ArffLoader();
                data = loadData(loader, file);
            }

            if (extension.equals("csv")) {
                CSVLoader loader = new CSVLoader();
                data = loadData(loader, file);
            }
        } catch(IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText("Błąd wczytywania danych");
            alert.setTitle(e.getMessage());
            alert.showAndWait();
            myResult = true;
            return this;
        }
        myResult = false;
        return this;
    }

    private Instances loadData(AbstractFileLoader loader, File file) throws IOException {
        Instances data;
        loader.setFile(file);
        data = loader.getDataSet();
        return data;
    }
}
