package com.mycompany.svmtrainandtest;

import weka.core.Attribute;

/**
 * Created by jakub on 09.06.17.
 */
public class AttributeChoiceBoxWrapper {
    private Attribute attribute;
    private String name;

    public AttributeChoiceBoxWrapper(Attribute attribute, String name) {
        this.attribute = attribute;
        this.name = name;
    }

    public Attribute getAttribute() {

        return attribute;
    }

    @Override
    public String toString() {
        return name;
    }
}
