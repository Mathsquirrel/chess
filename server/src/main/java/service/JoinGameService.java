package service;

import dataaccess.*;
import model.*;

import java.util.Objects;

public class JoinGameService {
    public void joinGame(JoinGameRequest joinRequest, String authToken, MemoryGameAccess gameList, MemoryAuthTokenAccess authList) throws BadRequestException, AlreadyTakenException {
        GameData gameRetrieved = gameList.getGame(joinRequest.gameID());
        if(gameRetrieved == null){
            // If game doesn't exist
            throw new BadRequestException("{Error: bad request}");
        }
        // If game exists
        String username = authList.getAuth(authToken).username();
        GameData newPlayerJoined;
        if(Objects.equals(joinRequest.playerColor(), "BLACK")){
            if(gameRetrieved.blackUsername() != null){
                throw new AlreadyTakenException("{Error: already taken}");
            }
            newPlayerJoined = new GameData(gameRetrieved.gameID(), gameRetrieved.whiteUsername(), username, gameRetrieved.gameName(), gameRetrieved.game());
        }else if(Objects.equals(joinRequest.playerColor(), "WHITE")) {
            if(gameRetrieved.whiteUsername() != null){
                throw new AlreadyTakenException("{Error: already taken}");
            }
            newPlayerJoined = new GameData(gameRetrieved.gameID(), username, gameRetrieved.blackUsername(), gameRetrieved.gameName(), gameRetrieved.game());
        }else {
            throw new BadRequestException("{Error: bad request}");
        }
        gameList.updateGame(newPlayerJoined);
    }
}
