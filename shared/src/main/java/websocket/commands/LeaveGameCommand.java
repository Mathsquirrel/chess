package websocket.commands;

public class LeaveGameCommand extends UserGameCommand{

    LeaveGameCommand(CommandType commandType, String authToken, Integer gameID){
        super(commandType, authToken, gameID);
    }
}
