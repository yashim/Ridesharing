import java.sql.Timestamp;

/**
 * Author: shim.
 * Creation date: 8/2/15.
 */
public class RideDetails extends RideSuggestion {
    private String id;
    private String driverName;
    private String driverLastName;
    private String driverPhone;

    public RideDetails() {
    }

    public RideDetails(String driverName, String driverLastName, String driverPhone) {
        this.driverName = driverName;
        this.driverLastName = driverLastName;
        this.driverPhone = driverPhone;
    }

    public RideDetails(int userId, String startPoint, String destinationPoint, Timestamp rideTime, int timeLag, int capacity, int free_seats_number, String driverName, String driverLastName, String driverPhone) {
        super(userId, startPoint, destinationPoint, rideTime, timeLag, capacity, free_seats_number);
        this.driverName = driverName;
        this.driverLastName = driverLastName;
        this.driverPhone = driverPhone;
    }

    public RideDetails(int rideSuggestionId, int userId, String startPoint, String destinationPoint, Timestamp rideTime, int timeLag, int capacity, int free_seats_number, String driverName, String driverLastName, String driverPhone) {
        super(rideSuggestionId, userId, startPoint, destinationPoint, rideTime, timeLag, capacity, free_seats_number);
        this.driverName = driverName;
        this.driverLastName = driverLastName;
        this.driverPhone = driverPhone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverLastName() {
        return driverLastName;
    }

    public void setDriverLastName(String driverLastName) {
        this.driverLastName = driverLastName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }
}
