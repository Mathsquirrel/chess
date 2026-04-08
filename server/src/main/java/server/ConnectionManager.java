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

    public void remove(Session session) {
        // Removes user from a game
        connections.forEach((gameID, values) -> {
            for(Session c : values){
                if(c.isOpen()){
                    if(c.equals(session)){
                        values.remove(session);
                        connections.put(gameID, values);
                    }
                }
            }
        });
    }

    public void broadcast(Session excludeSession, ServerMessage notification) throws IOException {
        String msg = notification.toString();
        for (List<Session> game : connections.values()) {
            for (Session c : game) {
                if (c.isOpen()) {
                    if (!c.equals(excludeSession)) {
                        c.getRemote().sendString(msg);
                    }
                }
            }
        }
    }

    public void sendMessage(Session currentSession, ServerMessage notification) throws IOException{
        String msg = notification.toString();
        currentSession.getRemote().sendString(msg);
    }
}