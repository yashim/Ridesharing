import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Author: shim.
 * Creation date: 7/24/15.
 */
public class SharedRideDAO {
    protected Connection connection;
    protected PreparedStatement preparedStatement;
    private final Logger logger = LogManager.getLogger(SharedRideDAO.class);

//todo decrease seats_amount and check foreign key: ride_suggestion_id
    public Hashtable<String, String> joinRide(int rideSuggestionId, int userId, int seatsAmount) {
        ResultSet generatedKeys;
        Hashtable<String, String> joinRideResult = new Hashtable<>();
        joinRideResult.put("Status", "-1");
        try {
            connection = ConnectionFactory.getConnection();
            String sqlInsertReview = "INSERT INTO shared_rides (ride_suggestion_id, user_id, seats_amount) " +
                    "VALUES (?, ? ,?)";
            preparedStatement = connection.prepareStatement(sqlInsertReview, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, rideSuggestionId);
            preparedStatement.setInt(2, userId);
            preparedStatement.setInt(3, seatsAmount);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                return joinRideResult;
            }
            generatedKeys = preparedStatement.getGeneratedKeys();
            if(generatedKeys.next()){
                joinRideResult.replace("Status", "0");
                joinRideResult.put("RideId", Integer.toString(generatedKeys.getInt(1)));
            }
//            RideSuggestionDAO rideSuggestionDAO = new RideSuggestionDAO();
//            rideSuggestionDAO.decreaseSeatsAmount(rideSuggestionId, seatsAmount);
        } catch (SQLException e) {
            logger.error(e.getErrorCode() + ":" + e.getMessage());
        } finally {
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return joinRideResult;
    }

    public Hashtable<String, String> joinRideFromTelegram(int rideSuggestionId, int userId, int seatsAmount) {
        ResultSet generatedKeys;
        Hashtable<String, String> joinRideResult = new Hashtable<>();
        joinRideResult.put("Status", "-1");
        try {
            connection = ConnectionFactory.getConnection();
            String sqlInsertReview = "INSERT INTO shared_rides (ride_suggestion_id, user_id, seats_amount) " +
                    "VALUES (?, ? ,?)";
            preparedStatement = connection.prepareStatement(sqlInsertReview, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, rideSuggestionId);
            preparedStatement.setInt(2, userId);
            preparedStatement.setInt(3, seatsAmount);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                return joinRideResult;
            }
            generatedKeys = preparedStatement.getGeneratedKeys();
            if(generatedKeys.next()){
                joinRideResult.replace("Status", "0");
                joinRideResult.put("RideId", Integer.toString(generatedKeys.getInt(1)));
            }
        } catch (SQLException e) {
            logger.error(e.getErrorCode() + ":" + e.getMessage());
        } finally {
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return joinRideResult;
    }


    public SharedRide getSharedRide(int id) throws SQLException {
        ResultSet rs = null;
        SharedRide sharedRide = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareCall("SELECT * FROM shared_rides WHERE shared_ride_id=?");
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            rs = preparedStatement.getResultSet();
            sharedRide = new SharedRide();
            while (rs.next()) {
                sharedRide.setRideSuggestionId(rs.getInt("ride_suggestion_id"));
                sharedRide.setUserId(rs.getInt("user_id"));
                sharedRide.setSeatsAmount(rs.getInt("seats_amount"));
            }
        } finally {
            DbUtil.close(rs);
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return sharedRide;
    }

    public void update(SharedRide sharedRide) throws SQLException {
        connection = ConnectionFactory.getConnection();
        preparedStatement = connection.prepareStatement(
                "UPDATE shared_rides SET ride_suggestion_id = ?, user_id = ?, seats_amount = ?" +
                        " WHERE shared_ride_id = ?");
        preparedStatement.setInt(1, sharedRide.getRideSuggestionId());
        preparedStatement.setInt(2, sharedRide.getUserId());
        preparedStatement.setInt(2, sharedRide.getSeatsAmount());
        preparedStatement.setInt(2, sharedRide.getSharedRideId());
        preparedStatement.execute();
    }

    public List<SharedRide> getSharedRideList(int userId) {
        ResultSet rs = null;

        List<SharedRide> sharedRideList = new ArrayList<>();
        try {
            connection = ConnectionFactory.getConnection();
            //todo add check for ride time
            preparedStatement = connection.prepareCall("SELECT * FROM shared_rides where user_id!=?");
            preparedStatement.setInt(1, userId);
            preparedStatement.execute();
            rs = preparedStatement.getResultSet();

            while (rs.next()) {
                SharedRide sharedRide = convertResultSetToSharedRide(rs);
                sharedRideList.add(sharedRide);
            }
        } catch (SQLException e) {
            logger.error(e.getErrorCode() + ":" + e.getMessage());
        } finally {
            DbUtil.close(rs);
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return sharedRideList;
    }

    private SharedRide convertResultSetToSharedRide(ResultSet rs) throws SQLException{
        SharedRide sharedRide = new SharedRide();
        sharedRide.setRideSuggestionId(rs.getInt("start_point"));
        sharedRide.setUserId(rs.getInt("user_id"));
        sharedRide.setSeatsAmount(rs.getInt("destination_point"));
        return sharedRide;
    }

    public int delete(int rideSuggestionId) {
        int result = -1;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareCall("DELETE FROM shared_rides WHERE ride_suggestion_id=?");
            preparedStatement.setInt(1, rideSuggestionId);
            if(preparedStatement.executeUpdate() > 0)
                result = 0;
        } catch (SQLException e) {
            logger.error(e.getErrorCode() + ":" + e.getMessage());
        } finally {
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return result;
    }
    public int delete(int rideSuggestionId, int userId) {
        int result = -1;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareCall("DELETE FROM shared_rides WHERE ride_suggestion_id=? AND user_id=?");
            preparedStatement.setInt(1, rideSuggestionId);
            preparedStatement.setInt(2, userId);
            preparedStatement.execute();
            result = 0;
        } catch (SQLException e) {
            logger.error(e.getErrorCode() + ":" + e.getMessage());
        } finally {
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return result;
    }

}