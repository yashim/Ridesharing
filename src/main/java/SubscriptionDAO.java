import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.Hashtable;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Author: shim.
 * Creation date: 8/24/15.
 */
public class SubscriptionDAO {
    private final Logger logger = LogManager.getLogger(SubscriptionDAO.class);
    protected Connection connection;
    protected PreparedStatement preparedStatement;

    public Hashtable<String, String> createSubscription(int userId, int chatId, String startPoint, String destinationPoint) {
        ResultSet generatedKeys;
        Hashtable<String, String> createUserResult = new Hashtable<>();
        createUserResult.put("Status","-1");
        try {
            connection = ConnectionFactory.getConnection();
            String sqlInsertReview = "INSERT INTO subscriptions (user_id, chat_id, start_point, destination_point) " +
                    "VALUES (?, ? ,? ,?)";
            preparedStatement = connection.prepareStatement(sqlInsertReview, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, chatId);
            preparedStatement.setString(3, startPoint);
            preparedStatement.setString(4, destinationPoint);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating subscription failed, no rows affected.");
            }
            generatedKeys = preparedStatement.getGeneratedKeys();
            if(generatedKeys.next()){
                createUserResult.replace("Status", "0");
                createUserResult.put("SubscriptionId",Integer.toString(generatedKeys.getInt(1)));
            }
        } catch (SQLException e) {
            logger.error(e.getErrorCode() +":"+ e.getMessage() + ":");
        } finally {
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return createUserResult;
    }

    public Hashtable<String, String> createSubscription(Subscription subscription) {
        ResultSet generatedKeys;
        Hashtable<String, String> createUserResult = new Hashtable<>();
        createUserResult.put("Status","-1");
        try {
            connection = ConnectionFactory.getConnection();
            String sqlInsertReview = "INSERT INTO subscriptions (user_id, chat_id, start_point, destination_point) " +
                    "VALUES (?, ? ,? ,?)";
            preparedStatement = connection.prepareStatement(sqlInsertReview, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, subscription.getUserId());
            preparedStatement.setInt(2, subscription.getChatId());
            preparedStatement.setString(3, subscription.getStartPoint());
            preparedStatement.setString(4, subscription.getDestinationPoint());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating subscription failed, no rows affected.");
            }
            generatedKeys = preparedStatement.getGeneratedKeys();
            if(generatedKeys.next()){
                createUserResult.replace("Status", "0");
                createUserResult.put("SubscriptionId",Integer.toString(generatedKeys.getInt(1)));
            }
        } catch (SQLException e) {
            logger.error(e.getErrorCode() +":"+ e.getMessage() + ":");
        } finally {
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return createUserResult;
    }

    public void delete(int userId) throws SQLException {
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareCall("DELETE FROM subscriptions WHERE user_id=?");
            preparedStatement.setInt(1, userId);
            preparedStatement.execute();
        }
        finally {
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
    }
    public void delete(int id, String destinationPoint) throws SQLException {
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareCall("DELETE FROM subscriptions WHERE user_id=? AND destination_point=?");
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, destinationPoint);
            preparedStatement.execute();
        }
        finally {
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
    }

    public CopyOnWriteArrayList<Subscription> getAllSubscriptions(){
        ResultSet rs = null;
        CopyOnWriteArrayList<Subscription> subscriptionsList = new CopyOnWriteArrayList<>();
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareCall("SELECT * FROM subscriptions");
            preparedStatement.execute();
            rs = preparedStatement.getResultSet();
            while (rs.next()) {
                Subscription subscription = convertResultSetToSubscription(rs);
                subscriptionsList.add(subscription);
            }
        } catch (SQLException e) {
            logger.error(e.getErrorCode() + ":" + e.getMessage());
        } finally {
            DbUtil.close(rs);
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return subscriptionsList;
    }

    public boolean exist (int userId) throws SQLException {
        connection = ConnectionFactory.getConnection();
        preparedStatement = connection.prepareCall("SELECT 1 FROM subscriptions WHERE user_id =?");
        preparedStatement.setInt(1, userId);
        preparedStatement.execute();
        ResultSet result = preparedStatement.getResultSet();
        return result.next();
    }

    private Subscription convertResultSetToSubscription(ResultSet rs) throws SQLException{
        Subscription subscription = new Subscription();
        subscription.setId(rs.getInt("subscription_id"));
        subscription.setChatId(rs.getInt("chat_id"));
        subscription.setUserId(rs.getInt("user_id"));
        subscription.setStartPoint(rs.getString("start_point"));
        subscription.setDestinationPoint(rs.getString("destination_point"));
        return subscription;
    }
}
