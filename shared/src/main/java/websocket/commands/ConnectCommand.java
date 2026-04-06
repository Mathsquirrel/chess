package websocket.commands;

public class ConnectCommand extends UserGameCommand{

    ConnectCommand(CommandType commandType, String authToken, Integer gameID){
        super(commandType, authToken, gameID);
    }
}
