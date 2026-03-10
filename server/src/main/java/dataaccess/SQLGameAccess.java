package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.List;

public class SQLGameAccess implements GameAccess{

    public Collection<GameData> listGames() {
        return List.of();
    }

    @Override
    public void createGame(GameData gd) {

    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public void updateGame(GameData gd) {

    }

    @Override
    public void deleteGames() {

    }
}
