package com.reservation.model;

/**
 * Represents a reservation / booking record.
 */
public class Reservation {
    private String pnr;
    private String passengerName;
    private int trainNumber;
    private String trainName;
    private String classType;
    private String dateOfJourney;
    private String source;
    private String destination;
    private String bookingTime;

    public Reservation() {}

    public Reservation(String pnr, String passengerName, int trainNumber, String trainName,
                       String classType, String dateOfJourney, String source,
                       String destination, String bookingTime) {
        this.pnr = pnr;
        this.passengerName = passengerName;
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.classType = classType;
        this.dateOfJourney = dateOfJourney;
        this.source = source;
        this.destination = destination;
        this.bookingTime = bookingTime;
    }

    public String getPnr() { return pnr; }
    public void setPnr(String pnr) { this.pnr = pnr; }

    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }

    public int getTrainNumber() { return trainNumber; }
    public void setTrainNumber(int trainNumber) { this.trainNumber = trainNumber; }

    public String getTrainName() { return trainName; }
    public void setTrainName(String trainName) { this.trainName = trainName; }

    public String getClassType() { return classType; }
    public void setClassType(String classType) { this.classType = classType; }

    public String getDateOfJourney() { return dateOfJourney; }
    public void setDateOfJourney(String dateOfJourney) { this.dateOfJourney = dateOfJourney; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getBookingTime() { return bookingTime; }
    public void setBookingTime(String bookingTime) { this.bookingTime = bookingTime; }

    /** Returns a formatted summary string for display in dialogs. */
    public String toDisplayString() {
        return String.format("""
            ╔══════════════════════════════════════╗
            ║        BOOKING CONFIRMATION          ║
            ╠══════════════════════════════════════╣
            ║  PNR Number    : %-20s║
            ║  Passenger     : %-20s║
            ║  Train No.     : %-20d║
            ║  Train Name    : %-20s║
            ║  Class         : %-20s║
            ║  Journey Date  : %-20s║
            ║  From          : %-20s║
            ║  To            : %-20s║
            ║  Booked At     : %-20s║
            ╚══════════════════════════════════════╝
            """,
            pnr, passengerName, trainNumber, trainName,
            classType, dateOfJourney, source, destination, bookingTime);
    }
}
