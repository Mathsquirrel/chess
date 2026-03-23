package service;

import dataaccess.GameAccess;
import exception.ResponseException;
import model.*;

import java.util.ArrayList;
import java.util.Collection;

public class ListGamesService {
    public ListGamesResponse listGames(GameAccess gameList) throws ResponseException {
        Collection<ListGamesData> responseList = new ArrayList<>();
        for(GameData games : gameList.listGames()){
            // For each game in the list
            responseList.add(new ListGamesData(games.gameID(), games.whiteUsername(), games.blackUsername(), games.gameName(), games));
        }
        return new ListGamesResponse(responseList);
    }
}
