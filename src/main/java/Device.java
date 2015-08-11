import java.sql.Timestamp;

/**
 * Author: shim.
 * Creation date: 8/11/15.
 */
public class Device {
    private int deviceId;
    private String token;
    private String os;
    private Timestamp registrationDate;
    private int userId;

    public Device() {
    }

    public Device(int deviceId, int userId, String token, String os, Timestamp registrationDate) {
        this.deviceId = deviceId;
        this.userId = userId;
        this.token = token;
        this.os = os;
        this.registrationDate = registrationDate;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public Timestamp getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Timestamp registrationDate) {
        this.registrationDate = registrationDate;
    }
}
