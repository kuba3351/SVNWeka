package com.mycompany.svmtrainandtest;

import com.splunk.Event;
import com.splunk.Job;
import com.splunk.JobArgs;
import com.splunk.ResultsReaderXml;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import weka.core.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by jakub on 29.05.17.
 */
public class TestDataWindowController implements StepController {

    @FXML
    private TableView<Instance> dataTable;

    private Instances instances;

    @FXML
    private Label infoLabel;

    private static TestDataWindowController instance;

    public static TestDataWindowController getInstance() {
        if(instance == null)
            instance = new TestDataWindowController();
        return instance;
    }

    private TestDataWindowController() {

    }

    @Override
    public void runWhileEnterStep() throws Exception {
        try {
            Platform.runLater(() -> {
                MainWindowController mainWindowController = MainWindowController.getInstance();
                mainWindowController.getLoadingLabel().setText("Ładowanie danych z bazy splunk...");
                mainWindowController.getProgressBar().setProgress(-1);
            });
            instances = TrainDataWindowController.getInstence().getTestInstances();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Błąd wczytywania danych!");
        }
        Platform.runLater(() -> {
            infoLabel.setText("Sprawdź i edytuj dane testowe wprowadzone do aplikacji");
            dataTable.setEditable(true);
            dataTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            ObservableList columns = dataTable.getColumns();
            dataTable.getItems().clear();
            columns.clear();
            Enumeration<Attribute> iterator = instances.enumerateAttributes();
            while (iterator.hasMoreElements()) {
                Attribute attribute = iterator.nextElement();
                TableColumn column = new TableColumn(attribute.name());
                column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
                    @Override
                    public ObservableValue call(TableColumn.CellDataFeatures cellDataFeatures) {
                        Instance instance = (Instance) cellDataFeatures.getValue();
                        if(attribute.isDate() || attribute.isNominal() || attribute.isString())
                            return new SimpleStringProperty(instance.stringValue(attribute));
                        else if(attribute.isNumeric()) {
                            return new SimpleStringProperty(String.valueOf(instance.value(attribute)));
                        }
                        else return new SimpleStringProperty("-");
                    }
                });
                column.setCellFactory(TextFieldTableCell.forTableColumn());
                column.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
                    public void handle(TableColumn.CellEditEvent cellEditEvent) {
                        TablePosition position = cellEditEvent.getTablePosition();
                        Instance instance = instances.instance(position.getRow());
                        Attribute attribute1 = instance.attribute(position.getColumn());
                        try {
                            if (attribute1.isNominal() || attribute1.isString())
                                instance.setValue(position.getColumn(), cellEditEvent.getNewValue().toString());
                            else if (attribute1.isNumeric()) {
                                instance.setValue(position.getColumn(), Double.parseDouble(cellEditEvent.getNewValue().toString()));
                            }
                        } catch (Exception e) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Błąd");
                            alert.setHeaderText("Błąd edycji danych");
                            alert.setContentText("Ta wartość jest nieprawidłowa dla tego atrybutu");
                            alert.showAndWait();
                        }
                    }
                });
                columns.add(column);
            }
            Enumeration<Instance> instanceEnumeration = instances.enumerateInstances();
            while (instanceEnumeration.hasMoreElements()) {
                dataTable.getItems().add(instanceEnumeration.nextElement());
            }
        });
    }
    public void onAddButtonClick() {
        Instance instance = new SparseInstance(instances.numAttributes());
        instances.add(instance);
        dataTable.getItems().add(instance);
    }
    public void onRemoveButtonClick() {
        List<Instance> instances1 = dataTable.getSelectionModel().getSelectedItems();
        //instances1.forEach(instance -> instances.remove(instance));
        dataTable.getItems().removeAll(instances1);
    }

    @Override
    public boolean canGoBack() {
        return true;
    }

    @Override
    public boolean canGoNext() {
        return true;
    }

    private Instances loadDataFromSplunk(String source) throws IOException {
        String searchQuery_normal = "search source="+source+" | outputtext | fields - _*, splunk_server, source, host, sourcetype, punct, timestamp, index, linecount, eventtype, splunk_server_group";
        JobArgs jobargs = new JobArgs();
        jobargs.setExecutionMode(JobArgs.ExecutionMode.NORMAL);
        Job job = PrepareDataStepController.getInstance().getService().getJobs().create(searchQuery_normal, jobargs);

        while (!job.isDone()) {

        }

        InputStream resultsNormalSearch = job.getResults();
        ResultsReaderXml resultsReaderXml = new ResultsReaderXml(resultsNormalSearch);
        ArrayList<Attribute> attributes = new ArrayList<>();
        List<String> fields = new ArrayList<>(resultsReaderXml.getFields());
        List<Event> events = new ArrayList<>();
        for(Event event : resultsReaderXml) {
            events.add(event);
        }
        fields.forEach((field -> {
            Attribute attribute;
            if(events.stream().filter(event -> event.get(field) != null).map(event -> event.get(field)).allMatch(values -> values.matches("[0-9]+")))
                attribute = new Attribute(field);
            else {
                attribute = new Attribute(field, (FastVector)null);
            }
            attributes.add(attribute);
        }));
        FastVector fastVector = new FastVector();
        attributes.forEach(fastVector::addElement);
        Instances instances = new Instances(source, fastVector, events.size());
        events.stream().filter(event -> event.size() != 0).forEach((event) -> {
            double[] attrValues = new double[attributes.size()];
            for(int i = 0;i<attrValues.length;i++) {
                if(instances.attribute(i).isNumeric()) {
                    attrValues[i] = Double.parseDouble(event.get(fields.get(i)));
                }
                else attrValues[i] = instances.attribute(i).addStringValue(event.get(fields.get(i)));
            }
            instances.add(new SparseInstance(1.0, attrValues));
        });
        return instances;
    }
}
