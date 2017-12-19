package com.mycompany.svmtrainandtest;

/**
 * Created by jakub on 07.06.17.
 */
public enum KernelType {
    NORMALIZED_POLY, POLY, PUK, RBF, STRING;

    @Override
    public String toString() {
        switch (this) {
            case PUK: return "PUK";
            case RBF: return "RBF";
            case POLY: return "POLY";
            case STRING: return "STRING";
            case NORMALIZED_POLY: return "NORMALIZED POLY";
            default: throw new IllegalArgumentException();
        }
    }
}
