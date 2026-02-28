package service;

import dataaccess.MemoryGameAccess;
import model.*;

import java.util.ArrayList;
import java.util.Collection;

public class ListGamesService {
    public ListGamesResponse listGames(MemoryGameAccess gameList) {
        Collection<ListGamesData> responseList = new ArrayList<>();
        for(GameData games : gameList.listGames()){
            // For each game in the list
            responseList.add(new ListGamesData(games.gameID(), games.whiteUsername(), games.blackUsername(), games.gameName()));
        }
        return new ListGamesResponse(responseList);
    }
}
