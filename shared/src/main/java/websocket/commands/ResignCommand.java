package websocket.commands;

public class ResignCommand extends UserGameCommand{

    ResignCommand(CommandType commandType, String authToken, Integer gameID){
        super(commandType, authToken, gameID);
    }
}
