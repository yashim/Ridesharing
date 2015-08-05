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
public class RideSuggestionDAO {
    private final Logger logger = LogManager.getLogger(RideSuggestionDAO.class);
    protected Connection connection;
    protected PreparedStatement preparedStatement;

    public Hashtable<String, String> createRideSuggestion(RideSuggestion rideSuggestion) {
        ResultSet generatedKeys;
        Hashtable<String, String> createRideResult = new Hashtable<>();
        createRideResult.put("Status", "-1");
        try {
            connection = ConnectionFactory.getConnection();
            String sqlInsertReview = "INSERT INTO ride_suggestions (user_id, start_point,destination_point, " +
                    "ride_time, time_lag, capacity, free_seats_number) " +
                    "VALUES (?, ? ,? ,?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(sqlInsertReview, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, rideSuggestion.getUserId());
            preparedStatement.setString(2, rideSuggestion.getStartPoint());
            preparedStatement.setString(3, rideSuggestion.getDestinationPoint());
            preparedStatement.setTimestamp(4, rideSuggestion.getStartTimeMin());
            preparedStatement.setInt(5, rideSuggestion.getTimeLag());
            preparedStatement.setInt(6, rideSuggestion.getCapacity());
            preparedStatement.setInt(7, rideSuggestion.getCapacity());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating RideSuggestion failed, no rows affected.");
            }
            generatedKeys = preparedStatement.getGeneratedKeys();
            if(generatedKeys.next()){
                createRideResult.replace("Status", "0");
                createRideResult.put("RideId", Integer.toString(generatedKeys.getInt(1)));
            }
        } catch (SQLException e) {
            logger.error(e.getErrorCode() + ":" + e.getMessage());
        } finally {
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return createRideResult;
    }

    public RideSuggestion getRideSuggestion(String login) throws SQLException {
        ResultSet rs = null;
        RideSuggestion rideSuggestion = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareCall("SELECT * FROM ride_suggestions WHERE ride_suggestion_id=?");
            //TODO
            preparedStatement.setString(1, login);
            preparedStatement.execute();
            rs = preparedStatement.getResultSet();
            rideSuggestion = new RideSuggestion();
            while (rs.next()) {
                rideSuggestion.setUserId(rs.getInt("user_id"));
                rideSuggestion.setStartPoint(rs.getString("start_point"));
                rideSuggestion.setDestinationPoint(rs.getString("destination_point"));
                rideSuggestion.setStartTimeMin(rs.getTimestamp("start_time_min"));
                rideSuggestion.setTimeLag(rs.getInt("time_lag"));
                rideSuggestion.setCapacity(rs.getInt("capacity"));
                rideSuggestion.setFreeSeatsNumber(rs.getInt("free_seats_number"));
            }
        } finally {
            DbUtil.close(rs);
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return rideSuggestion;
    }


    public RideSuggestion getRideSuggestion(int id) {
        ResultSet rs = null;
        RideSuggestion rideSuggestion = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareCall("SELECT * FROM ride_suggestions WHERE ride_suggestion_id=?");
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            rs = preparedStatement.getResultSet();
            rideSuggestion = new RideSuggestion();
            while (rs.next()) {
                rideSuggestion = convertResultSetToRideSuggestion(rs);
            }
        } catch (SQLException e) {
            logger.error(e.getErrorCode() + ":" + e.getMessage());
        } finally {
            DbUtil.close(rs);
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return rideSuggestion;
    }

    public void update(RideSuggestion rideSuggestion) throws SQLException {
        connection = ConnectionFactory.getConnection();
        preparedStatement = connection.prepareStatement(
                "UPDATE ride_suggestions SET user_id = ?, start_point = ?, destination_point = ?, ride_time = ?," +
                        " capacity = ?, free_seats_number = ? WHERE ride_suggestion_id = ?");
        preparedStatement.setInt(1, rideSuggestion.getUserId());
        preparedStatement.setString(2, rideSuggestion.getStartPoint());
        preparedStatement.setString(3, rideSuggestion.getDestinationPoint());
        preparedStatement.setTimestamp(4, rideSuggestion.getStartTimeMin());
        preparedStatement.setInt(5, rideSuggestion.getTimeLag());
        preparedStatement.setInt(6, rideSuggestion.getCapacity());
        preparedStatement.setInt(7, rideSuggestion.getFreeSeatsNumber());

        preparedStatement.execute();
    }

    public Hashtable<String, String> delete(int rideSuggestionId, int userId){
        Hashtable<String, String> deleteRideResult = new Hashtable<>();
        deleteRideResult.put("Status", "-1");
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareCall("DELETE FROM ride_suggestions WHERE ride_suggestion_id=? AND user_id=?");
            preparedStatement.setInt(1, rideSuggestionId);
            preparedStatement.setInt(2, userId);
            int affectedRows = preparedStatement.executeUpdate();
            SharedRideDAO sharedRideDAO = new SharedRideDAO();
            sharedRideDAO.delete(rideSuggestionId);
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

    public List<RideDetails> getRideSuggestionsWhereUserIsDriver(int userId) {
        ResultSet rs = null;

        List<RideDetails> rideDetailsList = new ArrayList<>();
        try {
            connection = ConnectionFactory.getConnection();
            //todo add check for ride time
            preparedStatement = connection.prepareCall("SELECT users.last_name, users.first_name, users.phone, " +
                    "start_point, destination_point, ride_time, time_lag, capacity, free_seats_number, " +
                    "ride_suggestions.ride_suggestion_id, ride_suggestions.user_id " +
                    "FROM ride_suggestions " +
                    "JOIN users ON users.user_id = ride_suggestions.user_id " +
                    "WHERE ride_suggestions.user_id=? AND " +
                    "ride_time > NOW() AND free_seats_number > 0");
            preparedStatement.setInt(1, userId);
            preparedStatement.execute();
            rs = preparedStatement.getResultSet();

            while (rs.next()) {
                RideDetails rideDetails = convertResultSetToRideSuggestion(rs);
                rideDetailsList.add(rideDetails);
            }
        } catch (SQLException e) {
            logger.error(e.getErrorCode() + ":" + e.getMessage());
        } finally {
            DbUtil.close(rs);
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return rideDetailsList;
    }

    public List<RideDetails> getRideSuggestionsMadeByOthers(int userId) {
        ResultSet rs = null;

        List<RideDetails> rideDetailsList = new ArrayList<>();
        try {
            connection = ConnectionFactory.getConnection();

            preparedStatement = connection.prepareCall("SELECT users.last_name, users.first_name, users.phone, " +
                    "start_point, destination_point, ride_time, time_lag, capacity, free_seats_number," +
                    "ride_suggestions.ride_suggestion_id,ride_suggestions.user_id FROM ride_suggestions LEFT JOIN shared_rides ON " +
                    "ride_suggestions.ride_suggestion_id = shared_rides.ride_suggestion_id " +
                    "JOIN users ON users.user_id = ride_suggestions.user_id " +
                    "WHERE (shared_rides.user_id IS NULL OR shared_rides.user_id  <>? )" +
                    "AND (ride_suggestions.user_id IS NULL OR ride_suggestions.user_id <> ?) AND ride_time > NOW() AND free_seats_number > 0");
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, userId);
            preparedStatement.execute();
            rs = preparedStatement.getResultSet();

            while (rs.next()) {
                RideDetails rideDetails = convertResultSetToRideSuggestion(rs);
                rideDetailsList.add(rideDetails);
            }
        } catch (SQLException e) {
            logger.error(e.getErrorCode() + ":" + e.getMessage());
        } finally {
            DbUtil.close(rs);
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return rideDetailsList;
    }

    public List<RideDetails> getRideSuggestionsWhereUserIsPassenger(int userId) {
        ResultSet rs = null;

        List<RideDetails> rideDetailsList = new ArrayList<>();
        try {
            connection = ConnectionFactory.getConnection();
            //todo add check for ride time
            preparedStatement = connection.prepareCall("SELECT users.last_name, users.first_name, users.phone, " +
                    "start_point, destination_point, ride_time, time_lag, capacity, free_seats_number," +
                    "ride_suggestions.ride_suggestion_id,ride_suggestions.user_id FROM ride_suggestions LEFT JOIN shared_rides ON " +
                    "ride_suggestions.ride_suggestion_id = shared_rides.ride_suggestion_id " +
                    "JOIN users ON users.user_id = ride_suggestions.user_id " +
                    "WHERE shared_rides.user_id=? " +
                    "AND ride_time > NOW() AND free_seats_number > 0");
            preparedStatement.setInt(1, userId);
            preparedStatement.execute();
            rs = preparedStatement.getResultSet();

            while (rs.next()) {
                RideDetails rideDetails = convertResultSetToRideSuggestion(rs);
                rideDetailsList.add(rideDetails);
            }
        } catch (SQLException e) {
            logger.error(e.getErrorCode() + ":" + e.getMessage());
        } finally {
            DbUtil.close(rs);
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return rideDetailsList;
    }

    private RideDetails convertResultSetToRideSuggestion(ResultSet rs) throws SQLException{
        RideDetails rideDetails = new RideDetails();
        rideDetails.setRideSuggestionId(rs.getInt("ride_suggestion_id"));
        rideDetails.setUserId(rs.getInt("user_id"));
        rideDetails.setStartPoint(rs.getString("start_point"));
        rideDetails.setDestinationPoint(rs.getString("destination_point"));
        rideDetails.setStartTimeMin(rs.getTimestamp("ride_time"));
        rideDetails.setTimeLag(rs.getInt("time_lag"));
        rideDetails.setCapacity(rs.getInt("capacity"));
        rideDetails.setFreeSeatsNumber(rs.getInt("free_seats_number"));
        rideDetails.setDriverName(rs.getString("first_name"));
        rideDetails.setDriverLastName(rs.getString("last_name"));
        rideDetails.setDriverPhone(rs.getString("phone"));
        return rideDetails;
    }

    public Hashtable<RideSuggestionType, List<RideDetails>> getRides(int userId) {
        Hashtable<RideSuggestionType, List<RideDetails>> ridesWithType = new Hashtable<>();
        ridesWithType.put(RideSuggestionType.PASSENGER, getRideSuggestionsWhereUserIsPassenger(userId));
        ridesWithType.put(RideSuggestionType.DRIVER, getRideSuggestionsWhereUserIsDriver(userId));
        ridesWithType.put(RideSuggestionType.UNDEFINED, getRideSuggestionsMadeByOthers(userId));

        return ridesWithType;

    }

    public RideDetails getRide(int rideId) {
        ResultSet rs = null;
        RideDetails rideDetails = new RideDetails();
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareCall("SELECT ride_suggestions.ride_suggestion_id, " +
                    "ride_suggestions.user_id, start_point, destination_point, ride_time, time_lag, capacity, " +
                    "free_seats_number FROM ride_suggestions " +
                    " WHERE ride_suggestion_id=?");
            preparedStatement.setInt(1, rideId);
            preparedStatement.execute();
            rs = preparedStatement.getResultSet();

            while (rs.next()) {
                rideDetails.setRideSuggestionId(rs.getInt("ride_suggestion_id"));
                rideDetails.setUserId(rs.getInt("user_id"));
                rideDetails.setStartPoint(rs.getString("start_point"));
                rideDetails.setDestinationPoint(rs.getString("destination_point"));
                rideDetails.setStartTimeMin(rs.getTimestamp("ride_time"));
                rideDetails.setTimeLag(rs.getInt("time_lag"));
                rideDetails.setCapacity(rs.getInt("capacity"));
                rideDetails.setFreeSeatsNumber(rs.getInt("free_seats_number"));
            }
            UserDAO userDAO = new UserDAO();
            User user = (User) userDAO.getUser(rideDetails.getUserId()).get("User");
            rideDetails.setDriverLastName(user.getLastName());
            rideDetails.setDriverName(user.getFirstName());
            rideDetails.setDriverPhone(user.getPhone());

        } catch (SQLException e) {
            logger.error(e.getErrorCode() + ":" + e.getMessage());
        } finally {
            DbUtil.close(rs);
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return rideDetails;
    }
}