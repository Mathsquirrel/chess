package dataaccess;

import com.google.gson.Gson;
import exception.ResponseException;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLGameAccess implements GameAccess{

    public Collection<GameData> listGames() throws ResponseException {
        Collection<GameData> result = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT json FROM gameData";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(ResponseException.Code.ServerError, String.format("Error: {Unable to read data: %s}", e.getMessage()));
        }
        return result;
    }

    public void createGame(GameData gameData) throws ResponseException {
        var statement = "INSERT INTO gameData (gameID, whiteUsername, blackUsername, gameName, chessGame, json) VALUES (?, ?, ?, ?, ?, ?)";
        String chessData = new Gson().toJson(gameData.game());
        String json = new Gson().toJson(gameData);
        executeUpdate(statement, gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(),
                gameData.gameName(), chessData, json);
    }

    public GameData getGame(int gameID) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT json FROM gameData WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(ResponseException.Code.ServerError, String.format("Error: {Unable to read data: %s}", e.getMessage()));
        }
        return null;
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        return new Gson().fromJson(json, GameData.class);
    }

    public void updateGame(GameData gameData) throws ResponseException{
        var statement = "UPDATE gameData SET chessGame=?, json=? WHERE gameID=?";
        String json = new Gson().toJson(gameData);
        String chessGame = new Gson().toJson(gameData.game());
        executeUpdate(statement, chessGame, json, gameData.gameID());
    }

    public void deleteGames() throws ResponseException{
        var statement = "TRUNCATE gameData";
        executeUpdate(statement);
    }

    private void executeUpdate(String statement, Object... params) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof Integer p){
                        ps.setInt(i + 1, p);
                    }
                    else if (param instanceof String p) {
                        ps.setString(i + 1, p);
                    }
                    else if (param == null) {
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
            throw new ResponseException(ResponseException.Code.ServerError,
                    String.format("Error: {unable to update database: %s, %s}", statement, e.getMessage()));
        }
    }
}
