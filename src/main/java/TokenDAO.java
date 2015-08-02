import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Author: shim.
 * Creation date: 4/18/14.
 */
public class TokenDAO {
    protected Connection connection;
    protected PreparedStatement preparedStatement;

    public  List<String> login(String login, String password){
        ResultSet rs;
        List<String> result = new ArrayList<>();
        result.add("-1");
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
            if (passwordDb.equals(password))
                return createToken(userId);
        } catch (SQLException e) {
            //todo
            e.printStackTrace();
        } finally {
            DbUtil.close(preparedStatement);
            DbUtil.close(connection);
        }
        return result;
    }

    public List<String> createToken(int userId) {
        List<String> result = new ArrayList<>();
        result.add("-1");
        UUID uuid = UUID.randomUUID();
        String randomUUIDString;
        try {
            connection = ConnectionFactory.getConnection();
            String sqlInsertReview = "INSERT INTO tokens (user_id, token) VALUES (?, ?)";
            preparedStatement = connection.prepareStatement(sqlInsertReview, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, userId);
            randomUUIDString = uuid.toString();
            preparedStatement.setString(2, randomUUIDString);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating token failed, no rows affected.");
            }
            result.set(0, "0");
            result.add(randomUUIDString);
        } catch (SQLException e) {
            //todo
            e.printStackTrace();
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
            //TODO
            e.printStackTrace();
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
        preparedStatement = connection.prepareStatement(
                "UPDATE tokens SET" +
                        " token = ?," +
                        " WHERE user_id = ?;"
        );

        UUID uuid = UUID.randomUUID();
        randomUUIDString = uuid.toString();
        preparedStatement.setString(1, randomUUIDString);
        preparedStatement.setInt(2, userId);
        preparedStatement.execute();
        } catch (SQLException e) {
            //todo
            e.printStackTrace();
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
        preparedStatement = connection.prepareCall("SELECT 1 FROM users WHERE login =?");
        preparedStatement.setString(1, login);
        preparedStatement.execute();
        ResultSet result = preparedStatement.getResultSet();
        return result.next();
    }

}
