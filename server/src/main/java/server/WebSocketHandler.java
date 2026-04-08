package server;

import chess.ChessGame;
import chess.ChessMove;
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
import java.util.Objects;

import static chess.ChessGame.TeamColor.*;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final Gson Serializer = new Gson();
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
            String errorMessage = "Error: Malformed Message";
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
        }else if(!authenticate(authToken)){
            errorMessage = "Error: Not Signed in or bad authorization";
            connections.sendMessage(session, new ErrorMessage(errorMessage));
        }else {
            connections.add(gameID, session);

            // If a player joined, include color. If observer, don't
            var message = String.format("%s joined as %s", username, getPlayerColor(username, gameID).toString());
            var notification = new NotificationMessage(message);
            ChessGame requestedGame = getGame(gameID);
            var loadGame = new LoadGameMessage(requestedGame);

            connections.broadcast(session, notification);
            connections.sendMessage(session, loadGame);
        }
    }

    private void leaveGame(Session session, String username, UserGameCommand command) throws IOException, ResponseException {
        String leaveMessage = username + "has left the game";
        var notification = new NotificationMessage(leaveMessage);
        connections.broadcast(session, notification);
        connections.remove(session);
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
        }else if(Objects.equals(checkGameEnd(changedGame), "Stalemate") || Objects.equals(checkGameEnd(changedGame), "Checkmate")) {
            // If the game has already ended
            connections.sendMessage(session, new ErrorMessage("Error: The game has already ended. You cannot make a move"));
        }else{
            if (changedGame.validMoves(attemptedMove.getStartPosition()).contains(attemptedMove)) {
                // If the move is actually valid, carry it out and return the new game
                changedGame.makeMove(attemptedMove);
                SQLGameAccess tempAccess = new SQLGameAccess();
                GameData oldGame = tempAccess.getGame(moveCommand.getGameID());
                GameData updatedGame = new GameData(oldGame.gameID(), oldGame.whiteUsername(), oldGame.blackUsername(),
                        oldGame.gameName(), changedGame);
                tempAccess.updateGame(updatedGame);

                String message = String.format("%s made the move %s %s", username,
                        attemptedMove.getStartPosition().toString(), attemptedMove.getEndPosition().toString());
                var moveMessage = new NotificationMessage(message);
                var game = new LoadGameMessage(changedGame);
                connections.broadcast(session, game);
                connections.sendMessage(session, game);
                connections.broadcast(session, moveMessage);
            } else {
                throw new ResponseException("Error: Move was invalid");
            }
        }
    }

    public void resign(Session session, String username, UserGameCommand command) throws ResponseException {

        // TESTS SAY THAT THIS PASSED BUT IT HAS NOT ACTUALLY PASSED YET. IT DOES NOT ACTUALLY END A GAME OR PREVENT MOVES

        try {
            String resignMessage = username + "resigned";
            var notification = new NotificationMessage(resignMessage);
            connections.broadcast(null, notification);
        } catch (Exception ex) {
            throw new ResponseException("Error: Unexpected Values");
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
        return authenticator.getAuth(authToken) != null;
    }

    private String checkGameEnd(ChessGame game){
        if (game.isInStalemate(WHITE) || game.isInStalemate(BLACK)) {
            return "Stalemate";
        }else if(game.isInCheckmate(WHITE) || game.isInCheckmate(BLACK)){
            return "Checkmate";
        }else{
            return "";
        }
    }

    private String getPlayerColor(String username, int gameID) throws ResponseException {
        SQLGameAccess colorRetriever = new SQLGameAccess();
        GameData retrievedGame = colorRetriever.getGame(gameID);
        if(Objects.equals(retrievedGame.blackUsername(), username)){
            return "Black";
        }else if(Objects.equals(retrievedGame.whiteUsername(), username)){
            return "White";
        }else{
            return "";
        }
    }
}