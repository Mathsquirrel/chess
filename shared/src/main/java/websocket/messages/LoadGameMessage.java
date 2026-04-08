package websocket.messages;

import chess.ChessGame;
import com.google.gson.Gson;

public class LoadGameMessage extends ServerMessage {
    ChessGame game;

    public LoadGameMessage(ChessGame game){
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public String getServerMessage() {
        return new Gson().toJson(game, ChessGame.class);
    }
}
