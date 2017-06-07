package com.mycompany.svmtrainandtest;

/**
 * Created by jakub on 07.06.17.
 */
public class ClassifierSettings {
    private Boolean debugMode;
    private String epsilon;
    private FilteringMode filteringMode;
    private KernelType kernelType;
    private Integer numDecimalPlaces;

    public ClassifierSettings() {
        debugMode = false;
        epsilon = "1.0E-12";
        filteringMode = FilteringMode.NORMALIZE;
        kernelType = KernelType.POLY;
        numDecimalPlaces = 2;
    }

    public Boolean getDebugMode() {
        return debugMode;
    }

    public void setDebugMode(Boolean debugMode) {
        this.debugMode = debugMode;
    }

    public String getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(String epsilon) {
        this.epsilon = epsilon;
    }

    public FilteringMode getFilteringMode() {
        return filteringMode;
    }

    public void setFilteringMode(FilteringMode filteringMode) {
        this.filteringMode = filteringMode;
    }

    public KernelType getKernelType() {
        return kernelType;
    }

    public void setKernelType(KernelType kernelType) {
        this.kernelType = kernelType;
    }

    public Integer getNumDecimalPlaces() {
        return numDecimalPlaces;
    }

    public void setNumDecimalPlaces(Integer numDecimalPlaces) {
        this.numDecimalPlaces = numDecimalPlaces;
    }
}
