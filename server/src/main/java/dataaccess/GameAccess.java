package dataaccess;
import model.GameData;

import java.util.Collection;

public interface GameAccess {
    Collection<GameData> listGames() throws DataAccessException;
    void createGame(GameData gd) throws DataAccessException;
    GameData getGame(GameData gd) throws DataAccessException;
    void updateGame(GameData gd) throws DataAccessException;

}
