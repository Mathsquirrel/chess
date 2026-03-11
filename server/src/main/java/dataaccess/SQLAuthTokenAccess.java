package dataaccess;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;

import java.sql.Connection;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLAuthTokenAccess implements AuthTokenAccess{

    public void createAuth(AuthData newData) throws ResponseException {
        var statement = "INSERT INTO authData (username, authToken, json) VALUES (?, ?, ?)";
        String json = new Gson().toJson(newData);
        executeUpdate(statement, newData.username(), newData.authToken(), json);
    }

    public AuthData getAuth(String authToken) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT json FROM authData WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(String.format("Error: {Unable to read data: %s}", e.getMessage()));
        }
        return null;
    }

    public void clearAuth() throws ResponseException{
        var statement = "TRUNCATE authData";
        executeUpdate(statement);
    }

    public void deleteAuth(AuthData removeData) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM authData WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, removeData.authToken());
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new ResponseException(String.format("Error: {Unable to read data: %s}", e.getMessage()));
        }
    }

    private void executeUpdate(String statement, Object... params) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p){
                        ps.setString(i + 1, p);
                    }
                    else if (param == null){
                        ps.setNull(i + 1, NULL);
                    }
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    rs.getInt(1);
                }

            }
        } catch (SQLException e) {
            throw new ResponseException(String.format("Error: {Unable to update database: %s, %s}", statement, e.getMessage()));
        }
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        return new Gson().fromJson(json, AuthData.class);
    }
}
