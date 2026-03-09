package service;

import dataaccess.GameAccess;
import model.*;

import java.util.ArrayList;
import java.util.Collection;

public class ListGamesService {
    public ListGamesResponse listGames(GameAccess gameList) {
        Collection<ListGamesData> responseList = new ArrayList<>();
        for(GameData games : gameList.listGames()){
            // For each game in the list
            responseList.add(new ListGamesData(games.gameID(), games.whiteUsername(), games.blackUsername(), games.gameName()));
        }
        return new ListGamesResponse(responseList);
    }
}
