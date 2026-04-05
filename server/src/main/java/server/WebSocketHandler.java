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
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
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
            gameId = command.getGameID();
            String username = getUsername(command.getAuthToken());
            connections.add(gameId, session); // Save the session

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, (ConnectCommand) command);
                case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) command);
                case LEAVE -> leaveGame(session, username, (LeaveGameCommand) command);
                case RESIGN -> resign(session, username, (ResignCommand) command);
            }
        } catch (Exception ex) {
            connections.sendMessage(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR));
        }
    }



    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(String authToken, String username, Session session) throws IOException {
        var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        connections.broadcast(session, notification);
    }

    private void leaveGame(String username, Session session) throws IOException, ResponseException {
        String leaveMessage = username + "has left the game";
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, leaveMessage);
        connections.broadcast(session, notification);
        connections.remove(session);
    }

    public void makeMove(String authToken, int gameID) throws ResponseException {
        try {
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            connections.broadcast(null, notification);
        } catch (Exception ex) {
            throw new ResponseException("Test");
        }
    }

    public void resign(Session session, String username, ResignCommand command) throws ResponseException {
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