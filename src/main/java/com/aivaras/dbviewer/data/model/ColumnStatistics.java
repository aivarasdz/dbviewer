package com.aivaras.dbviewer.data.model;

public class ColumnStatistics<T> {

    private String name;
    private T min;
    private T max;
    private Double average;
    private Double median;

    public ColumnStatistics(String name, T min, T max) {
        this.name = name;
        this.min = min;
        this.max = max;
    }

    public ColumnStatistics(String name, T min, T max, Double average, Double median) {
        this.name = name;
        this.min = min;
        this.max = max;
        this.average = average;
        this.median = median;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public T getMin() {
        return min;
    }

    public void setMin(T min) {
        this.min = min;
    }

    public T getMax() {
        return max;
    }

    public void setMax(T max) {
        this.max = max;
    }

    public Double getAverage() {
        return average;
    }

    public void setAverage(Double average) {
        this.average = average;
    }

    public Double getMedian() {
        return median;
    }

    public void setMedian(Double median) {
        this.median = median;
    }
}
