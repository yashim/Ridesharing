import java.sql.Timestamp;

/**
 * Author: shim.
 * Creation date: 8/2/15.
 */
public class RideDetails extends RideSuggestion {
    private String driverName;
    private String driverLastName;
    private String driverPhone;
    private String driverLogin;
    private int chatId;

    public RideDetails() {
    }

    public RideDetails(String driverName, String driverLastName, String driverPhone) {
        this.driverName = driverName;
        this.driverLastName = driverLastName;
        this.driverPhone = driverPhone;
    }

    public RideDetails(int userId, String startPoint, String destinationPoint, Timestamp rideTime, int timeLag,
                       int capacity, int freeSeatsNumber, String driverName, String driverLastName, String driverPhone) {
        super(userId, startPoint, destinationPoint, rideTime, timeLag, capacity, freeSeatsNumber);
        this.driverName = driverName;
        this.driverLastName = driverLastName;
        this.driverPhone = driverPhone;
    }

    public RideDetails(int rideSuggestionId, int userId, String startPoint, String destinationPoint, Timestamp rideTime,
                       int timeLag, int capacity, int freeSeatsNumber, String driverName, String driverLastName, String driverPhone) {
        super(rideSuggestionId, userId, startPoint, destinationPoint, rideTime, timeLag, capacity, freeSeatsNumber);
        this.driverName = driverName;
        this.driverLastName = driverLastName;
        this.driverPhone = driverPhone;
    }
    public RideDetails(RideSuggestion rideSuggestion, String driverName, String driverLastName, String driverLogin){
        super(rideSuggestion.getRideSuggestionId(), rideSuggestion.getUserId(), rideSuggestion.getStartPoint(),
                rideSuggestion.getDestinationPoint(), rideSuggestion.getRideTime(), rideSuggestion.getTimeLag(),
                rideSuggestion.getCapacity(), rideSuggestion.getFreeSeatsNumber());
        this.driverName = driverName;
        this.driverLastName = driverLastName;
        this.driverLogin = driverLogin;
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

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public String getDriverLogin() {
        return driverLogin;
    }

    public void setDriverLogin(String driverLogin) {
        this.driverLogin = driverLogin;
    }
}
