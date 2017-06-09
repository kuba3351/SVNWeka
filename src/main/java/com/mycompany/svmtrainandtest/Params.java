package com.mycompany.svmtrainandtest;

/**
 * Created by jakub on 03.06.17.
 */
public class Params {
    private String trainingDataFileName;
    private String trainingDataClass;
    private String testDataFileName;
    private Integer testDataPercent;
    private Integer testDataSeed;
    private String resultFile;
    private Boolean debug;
    private String epsilon;
    private FilteringMode filteringMode;
    private KernelType kernelType;
    private Integer numDecimal;

    public Params() {
        debug = false;
    }

    public Boolean getDebug() {
        return debug;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
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

    public Integer getNumDecimal() {
        return numDecimal;
    }

    public void setNumDecimal(Integer numDecimal) {
        this.numDecimal = numDecimal;
    }

    public String getTrainingDataFileName() {
        return trainingDataFileName;
    }

    public void setTrainingDataFileName(String trainingDataFileName) {
        this.trainingDataFileName = trainingDataFileName;
    }

    public String getTrainingDataClass() {
        return trainingDataClass;
    }

    public void setTrainingDataClass(String trainingDataClass) {
        this.trainingDataClass = trainingDataClass;
    }

    public String getTestDataFileName() {
        return testDataFileName;
    }

    public void setTestDataFileName(String testDataFileName) {
        this.testDataFileName = testDataFileName;
    }

    public Integer getTestDataPercent() {
        return testDataPercent;
    }

    public void setTestDataPercent(Integer testDataPercent) {
        this.testDataPercent = testDataPercent;
    }

    public Integer getTestDataSeed() {
        return testDataSeed;
    }

    public void setTestDataSeed(Integer testDataSeed) {
        this.testDataSeed = testDataSeed;
    }

    public String getResultFile() {
        return resultFile;
    }

    public void setResultFile(String resultFile) {
        this.resultFile = resultFile;
    }

    public void validateParams() throws Exception {
        if(trainingDataFileName == null) {
            throw new Exception("trainData.file not set!");
        }
        if(testDataFileName == null && testDataPercent == null) {
            throw new Exception("testData.file or testData.percent not set");
        }

    }
}
