import java.sql.Timestamp;

/**
 * Author: shim.
 * Creation date: 7/24/15.
 */
public class RideSuggestion {
    private String userLogin;
    private String startPoint;
    private String destinationPoint;
    private Timestamp startTimeMin;
    private Timestamp startTimeMax;
    private int capacity;
    private int freeSeatsNumber;

    public RideSuggestion() {
    }

    public RideSuggestion(String userLogin, String startPoint, String destinationPoint, Timestamp startTimeMin,
                          Timestamp startTimeMax, int capacity, int free_seats_number) {
        //this.id = id;
        this.userLogin = userLogin;
        this.startPoint = startPoint;
        this.destinationPoint = destinationPoint;
        this.startTimeMin = startTimeMin;
        this.startTimeMax = startTimeMax;
        this.capacity = capacity;
        this.freeSeatsNumber = free_seats_number;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
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
        return startTimeMin;
    }

    public void setStartTimeMin(Timestamp startTimeMin) {
        this.startTimeMin = startTimeMin;
    }

    public Timestamp getStartTimeMax() {
        return startTimeMax;
    }

    public void setStartTimeMax(Timestamp startTimeMax) {
        this.startTimeMax = startTimeMax;
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
