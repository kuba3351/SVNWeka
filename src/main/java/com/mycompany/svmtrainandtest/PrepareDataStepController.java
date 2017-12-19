package com.mycompany.svmtrainandtest;

import com.splunk.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PrepareDataStepController implements StepController{

    @FXML
    private RadioButton testRandomRadio;

    @FXML
    private RadioButton testDiskRadio;

    @FXML
    private Slider percentSlider;

    @FXML
    private CheckBox seedCheckBox;

    @FXML
    private TextField seedField;

    @FXML
    private Label percentLabel;
    private List<SplunkDataSource> dataSources;
    private Service service;

    public Service getService() {
        return service;
    }

    public ChoiceBox<SplunkDataSource> getTrainData() {
        return trainData;
    }

    public ChoiceBox<SplunkDataSource> getTestData() {
        return testData;
    }

    public RadioButton getTestDiskRadio() {

        return testDiskRadio;
    }

    @FXML
    private ChoiceBox<SplunkDataSource> trainData;

    @FXML
    private ChoiceBox<SplunkDataSource> testData;

    private static PrepareDataStepController instance;

    public Slider getPercentSlider() {
        return percentSlider;
    }

    public CheckBox getSeedCheckBox() {
        return seedCheckBox;
    }

    public TextField getSeedField() {
        return seedField;
    }

    public static PrepareDataStepController getInstance() {
        if(instance == null)
            instance = new PrepareDataStepController();
        return instance;
    }

    private PrepareDataStepController() {

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
    public void runWhileEnterStep() throws Exception {
        trainData.getItems().clear();
        testData.getItems().clear();
        Platform.runLater(() -> {
            MainWindowController mainWindowController = MainWindowController.getInstance();
            mainWindowController.getLoadingLabel().setText("Łączenie z bazą danych Splunk...");
            mainWindowController.getProgressBar().setProgress(-1);
        });
        DatabaseConnectController databaseConnectController = DatabaseConnectController.getInstance();
        try {
            HttpService.setSslSecurityProtocol(SSLSecurityProtocol.TLSv1_2);

            ServiceArgs loginArgs = new ServiceArgs();
            loginArgs.setUsername(databaseConnectController.getLogin().getText());
            loginArgs.setPassword(databaseConnectController.getPassword().getText());
            loginArgs.setHost(databaseConnectController.getHost().getText());
            loginArgs.setPort(Integer.parseInt(databaseConnectController.getPort().getText()));

            service = Service.connect(loginArgs);

            String searchQuery_normal = "| metadata type=sources *";
            JobArgs jobargs = new JobArgs();
            jobargs.setExecutionMode(JobArgs.ExecutionMode.NORMAL);
            Job job = service.getJobs().create(searchQuery_normal, jobargs);

            while (!job.isDone()) {

            }

            InputStream resultsNormalSearch = job.getResults();
            ResultsReaderXml resultsReaderXml = new ResultsReaderXml(resultsNormalSearch);
            dataSources = new ArrayList<>();
            resultsReaderXml.forEach(event -> {
                SplunkDataSource splunkDataSource = new SplunkDataSource();
                splunkDataSource.setName(event.get("source"));
                splunkDataSource.setTotalCount(event.get("totalCount"));
                dataSources.add(splunkDataSource);
            });
            if(dataSources.isEmpty()) {
                throw new Exception("NO_DATA");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage().equals("NO_DATA") ? "Baza danych Spunk nie zawiera danych do wczytania" : "Nie udało się połączyć z bazą danych Splunk");
        }
        Platform.runLater(() -> {
            percentLabel.setText("0%");
            percentSlider.valueProperty().addListener((observableValue, number, t1) -> percentLabel.setText(t1.intValue()+"%"));
            trainData.getItems().addAll(dataSources);
            testData.getItems().addAll(dataSources);
            trainData.setValue(dataSources.get(0));
            testData.setValue(dataSources.get(0));
            ToggleGroup toggleGroup = new ToggleGroup();
            toggleGroup.getToggles().addAll(testDiskRadio, testRandomRadio);
            testDiskRadio.selectedProperty().addListener((observableValue, aBoolean, t1) -> testData.setDisable(!t1));
            testDiskRadio.setSelected(true);
            testRandomRadio.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
                seedCheckBox.setDisable(!t1);
                percentSlider.setDisable(!t1);
            });
            testRandomRadio.setSelected(false);
            seedCheckBox.selectedProperty().addListener((observableValue, aBoolean, t1) -> seedField.setDisable(!t1));
            seedCheckBox.setSelected(false);
            percentSlider.setDisable(true);
            seedField.setDisable(true);
            seedField.setDisable(true);
            seedCheckBox.setDisable(true);
        });
    }
}
