import java.sql.Timestamp;

/**
 * Author: shim.
 * Creation date: 7/24/15.
 */
public class RideSuggestion {
    private int rideSuggestionId;
    private int userId;
    private String startPoint;
    private String destinationPoint;
    private Timestamp rideTime;
    private int timeLag;
    private int capacity;
    private int freeSeatsNumber;

    public RideSuggestion() {
    }
    public RideSuggestion(int userId, String startPoint, String destinationPoint, Timestamp rideTime,
                          int timeLag, int capacity, int free_seats_number) {
        this.userId = userId;
        this.startPoint = startPoint;
        this.destinationPoint = destinationPoint;
        this.rideTime = rideTime;
        this.timeLag = timeLag;
        this.capacity = capacity;
        this.freeSeatsNumber = free_seats_number;
    }

    public RideSuggestion(int rideSuggestionId, int userId, String startPoint, String destinationPoint, Timestamp rideTime,
                          int timeLag, int capacity, int free_seats_number) {
        this.rideSuggestionId = rideSuggestionId;
        this.userId = userId;
        this.startPoint = startPoint;
        this.destinationPoint = destinationPoint;
        this.rideTime = rideTime;
        this.timeLag = timeLag;
        this.capacity = capacity;
        this.freeSeatsNumber = free_seats_number;
    }

    public int getRideSuggestionId() {
        return rideSuggestionId;
    }

    public void setRideSuggestionId(int rideSuggestionId) {
        this.rideSuggestionId = rideSuggestionId;
    }

    public Timestamp getRideTime() {
        return rideTime;
    }

    public void setRideTime(Timestamp rideTime) {
        this.rideTime = rideTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getDestinationPoint() {
        return destinationPoint;
    }

    public void setDestinationPoint(String destinationPoint) {
        this.destinationPoint = destinationPoint;
    }

    public Timestamp getStartTimeMin() {
        return rideTime;
    }

    public void setStartTimeMin(Timestamp rideTime) {
        this.rideTime = rideTime;
    }

    public int getTimeLag() {
        return timeLag;
    }

    public void setTimeLag(int timeLag) {
        this.timeLag = timeLag;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getFreeSeatsNumber() {
        return freeSeatsNumber;
    }

    public void setFreeSeatsNumber(int free_seats_number) {
        this.freeSeatsNumber = free_seats_number;
    }
}
