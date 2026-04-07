package server;

import com.google.gson.Gson;
import dataaccess.SQLAuthTokenAccess;
import exception.*;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import jakarta.websocket.OnOpen;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.*;
import websocket.messages.ServerMessage;

import java.io.IOException;

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
        int gameId = -1;
        Session session = wsMessageContext.session;

        try {
            UserGameCommand command = Serializer.fromJson(
                    wsMessageContext.message(), UserGameCommand.class);
            String username = getUsername(command.getAuthToken());

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, command);
                case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) command);
                case LEAVE -> leaveGame(session, username, command);
                case RESIGN -> resign(session, username, command);
            }
        } catch (Exception ex) {
            connections.sendMessage(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR));
        }
    }
    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(Session session, String username, UserGameCommand command) throws IOException {
        connections.add(command.getGameID(), session);

        // If a player joined, include color. If observer, don't
        var message = String.format("%s joined as COLOR", username);

        // Create 3 subclasses of ServerMessage. Instantiate those here and then send those as the message from connections
        // Error and notification have a string, Load_game has a game object it sends
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        var loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, "");
        connections.broadcast(session, notification);
        connections.sendMessage(session, loadGame);
    }

    private void leaveGame(Session session, String username, UserGameCommand command) throws IOException, ResponseException {
        String leaveMessage = username + "has left the game";
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, leaveMessage);
        connections.broadcast(session, notification);
        connections.remove(session);
    }

    public void makeMove(Session session, String username, MakeMoveCommand moveCommand) throws ResponseException {

        // When the move is received, update the game with the made move and send the notification

        try {
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            connections.broadcast(null, notification);
        } catch (Exception ex) {
            throw new ResponseException("Test");
        }
    }

    public void resign(Session session, String username, UserGameCommand command) throws ResponseException {
        try {
            String resignMessage = username + "resigned";
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, resignMessage);
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
}