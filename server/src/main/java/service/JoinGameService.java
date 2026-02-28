package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthTokenAccess;
import dataaccess.MemoryGameAccess;
import model.*;

import java.util.Objects;

public class JoinGameService {
    public void joinGame(JoinGameRequest joinRequest, String authToken, MemoryGameAccess gameList, MemoryAuthTokenAccess authList) throws DataAccessException {
        GameData gameRetrieved = gameList.getGame(joinRequest.gameID());
        if(gameRetrieved == null){
            // If game doesn't exist
            throw new DataAccessException("Game ID Invalid");
        }
        // If game exists
        String username = authList.getAuth(authToken).username();
        GameData newPlayerJoined;
        if(Objects.equals(joinRequest.playerColor(), "BLACK")){
            newPlayerJoined = new GameData(gameRetrieved.gameID(), gameRetrieved.whiteUsername(), username, gameRetrieved.gameName(), gameRetrieved.game());
        }else {
            newPlayerJoined = new GameData(gameRetrieved.gameID(), username, gameRetrieved.blackUsername(), gameRetrieved.gameName(), gameRetrieved.game());
        }
        gameList.updateGame(newPlayerJoined);
    }
}
