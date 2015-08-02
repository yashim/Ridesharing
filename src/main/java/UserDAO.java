import java.sql.*;
import java.util.Hashtable;

/**
 * Author: shim.
 * Creation date: 4/18/14.
 */
public class UserDAO {
    protected Connection connection;
    protected PreparedStatement preparedStatement;

    public Hashtable<String, String> createUser(User user) {
        ResultSet generatedKeys = null;
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
            //todo
            e.printStackTrace();
        } finally {
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return createUserResult;
    }

    public User getUser(String login) {
        ResultSet rs = null;
        User user = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareCall("select * from users where login=?");
            preparedStatement.setString(1, login);
            preparedStatement.execute();
            rs = preparedStatement.getResultSet();
            user = new User();
            while (rs.next()) {
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setLogin(rs.getString("login"));
                user.setPassword(rs.getString("password"));
                user.setPhone(rs.getString("phone"));
            }
        } catch (SQLException e) {
            //TODO
            e.printStackTrace();
        } finally {
            DbUtil.close(rs);
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return user;
    }
//todo change result
    public User getUser(Integer id) {
        ResultSet rs = null;
        User user = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareCall("select * from users where user_id=?");
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            rs = preparedStatement.getResultSet();
            user = new User();
            while (rs.next()) {
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setLogin(rs.getString("login"));
                user.setPassword(rs.getString("password"));
                user.setPhone(rs.getString("phone"));
            }
        } catch (SQLException e) {
            //TODO
            e.printStackTrace();
        } finally {
            DbUtil.close(rs);
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return user;
    }

    public int update (User user)  {
        connection = ConnectionFactory.getConnection();
        try {
        preparedStatement = connection.prepareStatement(
                "update users set" +
                        " login = ?," +
                        " password = ?," +
                        " first_name = ?," +
                        " last_name = ?," +
                        " phone = ?," +
                        " where login = ?;"
        );

        preparedStatement.setString(1, user.getLogin());
        preparedStatement.setString(2, user.getPassword());
        preparedStatement.setString(3, user.getFirstName());
        preparedStatement.setString(4, user.getLastName());
        preparedStatement.setString(5, user.getPhone());
        preparedStatement.setString(6, user.getLogin());

        preparedStatement.execute();
        } catch (SQLException e) {
            //todo
            e.printStackTrace();
        }
        return 0;
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
