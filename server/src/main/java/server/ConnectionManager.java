package server;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public Map<Integer, List<Session>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, Session session) {
        if(!connections.containsKey(gameID)){
            // If Game not in list create it with empty playerList
            List<Session> players = new ArrayList<>();
            connections.put(gameID, players);
        }
        // Add player to list of players involved in game
        List<Session> playersInGame = connections.get(gameID);
        playersInGame.add(session);
        connections.put(gameID, playersInGame);
    }

    public void remove(Session session, int currentGameID) {
        // Removes user from a game
        connections.forEach((gameID, values) -> {
            if(currentGameID == gameID) {
                values.remove(session);
            }
        });
    }

    public void broadcast(Session excludeSession, ServerMessage notification, int gameID) throws IOException {
        String msg = notification.toString();
        for (Session session : connections.get(gameID)) {
            if (session.isOpen()) {
                if (!session.equals(excludeSession)) {
                    session.getRemote().sendString(msg);
                }
            }
        }
    }

    public void sendMessage(Session currentSession, ServerMessage notification) throws IOException{
        String msg = notification.toString();
        currentSession.getRemote().sendString(msg);
    }
}