package websocket.messages;

public class ErrorMessage extends ServerMessage {
    String errorMessage;
    public ErrorMessage(String message){
        super(ServerMessageType.ERROR);
        this.errorMessage = '\n'+message;
    }

    public String getServerMessage(){
        return errorMessage;
    }
}
