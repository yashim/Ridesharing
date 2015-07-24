/**
 * Author: shim.
 * Creation date: 7/24/15.
 */
public class SharedRide {
    private int sharedRideId;
    private int rideSuggestionId;
    private int userId;
    private int seatsAmount;

    public SharedRide() {
    }

    public SharedRide(int rideSuggestionId, int userId, int seatsAmount) {
        this.rideSuggestionId = rideSuggestionId;
        this.userId = userId;
        this.seatsAmount = seatsAmount;
    }

    public int getSharedRideId() {
        return sharedRideId;
    }

    public void setSharedRideId(int sharedRideId) {
        this.sharedRideId = sharedRideId;
    }

    public int getRideSuggestionId() {
        return rideSuggestionId;
    }

    public void setRideSuggestionId(int rideSuggestionId) {
        this.rideSuggestionId = rideSuggestionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getSeatsAmount() {
        return seatsAmount;
    }

    public void setSeatsAmount(int seatsAmount) {
        this.seatsAmount = seatsAmount;
    }
}
