package com.mycompany.svmtrainandtest;

/**
 * Created by jakub on 03.06.17.
 */
public class Params {
    private String trainingDataFileName;
    private String trainingDataClass;
    private String testDataFileName;
    private String testDataClass;
    private Integer testDataPercent;
    private Integer testDataSeed;
    private String resultFile;

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

    public String getTestDataClass() {
        return testDataClass;
    }

    public void setTestDataClass(String testDataClass) {
        this.testDataClass = testDataClass;
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
