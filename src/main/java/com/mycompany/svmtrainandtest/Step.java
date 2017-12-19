package com.mycompany.svmtrainandtest;

import javafx.scene.Parent;

public class Step {
    private String xmlFile;
    private StepController controller;
    private Parent parent;

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public Step(String xmlFile, StepController controller) {
        this.xmlFile = xmlFile;
        this.controller = controller;
    }

    public String getXmlFile() {

        return xmlFile;
    }

    public StepController getController() {
        return controller;
    }
}
