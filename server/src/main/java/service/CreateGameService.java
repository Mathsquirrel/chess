package service;

import chess.ChessGame;
import exception.BadRequestException;
import dataaccess.*;
import exception.ResponseException;
import model.*;

public class CreateGameService {
    public CreateGameResponse createGame(CreateGameRequest createGame, GameAccess gameList) throws BadRequestException, ResponseException {
        if(createGame.gameName() == null){
            throw new BadRequestException("{Error: bad request}");
        }
        int gameID = gameList.listGames().toArray().length + 1;
        GameData newGame = new GameData(gameID, null, null, createGame.gameName(), new ChessGame());
        gameList.createGame(newGame);
        return new CreateGameResponse(gameID);
    }
}
