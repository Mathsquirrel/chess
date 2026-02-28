package service;

import chess.ChessGame;
import dataaccess.MemoryGameAccess;
import model.*;

public class CreateGameService {
    public CreateGameResponse createGame(CreateGameRequest createGame, MemoryGameAccess gameList) {
        int gameID = gameList.listGames().toArray().length;
        GameData newGame = new GameData(gameID, null, null, createGame.gameName(), new ChessGame());
        gameList.createGame(newGame);
        return new CreateGameResponse(gameID);
    }
}
