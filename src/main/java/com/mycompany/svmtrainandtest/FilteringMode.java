package com.mycompany.svmtrainandtest;

/**
 * Created by jakub on 07.06.17.
 */
public enum  FilteringMode {
    NORMALIZE,STANDARIZE,DISABLED;

    @Override
    public String toString() {
        switch (this) {
            case DISABLED:
                return "Wyłączony";
            case NORMALIZE:
                return "normalizuj";
            case STANDARIZE:
                return "Standaryzuj";
            default: throw new IllegalArgumentException();
        }
    }
}
