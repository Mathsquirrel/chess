package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.List;

public class SQLGameAccess implements GameAccess{
    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void createGame(GameData gd) throws DataAccessException {

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(GameData gd) throws DataAccessException {

    }

    @Override
    public void deleteGames() {

    }
}
