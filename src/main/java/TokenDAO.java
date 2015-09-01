import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.Hashtable;
import java.util.UUID;

/**
 * Author: shim.
 * Creation date: 4/18/14.
 */
public class TokenDAO {
    private final Logger logger = LogManager.getLogger(TokenDAO.class);

    protected Connection connection;
    protected PreparedStatement preparedStatement;

    public Hashtable<String, String> login(String login, String password){
        ResultSet rs;
        Hashtable<String, String> loginResult = new Hashtable<>();
        loginResult.put("Status", "-1");
        String passwordDb = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareCall("SELECT password, user_id FROM users WHERE login=?");
            preparedStatement.setString(1, login);
            preparedStatement.execute();
            rs = preparedStatement.getResultSet();
            int userId = -1;
            while (rs.next()) {
                passwordDb = rs.getString("password");
                userId = rs.getInt("user_id");
            }
            if (userId==-1 || passwordDb == null || !passwordDb.equals(password))
                return loginResult;
            loginResult.replace("Status", "0");
            loginResult.put("Token", createToken(userId));
            loginResult.put("UserId", Integer.toString(userId));
            return loginResult;

        } catch (SQLException e) {
            logger.error(e.getErrorCode() + ":" + e.getMessage());
        } finally {
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return loginResult;
    }

    public String createToken(int userId) {
        String result = "-1";
        UUID uuid = UUID.randomUUID();
        String randomUUIDString;
        try {
            connection = ConnectionFactory.getConnection();
            String sqlInsertReview = "INSERT INTO tokens (user_id, token) VALUES (?, ?) ON DUPLICATE KEY UPDATE user_id=VALUES(user_id), token=?";
            preparedStatement = connection.prepareStatement(sqlInsertReview, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, userId);
            randomUUIDString = uuid.toString();
            preparedStatement.setString(2, randomUUIDString);
            preparedStatement.setString(3, randomUUIDString);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating token failed, no rows affected.");
            }
            result = randomUUIDString;
        } catch (SQLException e) {
            logger.error(e.getErrorCode() + ":" + e.getMessage());
        } finally {
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return result;
    }

    public String getToken(int userId) {
        ResultSet rs = null;
        String token = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareCall("SELECT * FROM tokens WHERE user_id=?");
            preparedStatement.setInt(1, userId);
            preparedStatement.execute();
            rs = preparedStatement.getResultSet();
            while (rs.next()) {
                token = rs.getString("token");
            }
        } catch (SQLException e) {
            logger.error(e.getErrorCode() + ":" + e.getMessage());
        } finally {
            DbUtil.close(rs);
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return token;
    }

    public String update (int userId)  {
        connection = ConnectionFactory.getConnection();
        String randomUUIDString = "-1";
        try {
        preparedStatement = connection.prepareStatement("UPDATE tokens SET token = ? WHERE user_id = ?");

        UUID uuid = UUID.randomUUID();
        randomUUIDString = uuid.toString();
        preparedStatement.setString(1, randomUUIDString);
        preparedStatement.setInt(2, userId);
        preparedStatement.execute();
        } catch (SQLException e) {
            logger.error(e.getErrorCode() + ":" + e.getMessage());
        }
        return randomUUIDString;
    }

    public void delete(int userId) throws SQLException {
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareCall("DELETE FROM tokens WHERE user_id=?");
            preparedStatement.setInt(1, userId);
            preparedStatement.execute();
        }
        finally {
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
    }


    public boolean exist(String login) throws SQLException {
        connection = ConnectionFactory.getConnection();
        preparedStatement = connection.prepareCall("SELECT EXISTS (SELECT 1 FROM users WHERE login =?) as result");
        preparedStatement.setString(1, login);
        preparedStatement.execute();
        ResultSet result = preparedStatement.getResultSet();
        result.next();
        return result.getBoolean("result");
    }

}
