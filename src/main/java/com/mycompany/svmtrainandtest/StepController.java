package com.mycompany.svmtrainandtest;

public interface StepController {
    boolean canGoBack();
    boolean canGoNext();
    void runWhileEnterStep() throws Exception;
}
