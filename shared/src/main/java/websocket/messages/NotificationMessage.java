package websocket.messages;

public class NotificationMessage extends ServerMessage {
    String message;

    public NotificationMessage(String message){
        super(ServerMessageType.NOTIFICATION);
        this.message = message + "\n";
    }

    public String getServerMessage(){ return this.message;}
}
