import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: shim.
 * Creation date: 7/24/15.
 */
public class RideSuggestionDAO {
    protected Connection connection;
    protected PreparedStatement preparedStatement;
//todo add check for duplicate suggestions
    public int createRideSuggestion(RideSuggestion rideSuggestion) {
        ResultSet generatedKeys = null;
        int result = -1;
        try {
            connection = ConnectionFactory.getConnection();
            String sqlInsertReview = "INSERT INTO ride_suggestions (user_login, start_point,destination_point, " +
                    "start_time_min, start_time_max, capacity, free_seats_number) " +
                    "VALUES (?, ? ,? ,?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(sqlInsertReview, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, rideSuggestion.getUserLogin());
            preparedStatement.setString(2, rideSuggestion.getStartPoint());
            preparedStatement.setString(3, rideSuggestion.getDestinationPoint());
            preparedStatement.setTimestamp(4, rideSuggestion.getStartTimeMin());
            preparedStatement.setTimestamp(5, rideSuggestion.getStartTimeMax());
            preparedStatement.setInt(6, rideSuggestion.getCapacity());
            preparedStatement.setInt(7, rideSuggestion.getCapacity());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating RideSuggestion failed, no rows affected.");
            }
            generatedKeys = preparedStatement.getGeneratedKeys();
            if(generatedKeys.next()){
                result = generatedKeys.getInt(1);
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
                rideSuggestion.setUserLogin(rs.getString("user_login"));
                rideSuggestion.setStartPoint(rs.getString("start_point"));
                rideSuggestion.setDestinationPoint(rs.getString("destination_point"));
                rideSuggestion.setStartTimeMin(rs.getTimestamp("start_time_min"));
                rideSuggestion.setStartTimeMax(rs.getTimestamp("start_time_max"));
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


    public RideSuggestion getRideSuggestion(int id) throws SQLException {
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
        preparedStatement.setString(1, rideSuggestion.getUserLogin());
        preparedStatement.setString(2, rideSuggestion.getStartPoint());
        preparedStatement.setString(3, rideSuggestion.getDestinationPoint());
        preparedStatement.setTimestamp(4, rideSuggestion.getStartTimeMin());
        preparedStatement.setTimestamp(5, rideSuggestion.getStartTimeMax());
        preparedStatement.setInt(6, rideSuggestion.getCapacity());
        preparedStatement.setInt(7, rideSuggestion.getFreeSeatsNumber());

        preparedStatement.execute();
    }

    public void delete(int id) throws SQLException {
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareCall("DELETE FROM ride_suggestions where ride_suggestion_id=?");
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
        }
        finally {
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
    }

    public List<RideSuggestion> getRideSuggestions(String login) {
        ResultSet rs = null;

        List<RideSuggestion> reviewList = new ArrayList<RideSuggestion>();
        try {
            connection = ConnectionFactory.getConnection();
            //todo add check for ride time
            preparedStatement = connection.prepareCall("SELECT * FROM ride_suggestions where user_login=?");
            preparedStatement.setString(1, login);
            preparedStatement.execute();
            rs = preparedStatement.getResultSet();

            while (rs.next()) {
                RideSuggestion review = convertResultSetToRideSuggestion(rs);
                reviewList.add(review);
            }
        } catch (SQLException e) {
            //todo
            e.printStackTrace();
        } finally {
            DbUtil.close(rs);
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return reviewList;
    }
    private RideSuggestion convertResultSetToRideSuggestion(ResultSet rs) throws SQLException{
        RideSuggestion rideSuggestion = new RideSuggestion();
        rideSuggestion.setUserLogin(rs.getString("user_login"));
        rideSuggestion.setStartPoint(rs.getString("start_point"));
        rideSuggestion.setDestinationPoint(rs.getString("destination_point"));
        rideSuggestion.setStartTimeMin(rs.getTimestamp("start_time_min"));
        rideSuggestion.setStartTimeMax(rs.getTimestamp("start_time_max"));
        rideSuggestion.setCapacity(rs.getInt("capacity"));
        rideSuggestion.setFreeSeatsNumber(rs.getInt("free_seats_number"));
        return rideSuggestion;
    }

    public List<RideSuggestion> getRideSuggestions(String startPoint, String destinationPoint, Timestamp startTimeMin, Timestamp startTimeMax) {
        ResultSet rs = null;

        List<RideSuggestion> reviewList = new ArrayList<RideSuggestion>();
        try {
            connection = ConnectionFactory.getConnection();
            //todo add check for ride time
            preparedStatement = connection.prepareCall("SELECT * FROM ride_suggestions where start_point=? and destination_point=?");
            preparedStatement.setString(1, startPoint);
            preparedStatement.setString(2, destinationPoint);
            preparedStatement.execute();
            rs = preparedStatement.getResultSet();

            while (rs.next()) {
                RideSuggestion review = convertResultSetToRideSuggestion(rs);
                reviewList.add(review);
            }
        } catch (SQLException e) {
            //todo
            e.printStackTrace();
        } finally {
            DbUtil.close(rs);
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return reviewList;
    }
}