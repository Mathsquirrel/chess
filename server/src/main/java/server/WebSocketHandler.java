package server;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import exception.*;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static chess.ChessGame.TeamColor.*;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final Gson Serializer = new Gson();
    private final String[] boardLetters = {"a", "b", "c", "d", "e", "f", "g", "h"};
    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext wsMessageContext) throws Exception {
        Session session = wsMessageContext.session;

        try {
            UserGameCommand command = Serializer.fromJson(
                    wsMessageContext.message(), UserGameCommand.class);
            String username = getUsername(command.getAuthToken());

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, command);
                case MAKE_MOVE -> makeMove(session, username, Serializer.fromJson(wsMessageContext.message(), MakeMoveCommand.class));
                case LEAVE -> leaveGame(session, username, command);
                case RESIGN -> resign(session, username, command);
            }
        } catch (Exception ex) {
            String errorMessage = ex.getMessage();
            connections.sendMessage(session, new ErrorMessage(errorMessage));
        }
    }
    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(Session session, String username, UserGameCommand command) throws IOException, ResponseException {
        var temp = getGame(command.getGameID());
        var authToken = command.getAuthToken();
        int gameID = command.getGameID();
        String errorMessage;
        if(temp == null){
            errorMessage = "Error: Invalid GameID";
            connections.sendMessage(session, new ErrorMessage(errorMessage));
        }else if(authenticate(authToken)){
            errorMessage = "Error: Not Signed in or bad authorization";
            connections.sendMessage(session, new ErrorMessage(errorMessage));
        }else {
            connections.add(gameID, session);

            // If a player joined, include color. If observer, don't
            var message = String.format("%s joined as %s", username, getPlayerColor(username, gameID));
            var notification = new NotificationMessage(message);
            ChessGame requestedGame = getGame(gameID);
            var loadGame = new LoadGameMessage(requestedGame);

            connections.broadcast(session, notification, gameID);
            connections.sendMessage(session, loadGame);
        }
    }

    private void leaveGame(Session session, String username, UserGameCommand command) throws IOException, ResponseException {
        String leaveMessage = username + "has left the game";
        var notification = new NotificationMessage(leaveMessage);
        SQLGameAccess leaveGameUpdate = new SQLGameAccess();
        String playerColor = getPlayerColor(username, command.getGameID());
        GameData oldGame = leaveGameUpdate.getGame(command.getGameID());
        GameData newGame = oldGame;
        if(playerColor.equals("White")){
            newGame = new GameData(oldGame.gameID(), null, oldGame.blackUsername(),
                    oldGame.gameName(), oldGame.game());
        }else if(playerColor.equals("Black")){
            newGame = new GameData(oldGame.gameID(), oldGame.whiteUsername(), null,
                    oldGame.gameName(), oldGame.game());
        }
        leaveGameUpdate.updateGame(newGame);
        connections.broadcast(session, notification, oldGame.gameID());
        connections.remove(session, newGame.gameID());
    }

    public void makeMove(Session session, String username, MakeMoveCommand moveCommand) throws ResponseException, IOException, InvalidMoveException {
        String playerColor = getPlayerColor(username, moveCommand.getGameID());
        ChessGame.TeamColor playerTrueColor = null;
        // When the move is received, update the game with the made move and send the notification
        ChessGame changedGame = getGame(moveCommand.getGameID());
        ChessMove attemptedMove = moveCommand.getMove();
        if (playerColor.equals("White")) {
            playerTrueColor = WHITE;
        }else if(playerColor.equals("Black")){
            playerTrueColor = BLACK;
        }
        if (playerColor.isEmpty()){
            // If the person making a move is not a player
            connections.sendMessage(session, new ErrorMessage("Error: You are not a player"));
        }else if(playerTrueColor != changedGame.getBoard().getPiece(attemptedMove.getStartPosition()).getTeamColor()){
            // If the person making a move tries to move someone else's piece
            connections.sendMessage(session, new ErrorMessage("Error: You cannot move another player's piece"));
        }else if(changedGame.getTeamTurn() != playerTrueColor){
            // If it is not the player's turn
            connections.sendMessage(session, new ErrorMessage("Error: It is not your turn"));
        }else if(checkGameEnd(changedGame).contains("Stalemate") || checkGameEnd(changedGame).contains("Checkmate")) {
            // If the game has already ended
            connections.sendMessage(session, new ErrorMessage("Error: The game has already ended. You cannot make a move"));
        }else if(changedGame.getTeamTurn() == RESIGNED){
            // If a player has already resigned
            connections.sendMessage(session, new ErrorMessage("Error: A player has already resigned"));
        }else{
            if (changedGame.validMoves(attemptedMove.getStartPosition()).contains(attemptedMove)) {
                // If the move is actually valid, carry it out and return the new game
                changedGame.makeMove(attemptedMove);
                SQLGameAccess tempAccess = new SQLGameAccess();
                GameData oldGame = tempAccess.getGame(moveCommand.getGameID());
                GameData updatedGame = new GameData(oldGame.gameID(), oldGame.whiteUsername(), oldGame.blackUsername(),
                        oldGame.gameName(), changedGame);
                tempAccess.updateGame(updatedGame);

                String startPosition = convertToLetter(attemptedMove.getStartPosition().getColumn(),
                        attemptedMove.getStartPosition().getRow());
                String endPosition = convertToLetter(attemptedMove.getEndPosition().getColumn(),
                        attemptedMove.getEndPosition().getRow());
                ChessPiece.PieceType promotionPiece = attemptedMove.getPromotionPiece();
                String message;
                if(promotionPiece != null){
                    message = String.format("%s made the move %s %s->%s", username, startPosition, endPosition, promotionPiece);
                }else {
                    message = String.format("%s made the move %s %s", username, startPosition, endPosition);
                }
                var moveMessage = new NotificationMessage(message);
                var game = new LoadGameMessage(changedGame);
                connections.broadcast(session, game, updatedGame.gameID());
                connections.sendMessage(session, game);
                connections.broadcast(session, moveMessage, updatedGame.gameID());

                // Check for additional messages
                List<String> gameState = checkGameEnd(changedGame);
                String colorInCheckmate;
                String gameEnd = "The game has ended ";
                if(gameState.contains("Stalemate")){
                    connections.broadcast(session, new NotificationMessage(gameEnd + "in stalemate!"), updatedGame.gameID());
                    connections.sendMessage(session, new NotificationMessage(gameEnd + "in stalemate!"));
                }else if(gameState.contains("Checkmate")) {
                    if(gameState.contains("Black")){
                        colorInCheckmate = "Black";
                    }else{
                        colorInCheckmate = "White";
                    }
                    gameEnd = String.format("%s with %s in checkmate!", gameEnd, colorInCheckmate);
                    connections.broadcast(session, new NotificationMessage(gameEnd), updatedGame.gameID());
                    connections.sendMessage(session, new NotificationMessage(gameEnd));
                }else if(changedGame.isInCheck(WHITE)){
                    connections.broadcast(session, new NotificationMessage("White is in Check!"), updatedGame.gameID());
                    connections.sendMessage(session, new NotificationMessage("White is in Check!"));
                }else if(changedGame.isInCheck(BLACK)){
                    connections.broadcast(session, new NotificationMessage("Black is in Check!"), updatedGame.gameID());
                    connections.sendMessage(session, new NotificationMessage("Black is in Check!"));
                }
            } else {
                throw new ResponseException("Error: Move was invalid");
            }
        }
    }

    public void resign(Session session, String username, UserGameCommand command) throws ResponseException, IOException {
        ChessGame resignedGame = getGame(command.getGameID());
        String playerColor = getPlayerColor(username, command.getGameID());
        if(authenticate(command.getAuthToken())){
            connections.sendMessage(session, new ErrorMessage("Error: You are not authorized"));
        }else if(resignedGame.getTeamTurn() == RESIGNED){
            connections.sendMessage(session, new ErrorMessage("Error: The other user has already resigned"));
        }else if(playerColor.equals("Observer")){
            connections.sendMessage(session, new ErrorMessage("Error: Observers cannot resign"));
        }
        else{
            SQLGameAccess resignedGameUpdater = new SQLGameAccess();
            resignedGame.setTeamTurn(RESIGNED);
            GameData oldResignedGame = resignedGameUpdater.getGame(command.getGameID());
            GameData newResignedGame = new GameData(oldResignedGame.gameID(), oldResignedGame.whiteUsername(),
                    oldResignedGame.blackUsername(), oldResignedGame.gameName(), resignedGame);
            resignedGameUpdater.updateGame(newResignedGame);

            String resignMessage = String.format("%s has resigned the game!", username);
            connections.sendMessage(session, new NotificationMessage(resignMessage));
            connections.broadcast(session, new NotificationMessage(resignMessage), newResignedGame.gameID());
        }
    }

    private String getUsername(String authToken) throws ResponseException {
        SQLAuthTokenAccess authData = new SQLAuthTokenAccess();
        AuthData requestedUser = authData.getAuth(authToken);
        if(requestedUser == null){
            return null;
        }
        return requestedUser.username();
    }

    private ChessGame getGame(int gameID) throws ResponseException {
        SQLGameAccess gameData = new SQLGameAccess();
        return gameData.getGame(gameID).game();
    }

    private boolean authenticate(String authToken) throws ResponseException {
        SQLAuthTokenAccess authenticator = new SQLAuthTokenAccess();
        return authenticator.getAuth(authToken) == null;
    }

    private List<String> checkGameEnd(ChessGame game){
        List<String> gameResult = new ArrayList<>();
        if (game.isInStalemate(WHITE) || game.isInStalemate(BLACK)){
            gameResult.add("Stalemate");
        }else if(game.isInCheckmate(WHITE)){
            gameResult.add("Checkmate");
            gameResult.add("White");
        }else if(game.isInCheckmate(BLACK)){
            gameResult.add("Checkmate");
            gameResult.add("Black");
        }
        return gameResult;
    }

    private String getPlayerColor(String username, int gameID) throws ResponseException {
        SQLGameAccess colorRetriever = new SQLGameAccess();
        GameData retrievedGame = colorRetriever.getGame(gameID);
        if(Objects.equals(retrievedGame.blackUsername(), username)){
            return "Black";
        }else if(Objects.equals(retrievedGame.whiteUsername(), username)){
            return "White";
        }else{
            return "Observer";
        }
    }

    private String convertToLetter(int col, int row){
        String columnLetter = boardLetters[col -1];
        return String.format("%s%d", columnLetter, row);
    }
}