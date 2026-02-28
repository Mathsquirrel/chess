package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthTokenAccess;
import dataaccess.MemoryGameAccess;
import model.*;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Collection;

public class ListGamesService {
    public Collection<ListGamesResponse> listGames(String authToken, MemoryGameAccess gameList, MemoryAuthTokenAccess authList) throws DataAccessException {
        if(authList.getAuth(authToken) != null){
            Collection<ListGamesResponse> responseList = new ArrayList<>();
            for(GameData games : gameList.listGames()){
                // For each game in the list
                responseList.add(new ListGamesResponse(games.gameID(), games.whiteUsername(), games.blackUsername(), games.gameName()));
            }
            return responseList;
        }
        throw new DataAccessException("Error: User Not Logged In");
    }
}
