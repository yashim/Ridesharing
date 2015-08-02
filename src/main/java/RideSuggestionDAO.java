import java.sql.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Author: shim.
 * Creation date: 7/24/15.
 */
public class RideSuggestionDAO {
    protected Connection connection;
    protected PreparedStatement preparedStatement;
//todo add check for duplicate suggestions


    public List<String> createRideSuggestion(RideSuggestion rideSuggestion) {
        ResultSet generatedKeys;
        List<String> result = new ArrayList<>();
        result.add("-1");
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
                result.set(0, "0");
                result.add(Integer.toString(generatedKeys.getInt(1)));
            }
        } catch (SQLException e) {
            //todo
            e.printStackTrace();
        } finally {
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return result;
    }

    public RideSuggestion getRideSuggestion(String login) throws SQLException {
        ResultSet rs = null;
        RideSuggestion rideSuggestion = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareCall("select * from ride_suggestions where ride_suggestions_id=?");
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
            preparedStatement = connection.prepareCall("select * from ride_suggestions where ride_suggestion_id=?");
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            rs = preparedStatement.getResultSet();
            rideSuggestion = new RideSuggestion();
            while (rs.next()) {
                rideSuggestion = convertResultSetToRideSuggestion(rs);
            }
        } catch (SQLException e) {
            //todo
            e.printStackTrace();
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
                "update ride_suggestions set" +
                        " user_login = ?," +
                        " start_point = ?," +
                        " destination_point = ?," +
                        " start_time_max = ?," +
                        " capacity = ?," +
                        " free_seats_number = ?," +
                        " where ride_suggestion_id = ?;"
        );
        preparedStatement.setInt(1, rideSuggestion.getUserId());
        preparedStatement.setString(2, rideSuggestion.getStartPoint());
        preparedStatement.setString(3, rideSuggestion.getDestinationPoint());
        preparedStatement.setTimestamp(4, rideSuggestion.getStartTimeMin());
        preparedStatement.setInt(5, rideSuggestion.getTimeLag());
        preparedStatement.setInt(6, rideSuggestion.getCapacity());
        preparedStatement.setInt(7, rideSuggestion.getFreeSeatsNumber());

        preparedStatement.execute();
    }

    public int delete(int rideSuggestionId){
        int result = -1;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareCall("DELETE FROM ride_suggestions where ride_suggestion_id=?");
            preparedStatement.setInt(1, rideSuggestionId);
            preparedStatement.execute();
            result = 0;
        } catch (SQLException e) {
            //todo
            e.printStackTrace();
        } finally {
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return result;
    }

    public List<RideSuggestion> getRideSuggestionsWhereUserIsDriver(int userId) {
        ResultSet rs = null;

        List<RideSuggestion> rideSuggestionList = new ArrayList<>();
        try {
            connection = ConnectionFactory.getConnection();
            //todo add check for ride time
            preparedStatement = connection.prepareCall("SELECT * FROM ride_suggestions WHERE user_id=? AND " +
                    "ride_time > NOW() AND free_seats_number > 0");
            preparedStatement.setInt(1, userId);
            preparedStatement.execute();
            rs = preparedStatement.getResultSet();

            while (rs.next()) {
                RideSuggestion rideSuggestion = convertResultSetToRideSuggestion(rs);
                rideSuggestionList.add(rideSuggestion);
            }
        } catch (SQLException e) {
            //todo
            e.printStackTrace();
        } finally {
            DbUtil.close(rs);
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return rideSuggestionList;
    }

    public List<RideSuggestion> getRideSuggestionsMadeByOthers(int userId) {
        ResultSet rs = null;

        List<RideSuggestion> rideSuggestionList = new ArrayList<>();
        try {
            connection = ConnectionFactory.getConnection();

            preparedStatement = connection.prepareCall("SELECT ride_suggestions.ride_suggestion_id, " +
                    "ride_suggestions.user_id, start_point, destination_point, ride_time, time_lag, " +
                    "capacity, free_seats_number FROM ride_suggestions LEFT JOIN shared_rides ON " +
                    "ride_suggestions.ride_suggestion_id = shared_rides.ride_suggestion_id WHERE shared_rides.user_id<>? " +
                    "AND ride_suggestions.user_id <> ? AND ride_time > NOW() AND free_seats_number > 0");
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, userId);
            preparedStatement.execute();
            rs = preparedStatement.getResultSet();

            while (rs.next()) {
                RideSuggestion review = convertResultSetToRideSuggestion(rs);
                rideSuggestionList.add(review);
            }
        } catch (SQLException e) {
            //todo
            e.printStackTrace();
        } finally {
            DbUtil.close(rs);
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return rideSuggestionList;
    }

    public List<RideSuggestion> getRideSuggestionsWhereUserIsPassenger(int userId) {
        ResultSet rs = null;

        List<RideSuggestion> rideSuggestionList = new ArrayList<>();
        try {
            connection = ConnectionFactory.getConnection();
            //todo add check for ride time
            //SELECT ride_suggestions.ride_suggestion_id, ride_suggestions.user_id, start_point, destination_point, ride_time, time_lag, capacity, free_seats_number FROM ride_suggestions INNER JOIN shared_rides on ride_suggestions.ride_suggestion_id = shared_rides.ride_suggestion_id;

            preparedStatement = connection.prepareCall("SELECT ride_suggestions.ride_suggestion_id, " +
                    "ride_suggestions.user_id, start_point, destination_point, ride_time, time_lag, " +
                    "capacity, free_seats_number FROM ride_suggestions LEFT JOIN shared_rides ON " +
                    "ride_suggestions.ride_suggestion_id = shared_rides.ride_suggestion_id WHERE shared_rides.user_id=? " +
                    "AND ride_time > NOW() AND free_seats_number > 0");
            preparedStatement.setInt(1, userId);
            preparedStatement.execute();
            rs = preparedStatement.getResultSet();

            while (rs.next()) {
                RideSuggestion review = convertResultSetToRideSuggestion(rs);
                rideSuggestionList.add(review);
            }
        } catch (SQLException e) {
            //todo
            e.printStackTrace();
        } finally {
            DbUtil.close(rs);
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return rideSuggestionList;
    }


    private RideSuggestion convertResultSetToRideSuggestion(ResultSet rs) throws SQLException{
        RideSuggestion rideSuggestion = new RideSuggestion();
        rideSuggestion.setRideSuggestionId(rs.getInt("ride_suggestion_id"));
        rideSuggestion.setUserId(rs.getInt("user_id"));
        rideSuggestion.setStartPoint(rs.getString("start_point"));
        rideSuggestion.setDestinationPoint(rs.getString("destination_point"));
        rideSuggestion.setStartTimeMin(rs.getTimestamp("ride_time"));
        rideSuggestion.setTimeLag(rs.getInt("time_lag"));
        rideSuggestion.setCapacity(rs.getInt("capacity"));
        rideSuggestion.setFreeSeatsNumber(rs.getInt("free_seats_number"));
        return rideSuggestion;
    }

    public Hashtable<RideSuggestionType, List<RideSuggestion>> getRides(int userId) {
        Hashtable<RideSuggestionType, List<RideSuggestion>> ridesWithType = new Hashtable<>();
        ridesWithType.put(RideSuggestionType.PASSENGER, getRideSuggestionsWhereUserIsPassenger(userId));
        ridesWithType.put(RideSuggestionType.DRIVER, getRideSuggestionsWhereUserIsDriver(userId));
        ridesWithType.put(RideSuggestionType.UNDEFINED, getRideSuggestionsMadeByOthers(userId));

        return ridesWithType;

    }

    public Object getRide(int rideId) {
        ResultSet rs = null;
        RideSuggestion rideSuggestion = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareCall("SELECT ride_suggestions.ride_suggestion_id, " +
                    "ride_suggestions.user_id, start_point, destination_point, ride_time, time_lag, capacity, " +
                    "free_seats_number FROM ride_suggestions LEFT JOIN users ON ride_suggestions.user_id = users.user_id" +
                    " WHERE ride_suggestion_id=? ride_time > NOW() and free_seats_number > 0");
            preparedStatement.setInt(1, rideId);
            preparedStatement.execute();
            rs = preparedStatement.getResultSet();
            rideSuggestion = new RideSuggestion();
            while (rs.next()) {
                rideSuggestion = convertResultSetToRideSuggestion(rs);
            }
        } catch (SQLException e) {
            //todo
            e.printStackTrace();
        } finally {
            DbUtil.close(rs);
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return rideSuggestion;
    }
}