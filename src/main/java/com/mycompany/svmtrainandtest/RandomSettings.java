package com.mycompany.svmtrainandtest;

/**
 * Created by jakub on 02.06.17.
 */
public class RandomSettings {
    private Integer testDataPercent;
    private Integer seed;

    public RandomSettings() {
        this.testDataPercent = 0;
    }

    public Integer getTestDataPercent() {
        return testDataPercent;
    }

    public void setTestDataPercent(Integer testDataPercent) throws Exception {
        if(testDataPercent > 0 && testDataPercent < 100) {
            this.testDataPercent = testDataPercent;
        }
        else throw new Exception("Nieprawidłowy procent danych testowych!");
    }

    public Integer getSeed() {
        return seed;
    }

    public void setSeed(Integer seed) {
        this.seed = seed;
    }
}
