import java.sql.Timestamp;

/**
 * Author: shim.
 * Creation date: 8/24/15.
 */
public class Subscription {
    private int id;
    private int userId;
    private int chatId;
    private String startPoint;
    private String destinationPoint;
    private Timestamp creationTime;

    public Subscription() {
    }

    public Subscription(int id, int userId, int chatId, String startPoint, String destinationPoint, Timestamp creationTime) {
        this.id = id;
        this.userId = userId;
        this.chatId = chatId;
        this.startPoint = startPoint;
        this.destinationPoint = destinationPoint;
        this.creationTime = creationTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
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

    public Timestamp getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
    }
}
