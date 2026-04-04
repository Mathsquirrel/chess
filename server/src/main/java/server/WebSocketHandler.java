package server;

import com.google.gson.Gson;
import exception.ResponseException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> enter(command.getAuthToken(), ctx.session);
                case MAKE_MOVE -> makeMove(command.getAuthToken(), command.getGameID());
                case RESIGN -> resign(command.getAuthToken(), command.getGameID());
                case LEAVE -> exit(command.getAuthToken(), ctx.session);
            }
        } catch (IOException | ResponseException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void enter(String authToken, Session session) throws IOException {
        connections.add(session);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        connections.broadcast(session, notification);
    }

    private void exit(String authToken, Session session) throws IOException {
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
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

    public void resign(String authToken, int gameID) throws ResponseException {
        try {
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            connections.broadcast(null, notification);
        } catch (Exception ex) {
            throw new ResponseException("Test");
        }
    }
}