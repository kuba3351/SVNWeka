package com.mycompany.svmtrainandtest;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.converters.AbstractFileLoader;
import weka.core.converters.ArffLoader;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

public class FXMLController implements Initializable {

    @FXML
    private Button browseButton;

    @FXML
    private Accordion accordion;
    
    @FXML
    private Label trainingStatusLabel;

    @FXML
    private RadioButton testRandomRadio;

    @FXML
    private RadioButton testDiskRadio;

    @FXML
    private Button testOptionsButton;

    @FXML
    private Button testBrowseButton;

    @FXML
    private Button testApplyButton;

    @FXML
    private Label bottomLabel;

    private RandomSettings randomSettings;

    private Stage stage;
    
    private File trainingDataFile;

    private Boolean randomPerformed;

    private File testDataFile;
    
    private Tab tab;
    private Instances trainingInstances = null;
    private Instances testInstances = null;

    private Evaluation result;

    public FXMLController(Stage stage, Tab tab) {
        this.stage = stage;
        this.tab = tab;
    }

    public void setRandomSettings(RandomSettings randomSettings) {
        this.randomSettings = randomSettings;
        if(testRandomRadio.isSelected()) {
            testApplyButton.setDisable(false);
        }
    }

    public RandomSettings getRandomSettings() {
        return randomSettings;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        randomPerformed = false;
        accordion.getPanes().forEach(pane -> pane.setAnimated(true));
        accordion.getPanes().stream().filter(pane -> !pane.getText().equals("Dane treningowe")).forEach(pane -> pane.setDisable(true));
        accordion.setExpandedPane(accordion.getPanes().get(0));
        testOptionsButton.setDisable(true);
        testBrowseButton.setDisable(true);
        testApplyButton.setDisable(true);
        ToggleGroup testGroup = new ToggleGroup();
        testGroup.getToggles().add(testRandomRadio);
        testGroup.getToggles().add(testDiskRadio);
        testGroup.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1) -> {
            testOptionsButton.setDisable(true);
            testBrowseButton.setDisable(true);
            testApplyButton.setDisable(true);
            if(testDiskRadio.isSelected()) {
                testBrowseButton.setDisable(false);
                if(testDataFile != null) {
                    testApplyButton.setDisable(false);
                }
            }
            if(testRandomRadio.isSelected()) {
                testOptionsButton.setDisable(false);
                if(randomSettings != null) {
                    testApplyButton.setDisable(false);
                }
            }
        });
    }

    public void onRunClassiferButtonClick() {
        trainingInstances.setClassIndex(trainingInstances.numAttributes() - 1);
        testInstances.setClassIndex(testInstances.numAttributes() - 1);
        SMO smo = new SMO();
        Evaluation evaluation = null;
        try {
            smo.buildClassifier(trainingInstances);
            evaluation = new Evaluation(trainingInstances);
            evaluation.evaluateModel(smo, testInstances);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText("Błąd klasyfikatora!");
            alert.setTitle(e.getMessage());
            alert.showAndWait();
            return;
        }
        result = evaluation;
        TitledPane wynik = accordion.getPanes().stream().filter(pane -> pane.getText().equals("Wynik")).findFirst().get();
        wynik.setDisable(false);
        accordion.setExpandedPane(wynik);
    }

    public void onResultButtonClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/result.fxml"));
        Stage stage = new Stage();
        ResultController controller = new ResultController(result, stage);
        loader.setController(controller);
        Parent root = loader.load();

        Scene scene = new Scene(root);

        stage.setTitle("Wynik");
        stage.setScene(scene);
        stage.show();
    }

    public void onRandomOptionsButtonClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RandomOptions.fxml"));
        Stage stage = new Stage();
        RandomOptionsController controller = new RandomOptionsController(this, randomPerformed);
        loader.setController(controller);
        Parent root = loader.load();

        Scene scene = new Scene(root);

        stage.setTitle("Opcje losowania");
        stage.setScene(scene);
        stage.show();
    }

    public void onTrainDataViewClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/viewDataWindow.fxml"));
        DataWindowController controller = new DataWindowController(trainingInstances);
        loader.setController(controller);
        Parent root = loader.load();

        Stage stage = new Stage();

        Scene scene = new Scene(root);

        stage.setTitle("Dane treningowe");
        stage.setScene(scene);
        stage.show();
    }

    public void onNextButtonClick() {
        TitledPane classifer = accordion.getPanes().stream().filter(pane -> pane.getText().equals("Klasyfikator SVN")).findFirst().get();
        accordion.setExpandedPane(classifer);
    }

    public void onTestDataViewButtonClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/viewDataWindow.fxml"));
        DataWindowController controller = new DataWindowController(testInstances);
        loader.setController(controller);
        Parent root = loader.load();

        Stage stage = new Stage();
        Scene scene = new Scene(root);

        stage.setTitle("Dane testowe");
        stage.setScene(scene);
        stage.show();
    }

    public void onTestBrowseButtonClick() {
        testDataFile = openFile();
        testApplyButton.setDisable(false);
        bottomLabel.setText("Wybrano plik "+ testDataFile.getName()+" do wczytania.");
    }

    public void onTestApplyButtonClick() {
        if(testDiskRadio.isSelected()) {
            String extension;
            int dot = testDataFile.getName().lastIndexOf(".");
            extension = testDataFile.getName().substring(dot + 1, testDataFile.getName().length());
            LoadInstances loadInstances = new LoadInstances(extension).invoke(testDataFile);
            if (loadInstances.is()) return;
            testInstances = loadInstances.getData();

            TitledPane viewAndEdit = accordion.getPanes().stream().filter(pane -> pane.getText().equals("Wyświetlanie i edycja danych")).findFirst().get();
            accordion.getPanes().stream().filter(pane -> pane.getText().equals("Klasyfikator SVN")).findFirst().get().setDisable(false);
            viewAndEdit.setDisable(false);
            accordion.setExpandedPane(viewAndEdit);
            bottomLabel.setText("Wczytano " + testInstances.numInstances() + " instancji.");
        }
        if(testRandomRadio.isSelected()) {
            if(randomPerformed) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                ButtonType tak = new ButtonType("Tak");
                ButtonType nie = new ButtonType("Nie");
                alert.getButtonTypes().setAll(tak, nie);
                alert.setTitle("Ponowne losowanie");
                alert.setHeaderText("To spowoduje powtórzenie losowania!");
                alert.setContentText("W wyniku powtórzenia losowania zbiory danych mogą się zmienić. Czy chcesz kontynuować?");
                if(alert.showAndWait().get().equals(nie)) return;
            }
            onApplyButtonClick();
            if(randomSettings.getSeed() == null) {
                trainingInstances.randomize(new Random());
            }
            else {
                trainingInstances.randomize(new Random(randomSettings.getSeed()));
            }
            int trainSize = Math.round(trainingInstances.numInstances() * randomSettings.getTestDataPercent() / 100);
            int testSize = trainingInstances.numInstances() - trainSize;
            Instances newTraining = new Instances(trainingInstances, 0, trainSize);
            Instances newTest = new Instances(trainingInstances, trainSize, testSize);
            trainingInstances = newTraining;
            testInstances = newTest;
            TitledPane viewAndEdit = accordion.getPanes().stream().filter(pane -> pane.getText().equals("Wyświetlanie i edycja danych")).findFirst().get();
            accordion.getPanes().stream().filter(pane -> pane.getText().equals("Klasyfikator SVN")).findFirst().get().setDisable(false);
            viewAndEdit.setDisable(false);
            accordion.setExpandedPane(viewAndEdit);
            bottomLabel.setText("Wybrano " + testInstances.numInstances() + " instancji testowych.");
            randomPerformed = true;
        }
    }

    private File openFile() {
        FileChooser chooser = new FileChooser();
        ExtensionFilter arff = new ExtensionFilter("Plik Weka arff", "*.arff");
        ExtensionFilter csv = new ExtensionFilter("Plik z danymi CSV", "*.csv");
        chooser.getExtensionFilters().add(arff);
        chooser.getExtensionFilters().add(csv);
        return chooser.showOpenDialog(stage);
    }

    public void onBrowseButtonClick() {
        trainingDataFile = openFile();
        trainingStatusLabel.setText("Wybrano plik "+ trainingDataFile.getName()+" do wczytania.");
        onApplyButtonClick();
    }
    public void onApplyButtonClick() {
        String extension;
        int dot = trainingDataFile.getName().lastIndexOf(".");
        extension = trainingDataFile.getName().substring(dot + 1, trainingDataFile.getName().length());
        LoadInstances loadInstances = new LoadInstances(extension).invoke(trainingDataFile);
        if (loadInstances.is()) return;
        trainingInstances = loadInstances.getData();

        TitledPane testPane = accordion.getPanes().stream().filter(pane -> pane.getText().equals("Dane testowe")).findFirst().get();
        testPane.setDisable(false);
        accordion.setExpandedPane(testPane);
        tab.setText(trainingDataFile.getName());
        trainingStatusLabel.setText("Wczytano "+ trainingInstances.numInstances()+" instancji.");
    }

    private Instances loadData(AbstractFileLoader loader, File file) throws IOException {
        Instances data;
        loader.setFile(file);
        data = loader.getDataSet();
        return data;
    }

    private class LoadInstances {
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
    }
}
