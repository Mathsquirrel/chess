package dataaccess;
import model.GameData;

import java.util.Collection;

public interface GameAccess {
    Collection<GameData> listGames();
    void createGame(GameData gd) throws BadRequestException;
    GameData getGame(int gameID);
    void updateGame(GameData gd);
    void deleteGames();
}
