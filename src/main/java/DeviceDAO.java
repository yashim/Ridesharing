import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Author: shim.
 * Creation date: 8/11/15.
 */
public class DeviceDAO {
    private final Logger logger = LogManager.getLogger(UserDAO.class);
    protected Connection connection;
    protected PreparedStatement preparedStatement;

    public Hashtable<String, String> createDevice(Device device) {
        ResultSet generatedKeys;
        Hashtable<String, String> createDeviceResult = new Hashtable<>();
        createDeviceResult.put("Status", "-1");
        try {
            connection = ConnectionFactory.getConnection();
            String sqlInsertReview = "INSERT INTO devices (user_id, token, os) " +
                    "VALUES (?, ?, ?)";
            preparedStatement = connection.prepareStatement(sqlInsertReview, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, device.getUserId());
            preparedStatement.setString(2, device.getToken());
            preparedStatement.setString(3, device.getOs());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating device failed, no rows affected.");
            }
            generatedKeys = preparedStatement.getGeneratedKeys();
            if(generatedKeys.next()){
                createDeviceResult.replace("Status", "0");
                createDeviceResult.put("DeviceId", Integer.toString(generatedKeys.getInt(1)));
            }
        } catch (SQLException e) {
            logger.error(e.getErrorCode() +":"+ e.getMessage() + ":");
        } finally {
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return createDeviceResult;
    }
    public Device getDevice(int deviceId) {
        ResultSet rs = null;
        Device device = new Device();
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareCall("SELECT * FROM devices WHERE device_id=?");
            preparedStatement.setInt(1, deviceId);
            preparedStatement.execute();
            rs = preparedStatement.getResultSet();

            while (rs.next()) {
                device.setDeviceId(rs.getInt("device_id"));
                device.setToken(rs.getString("token"));
                device.setOs(rs.getString("os"));
                device.setRegistrationDate(rs.getTimestamp("registration_time"));
            }
        } catch (SQLException e) {
            logger.error(e.getErrorCode() + ":" + e.getMessage());
        } finally {
            DbUtil.close(rs);
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return device;
    }
    public List<Device> getDeviceByUserId(int userId){
        ResultSet rs = null;
        List<Device> deviceList = new ArrayList<>();
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareCall("SELECT token, os, device_id, registration_date FROM devices, users JOIN users ON users.user_id = devices.user_id WHERE user_id=?");
            preparedStatement.setInt(1, userId);
            preparedStatement.execute();
            rs = preparedStatement.getResultSet();

            while (rs.next()) {
                Device device = convertResultSetToDevice(rs);
                deviceList.add(device);
            }
        } catch (SQLException e) {
            logger.error(e.getErrorCode() + ":" + e.getMessage());
        } finally {
            DbUtil.close(rs);
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return deviceList;
    }
    public Hashtable<String, Object> update (Device device, int deviceId)  {
        Hashtable<String, Object> saveUserResult = new Hashtable<>();
        saveUserResult.put("Status","-1");
        connection = ConnectionFactory.getConnection();
        try {
            preparedStatement = connection.prepareStatement(
                    "UPDATE devices SET user_id = ?, token = ?, os = ? WHERE device_id = ?");
            preparedStatement.setInt(1, device.getUserId());
            preparedStatement.setString(2, device.getToken());
            preparedStatement.setString(3, device.getOs());
            preparedStatement.setInt(4, deviceId);

            int affectedRows = preparedStatement.executeUpdate();
            if(affectedRows > 0)
                saveUserResult.replace("Status", "0");
        } catch (SQLException e) {
            logger.error(e.getErrorCode() + ":" + e.getMessage());
        }
        return saveUserResult;
    }
    public Hashtable<String, String> delete(int deviceid){
        Hashtable<String, String> deleteRideResult = new Hashtable<>();
        deleteRideResult.put("Status", "-1");
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareCall("DELETE FROM devices WHERE device_id=?");
            preparedStatement.setInt(1, deviceid);
            int affectedRows = preparedStatement.executeUpdate();
            if(affectedRows == 0)
                return deleteRideResult;
            deleteRideResult.replace("Status", "0");
        } catch (SQLException e) {
            logger.error(e.getErrorCode() + ":" + e.getMessage());
        } finally {
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return deleteRideResult;
    }

    private Device convertResultSetToDevice(ResultSet rs) throws SQLException{
        Device device = new Device();
        device.setDeviceId(rs.getInt("ride_suggestion_id"));
        device.setUserId(rs.getInt("user_id"));
        device.setToken(rs.getString("start_point"));
        device.setOs(rs.getString("destination_point"));
        device.setRegistrationDate(rs.getTimestamp("ride_time"));
        return device;
    }
}
