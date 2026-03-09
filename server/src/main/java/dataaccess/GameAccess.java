package dataaccess;
import exception.BadRequestException;
import exception.ResponseException;
import model.GameData;

import java.util.Collection;

public interface GameAccess {
    Collection<GameData> listGames() throws ResponseException;
    void createGame(GameData gd) throws BadRequestException, ResponseException;
    GameData getGame(int gameID) throws ResponseException;
    void updateGame(GameData gd) throws ResponseException;
    void deleteGames();
}
