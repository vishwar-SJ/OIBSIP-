package com.reservation.model;

/**
 * Represents a train in the master data.
 */
public class Train {
    private int trainNumber;
    private String trainName;

    public Train() {}

    public Train(int trainNumber, String trainName) {
        this.trainNumber = trainNumber;
        this.trainName = trainName;
    }

    public int getTrainNumber() { return trainNumber; }
    public void setTrainNumber(int trainNumber) { this.trainNumber = trainNumber; }

    public String getTrainName() { return trainName; }
    public void setTrainName(String trainName) { this.trainName = trainName; }

    @Override
    public String toString() {
        return trainNumber + " - " + trainName;
    }
}
