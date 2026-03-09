package dataaccess;

import exception.ResponseException;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static String databaseName;
    private static String dbUsername;
    private static String dbPassword;
    private static String connectionUrl;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        loadPropertiesFromResources();
    }
    public DatabaseManager() throws ResponseException {
        configureDatabase();
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DatabaseManager.getConnection()) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws ResponseException {
        try {
            //do not wrap the following line with a try-with-resources
            var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
            conn.setCatalog(databaseName);
            return conn;
        } catch (SQLException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private static void loadPropertiesFromResources() {
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (propStream == null) {
                throw new Exception("Unable to load db.properties");
            }
            Properties props = new Properties();
            props.load(propStream);
            loadProperties(props);
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties", ex);
        }
    }

    private static void loadProperties(Properties props) {
        databaseName = props.getProperty("db.name");
        dbUsername = props.getProperty("db.user");
        dbPassword = props.getProperty("db.password");

        var host = props.getProperty("db.host");
        var port = Integer.parseInt(props.getProperty("db.port"));
        connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  userData (
              `username` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`username`),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  authData (
              `authtoken` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`authtoken`),
              INDEX(authtoken),
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  gameData (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`gameID`),
              INDEX(gameID),
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    /**
     * Creates the database if it does not already exist.
     */
    static public void createDatabase() throws ResponseException {
        var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
        try (var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private void configureDatabase() throws ResponseException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
