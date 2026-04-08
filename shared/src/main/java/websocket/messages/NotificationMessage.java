package websocket.messages;

public class NotificationMessage extends ServerMessage {
    public NotificationMessage(String message){
        super(ServerMessageType.NOTIFICATION);
        this.message = '\n'+message;
    }
}
