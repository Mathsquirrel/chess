package dataaccess;

import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryGameAccess implements GameAccess {
    Collection<GameData> gameList = new ArrayList<>();// List of GameData objects or Games

    public Collection<GameData> listGames(){
        // Returns all games
        return gameList;
    }

    public void createGame(GameData gd) throws DataAccessException {
        // Adds new game to gameList
        gameList.add(gd);
    }

    public GameData getGame(int gameID) throws DataAccessException {
        // If gameID exists in GameList, return it
        for(GameData checker : gameList) {
            if (checker.gameID() == gameID) {
                return checker;
            }
        }// else
        return null;
    }

    public void updateGame(GameData gd) throws DataAccessException {
        // Remove old game and replace with updated game
        for(GameData checker: gameList){
            if(checker.gameID() == gd.gameID()){
                gameList.remove(checker);
                gameList.add(gd);
            }
        }
    }

    public void deleteGames(){
        // Reinitialize gameList to be empty
        gameList = new ArrayList<>();
    }
}
