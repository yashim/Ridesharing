import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.Hashtable;

/**
 * Author: shim.
 * Creation date: 4/18/14.
 */
public class UserDAO {
    private final Logger logger = LogManager.getLogger(UserDAO.class);
    protected Connection connection;
    protected PreparedStatement preparedStatement;

    public Hashtable<String, String> createUser(User user) {
        ResultSet generatedKeys;
        Hashtable<String, String> createUserResult = new Hashtable<>();
        createUserResult.put("Status","-1");
        try {
            connection = ConnectionFactory.getConnection();
            String sqlInsertReview = "INSERT INTO users (first_name, last_name,login, password, phone) " +
                    "VALUES (?, ? ,? ,?, ?)";
            preparedStatement = connection.prepareStatement(sqlInsertReview, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setString(3, user.getLogin());
            preparedStatement.setString(4, user.getPassword());
            preparedStatement.setString(5, user.getPhone());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            generatedKeys = preparedStatement.getGeneratedKeys();
            if(generatedKeys.next()){
                createUserResult.replace("Status", "0");
                createUserResult.put("UserId",Integer.toString(generatedKeys.getInt(1)));
            }
        } catch (SQLException e) {
            logger.error(e.getErrorCode() +":"+ e.getMessage() + ":");
        } finally {
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return createUserResult;
    }

    public Hashtable<String, String> createUserFromTelegram(User user, int chatId) {
        ResultSet generatedKeys;
        Hashtable<String, String> createUserResult = new Hashtable<>();
        createUserResult.put("Status","-1");
        try {
            connection = ConnectionFactory.getConnection();
            String sqlInsertReview = "INSERT INTO users (first_name, last_name,login, password, phone, chat_id, is_telegram) " +
                    "VALUES (?, ? ,? ,?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(sqlInsertReview, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setString(3, Integer.toString(chatId));
            preparedStatement.setString(4, user.getPassword());
            preparedStatement.setString(5, user.getPhone());
            preparedStatement.setInt(6, chatId);
            preparedStatement.setBoolean(7, true);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            generatedKeys = preparedStatement.getGeneratedKeys();
            if(generatedKeys.next()){
                createUserResult.replace("Status", "0");
                createUserResult.put("UserId",Integer.toString(generatedKeys.getInt(1)));
            }
        } catch (SQLException e) {
            logger.error(e.getErrorCode() +":"+ e.getMessage() + ":");
        } finally {
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return createUserResult;
    }

    public boolean exist(int chatId) throws SQLException {
        ResultSet result = null;
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareCall("SELECT 1 FROM users WHERE chat_id =?");
            preparedStatement.setInt(1, chatId);
            preparedStatement.execute();
            result = preparedStatement.getResultSet();
        return result.next();
    }

    public User getUser(int chatId) {
        ResultSet rs = null;
        User user = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareCall("SELECT * FROM users WHERE chat_id=?");
            preparedStatement.setInt(1, chatId);
            preparedStatement.execute();
            rs = preparedStatement.getResultSet();
            user = new User();
            while (rs.next()) {
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setLogin(rs.getString("login"));
                user.setPassword(rs.getString("password"));
                user.setPhone(rs.getString("phone"));
                user.setId(rs.getInt("user_id"));
            }
        } catch (SQLException e) {
            logger.error(e.getErrorCode() + ":" + e.getMessage());
        } finally {
            DbUtil.close(rs);
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return user;
    }
//todo change result
    public Hashtable<String, Object> getUser(Integer userId) {
        Hashtable<String, Object> getUserResult = new Hashtable<>();
        getUserResult.put("Status","-1");
        ResultSet rs = null;
        User user;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareCall("SELECT * FROM users WHERE user_id=?");
            preparedStatement.setInt(1, userId);
            preparedStatement.execute();
            rs = preparedStatement.getResultSet();
            user = new User();
            while (rs.next()) {
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setLogin(rs.getString("login"));
                user.setPassword("");
                user.setPhone(rs.getString("phone"));
            }
            getUserResult.replace("Status", "0");
            getUserResult.put("User", user);
        } catch (SQLException e) {
            logger.error(e.getErrorCode() + ":" + e.getMessage());
        } finally {
            DbUtil.close(rs);
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return getUserResult;
    }

    public Hashtable<String, Object> update (User user, int userId)  {
        Hashtable<String, Object> saveUserResult = new Hashtable<>();
        saveUserResult.put("Status","-1");
        connection = ConnectionFactory.getConnection();
        try {
        preparedStatement = connection.prepareStatement(
                "UPDATE users SET login = ?, first_name = ?, last_name = ?, phone = ? WHERE user_id = ?");
        preparedStatement.setString(1, user.getLogin());
//        preparedStatement.setString(2, user.getPassword());
        preparedStatement.setString(2, user.getFirstName());
        preparedStatement.setString(3, user.getLastName());
        preparedStatement.setString(4, user.getPhone());
        preparedStatement.setInt(5, userId);

        int affectedRows = preparedStatement.executeUpdate();
        if(affectedRows > 0)
            saveUserResult.replace("Status", "0");
        } catch (SQLException e) {
            logger.error(e.getErrorCode() + ":" + e.getMessage());
        }
        return saveUserResult;
    }

    public Hashtable<String, Object> updatePassword (String oldPassword, String newPassword, int userId)  {
        ResultSet rs;
        Hashtable<String, Object> updatePasswordResult = new Hashtable<>();
        updatePasswordResult.put("Status","-1");
        connection = ConnectionFactory.getConnection();
        try {
            preparedStatement = connection.prepareCall("SELECT password FROM users WHERE user_id=?");
            preparedStatement.setInt(1, userId);
            preparedStatement.execute();
            rs = preparedStatement.getResultSet();
            String passwordDb = "";
            while (rs.next()) {
                passwordDb = rs.getString("password");
            }
            if (passwordDb == null || !passwordDb.equals(oldPassword))
                return updatePasswordResult;

            preparedStatement = connection.prepareStatement(
                    "UPDATE users SET password = ? WHERE user_id = ?");
            preparedStatement.setString(1, newPassword);
            preparedStatement.setInt(2, userId);

            int affectedRows = preparedStatement.executeUpdate();
            if(affectedRows > 0)
                updatePasswordResult.replace("Status", "0");
        } catch (SQLException e) {
            logger.error(e.getErrorCode() + ":" + e.getMessage());
        }
        return updatePasswordResult;
    }

    public boolean updatePhone (String phone, int chatId)  {
        connection = ConnectionFactory.getConnection();
        try {
            preparedStatement = connection.prepareStatement("UPDATE users SET phone = ? WHERE chat_id = ?");
            preparedStatement.setString(1, phone);
            preparedStatement.setInt(2, chatId);

            int affectedRows = preparedStatement.executeUpdate();
            if(affectedRows > 0)
                return true;
        } catch (SQLException e) {
            logger.error(e.getErrorCode() + ":" + e.getMessage());

        }
        return false;
    }
    public boolean checkPhone(int chatId){
        User user = getUser(chatId);
        if( user.getPhone()==null || user.getPhone().equals("")){
            return false;
        }
        return true;
    }


    public void delete(int id) throws SQLException {
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareCall("DELETE FROM users where user_id=?");
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
        }
        finally {
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
    }

    public boolean exist (String login) throws SQLException {
        connection = ConnectionFactory.getConnection();
        preparedStatement = connection.prepareCall("SELECT 1 FROM users WHERE login =?");
        preparedStatement.setString(1, login);
        preparedStatement.execute();
        ResultSet result = preparedStatement.getResultSet();
        return result.next();
    }

}
