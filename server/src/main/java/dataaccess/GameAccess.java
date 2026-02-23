package dataaccess;
import model.GameData;

import java.util.Collection;

public interface GameAccess {
    Collection<GameData> listGames() throws DataAccessException;
    void createGame(GameData gd) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    void updateGame(GameData gd) throws DataAccessException;
    void deleteGames() throws DataAccessException;
}
