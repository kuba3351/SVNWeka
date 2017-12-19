package com.mycompany.svmtrainandtest;

public class SplunkDataSource {
    private String name;
    private String totalCount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public String toString() {
        return name+" ("+totalCount+")";
    }
}
