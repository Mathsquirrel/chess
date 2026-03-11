package dataaccess;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import java.sql.Connection;
import java.sql.*;

import dataaccess.SQLUserAccess.*;

import static dataaccess.SQLUserAccess.executeUpdate;

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

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        return new Gson().fromJson(json, AuthData.class);
    }
}
