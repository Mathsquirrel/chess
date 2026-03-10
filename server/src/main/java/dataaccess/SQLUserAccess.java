package dataaccess;

import com.google.gson.Gson;
import exception.ResponseException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLUserAccess implements UserAccess{

    public void createUser(UserData user) throws ResponseException {
        var statement = "INSERT INTO userData (username, password, email, json) VALUES (?, ?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        UserData hashedUser = new UserData(user.username(), hashedPassword, user.email());
        String json = new Gson().toJson(hashedUser);
        executeUpdate(statement, user.username(), hashedPassword, user.email(), json);
    }

    public UserData getUser(String requestedUsername) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, json FROM userData WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, requestedUsername);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(ResponseException.Code.ServerError, String.format("Error: {Unable to read data: %s}", e.getMessage()));
        }
        return null;
    }

    public void clear() throws ResponseException{
        var statement = "TRUNCATE userData";
        executeUpdate(statement);
    }

    public Collection<UserData> getUserList() throws ResponseException {
        Collection<UserData> result = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT json FROM userData";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readUser(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(ResponseException.Code.ServerError, String.format("Error: {Unable to read data: %s}", e.getMessage()));
        }
        return result;
    }

    private void executeUpdate(String statement, Object... params) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    rs.getInt(1);
                }

            }
        } catch (SQLException e) {
            throw new ResponseException(ResponseException.Code.ServerError, String.format("Error: {Unable to update database: %s, %s}", statement, e.getMessage()));
        }
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        return new Gson().fromJson(json, UserData.class);
    }

    public boolean verifyPasswords(String username, String providedClearTextPassword) throws ResponseException {
        // read the previously hashed password from the database
        var hashedPassword = readHashedPasswordFromDatabase(username);
        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }

    private String readHashedPasswordFromDatabase(String username) throws ResponseException {
        UserData user = getUser(username);
        return user.password();
    }
}
