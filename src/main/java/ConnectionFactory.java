import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Author: shim.
 * Creation date: 6/4/14.
 */
public class ConnectionFactory {

    private final Logger logger = LogManager.getLogger(ConnectionFactory.class);

    public static final String MYSQL_LOCAL_DB_URL = "jdbc:mysql://localhost:3306/ridesharing";
    public static final String MYSQL_LOCAL_DB_USER = "shim";
    public static final String MYSQL_LOCAL_DB_USER_PASSWORD = "shim";
    public static final String MYSQL_LIBRARY_CLASS_NAME = "com.mysql.jdbc.Driver";

    //static reference to itself
    private static ConnectionFactory instance = new ConnectionFactory();

    //private constructor
    private ConnectionFactory() {
        try {
            Class.forName(MYSQL_LIBRARY_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage());
        }
    }

    private Connection createConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(MYSQL_LOCAL_DB_URL,
                    MYSQL_LOCAL_DB_USER, MYSQL_LOCAL_DB_USER_PASSWORD);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return connection;
    }

    public static Connection getConnection() {
        return instance.createConnection();
    }
}
