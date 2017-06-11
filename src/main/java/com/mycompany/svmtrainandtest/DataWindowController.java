package com.mycompany.svmtrainandtest;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;

import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by jakub on 29.05.17.
 */
public class DataWindowController implements Initializable {

    @FXML
    private TableView<Instance> dataTable;

    private Instances instances;

    public DataWindowController(Instances instances) {
        this.instances = instances;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dataTable.setEditable(true);
        dataTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ObservableList columns = dataTable.getColumns();
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
                    Instance instance = instances.get(position.getRow());
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
    }
    public void onAddButtonClick() {
        Instance instance = new DenseInstance(instances.numAttributes());
        instances.add(instance);
        dataTable.getItems().add(instance);
    }
    public void onRemoveButtonClick() {
        List<Instance> instances1 = dataTable.getSelectionModel().getSelectedItems();
        instances1.forEach(instance -> instances.remove(instance));
        dataTable.getItems().removeAll(instances1);
    }
}
