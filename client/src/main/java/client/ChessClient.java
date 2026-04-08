package client;

import chess.*;
import chess.ChessGame.TeamColor;
import client.websocket.WebSocketFacade;
import com.google.gson.Gson;
import model.*;
import server.ServerFacade;
import exception.ResponseException;
import ui.PrintBoard;
import websocket.commands.*;
import websocket.messages.ServerMessage;
import client.websocket.NotificationHandler;
import java.util.*;

import static chess.ChessGame.TeamColor.*;
import static client.State.INGAME;
import static chess.ChessPiece.PieceType.*;

public class ChessClient implements NotificationHandler{
    private String visitorName = null;
    private String visitorAuth = "";
    private int currentGameID = 0;
    private ChessGame currentGame = null;
    private TeamColor currentColor = null;
    private final ServerFacade server;
    private final WebSocketFacade ws;
    private State state = State.SIGNEDOUT;
    private static int[] gameNums = new int[100];
    private static final String[] rowLetters = {"a", "b", "c", "d", "e", "f", "g", "h"};

    public ChessClient(String serverUrl) throws ResponseException {
        server = new ServerFacade(serverUrl);
        ws = new WebSocketFacade(serverUrl, this);
    }

    public void run() {
        System.out.println(" Welcome to the Chess Server. Type 'help' for options.");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public void notify(ServerMessage notification) {
        // Switch case to handle the 3 types of messages, ERROR, LOAD_GAME, and NOTIFICATION
        switch(notification.getServerMessageType()){
            case ERROR:
            case NOTIFICATION:
                System.out.println(notification.getServerMessage());
                break;
            case LOAD_GAME:
                ChessGame printedGame = new Gson().fromJson(notification.getServerMessage(), ChessGame.class);
                PrintBoard.print(printedGame, currentColor, null);
                break;
        }
        printPrompt();
    }

    private void printPrompt() {
        System.out.print("\n[" + state.toString() + "]>>> ");
    }


    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout();
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "create" -> createGame(params);
                case "observe" -> observeGame(params);
                case "leave" -> leaveGame();
                case "redraw" -> redraw();
                case "move" -> makeMove(params);
                case "resign" -> resign();
                case "highlight" -> highlight(params);
                case "clear" -> clear();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException | InvalidMoveException ex) {
            return ex.getMessage();
        }
    }

    public String leaveGame() throws ResponseException {
        ws.leaveGame(visitorAuth, currentGameID);
        return "";
    }

    public String redraw() throws ResponseException {
        assertInGame();
        PrintBoard.print(currentGame, currentColor, null);
        return "";
    }

    public String makeMove(String... params) throws ResponseException, InvalidMoveException {

        // Might still need to color correct based on board

        if(params.length != 2){
            throw new ResponseException("Expected format: move <COLROW> <COLROW> (Ex: a7 a8->Queen) (->PIECE only needed for promotion)");
        }
        int[] startCoords = validateInput(params[0]);
        ChessPosition startPosition = new ChessPosition(startCoords[0], startCoords[1]);
        int[] endCoords = validateInput(params[1]);
        ChessPosition endPosition = new ChessPosition(endCoords[0], endCoords[1]);
        ChessPiece.PieceType promotionPiece = null;
        if(currentGame.getBoard().getPiece(startPosition).getPieceType() == PAWN){
            if(endPosition.getRow() == 1 || endPosition.getRow() == 8){
                // If pawn would promote
                if(params[1].length() == 2){
                    throw new ResponseException("Error: Pawn will promote. Should include ->PIECE");
                }
                String promotion = params[1].substring(2);
                switch(promotion.toLowerCase()){
                    case "king":
                        promotionPiece = KING;
                    case "queen":
                        promotionPiece = QUEEN;
                    case "rook":
                        promotionPiece = ROOK;
                    case "bishop":
                        promotionPiece = BISHOP;
                    case "knight":
                        promotionPiece = KNIGHT;
                    default:
                        throw new ResponseException("Error: Expected a non-pawn piece to promote to");
                }
            }
        }
        ChessMove attemptedMove = new ChessMove(startPosition, endPosition, promotionPiece);
        ws.makeMove(visitorAuth, currentGameID, attemptedMove);
        return "";
    }

    public String resign() throws ResponseException {
        ws.resign(visitorAuth, currentGameID);
        return "";
    }

    public String highlight(String... params) throws ResponseException {
        int[] coords = validateInput(params);
        int row = coords[0];
        int col = coords[1];
        if(params.length == 1) {
            if(params[0].length() != 2){
                throw new ResponseException("Error: Expected <COLROW> (Ex: a2)");
            }

            ChessPosition highlightSquare = new ChessPosition(row, col);
            PrintBoard.highlight(currentGame, currentColor, highlightSquare);
            return "";
        }
        throw new ResponseException("Error: Expected <COLROW> (Ex: a2)");
    }

    public String clear() throws ResponseException {
        server.clear();
        return "You have cleared the database";
    }

    public String register(String... params) throws ResponseException {
        assertSignedOut();
        if (params.length == 3) {
            // Correct number of parameters
            RegisterRequest registerAttempt = new RegisterRequest(params[0], params[1], params[2]);
            LoginRegisterResult result;
            try {
                result = server.register(registerAttempt);
            }catch(Exception e){
                return "Error: Username Already Taken";
            }
            if(result != null){
                visitorAuth = result.authToken();
                state = State.SIGNEDIN;
                visitorName = params[0];
                return String.format("Logged in as %s", visitorName);
            }
        }
        throw new ResponseException("Expected Format: 'username' 'password' 'email'");
    }

    public String login(String... params) throws ResponseException {
        assertSignedOut();
        if (params.length == 2) {
            // Correct number of parameters
            LoginRequest attempt = new LoginRequest(params[0], params[1]);
            LoginRegisterResult result = server.login(attempt);
            if(result != null){
                visitorAuth = result.authToken();
                state = State.SIGNEDIN;
                visitorName = params[0];
                return String.format("Logged in as %s", visitorName);
            }else{
                return "Invalid Credentials: Username or Password is incorrect";
            }

        }
        throw new ResponseException("Expected Format: 'username' 'password'");
    }

    public String logout(String... params) throws ResponseException {
        assertSignedIn();
        if(params.length == 0) {
            server.logout(visitorAuth);
                state = State.SIGNEDOUT;
                return String.format("%s has successfully logged out", visitorName);
        }else {
            return "Expected no arguments but received 1 or more";
        }
    }

    public String listGames() throws ResponseException {
        assertSignedIn();
        ListGamesResponse games = server.listGames(visitorAuth);
        var result = new StringBuilder();
        gameNums = new int[100];
        int numGames = 1;
        for (ListGamesData game : games.games()) {
            String oneData = numGames + " " + game.gameName()
                    + " White: " + game.whiteUsername() + " Black: "+ game.blackUsername();
            result.append(oneData).append('\n');
            gameNums[numGames] = numGames;
            numGames ++;
        }
        return result.toString();
    }

    public String joinGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 2) {
            try {
                // Convert provided ID to gameID
                int id = Integer.parseInt(params[0]);
                if(!contains(gameNums, id)){
                    throw new ResponseException("Error: GameID does not exist");
                }
                id = gameNums[id];
                ListGamesData game = getGame(id);
                String playerColor = params[1].toLowerCase();
                TeamColor color;
                if(playerColor.equals("black")){
                    color = BLACK;
                }else if(playerColor.equals("white")){
                    color = WHITE;
                }else{
                    throw new ResponseException("Error: Player color was not 'White' OR 'Black'");
                }
                if (game != null) {
                    JoinGameRequest joinAttempt = new JoinGameRequest(color, id);
                    server.joinGame(joinAttempt, visitorAuth);
                    currentGame = game.chessGame().game();
                    currentColor = color;
                    state = INGAME;
                    PrintBoard.print(currentGame, color, null);
                    ws.joinedGame(visitorAuth, game.gameID());
                    currentGameID = game.gameID();
                    return String.format("You have joined Game %d as %s", id, playerColor);
                }
            } catch (NumberFormatException ignored) {
                throw new ResponseException("Error: First parameter should be the Game's ID");
            }
        }
        throw new ResponseException("Expected: <game id> 'WHITE' OR 'BLACK'");
    }

    public String createGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1) {
            CreateGameRequest createAttempt = new CreateGameRequest(params[0]);
            server.createGame(createAttempt, visitorAuth);
            return "You have successfully created a game";
        }
        throw new ResponseException("Expected: <NAME>");
    }

    private ListGamesData getGame(int id) throws ResponseException {
        for (ListGamesData game : server.listGames(visitorAuth).games()) {
            if (game.gameID() == id) {
                return game;
            }
        }
        return null;
    }

    private String observeGame(String... params) throws ResponseException {
        if(params.length != 1){
            throw new ResponseException("Error: Expected <ID>");
        }
        int id;
        try {
            id = Integer.parseInt(params[0]);
        }catch(Exception e){
            throw new ResponseException("Error: Did not provide a number");
        }
        boolean foundGame = false;
        for (ListGamesData game : server.listGames(visitorAuth).games()) {
            if (game.gameID() == id) {
                PrintBoard.print(game.chessGame().game(), WHITE, null);
                foundGame = true;
            }
        }
        if(foundGame){
            state = INGAME;
            return "You have joined as an observer";
        }else{
            return "The ID you provided did not match any games";
        }
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - register <USERNAME> <PASSWORD> <EMAIL> - creates an account
                    - login <USERNAME> <PASSWORD> - signs into an account
                    - quit - quit playing chess
                    - help - shows all possible commands
                    """;
        }else if(state == State.SIGNEDIN) {
            return """
                    - list - lists all games
                    - join - <ID> [WHITE | BLACK] - joins a game
                    - create - <NAME> - creates a game
                    - observe - <ID> - observe a game
                    - logout - logs out of account
                    - quit - quit playing chess
                    - help - shows all possible commands
                    """;
        }else{
            return """
                    - redraw - redraws the chess board
                    - leave - leaves the game
                    - move <COLROW> <COLROW> (Ex: a7 a8->Queen) - moves the piece at the first position to the second. (->PIECE needed for promotion)
                    - resign - forfeit and end the game
                    - highlight <COLROW> (Ex:a2) - highlight all legal moves for that piece
                    - help - shows all possible commands
                    """;
        }
    }

    private void assertSignedIn() throws ResponseException {
        if (state != State.SIGNEDIN) {
            throw new ResponseException("You must sign in and not be in a game");
        }
    }

    private void assertSignedOut() throws ResponseException {
        if (state != State.SIGNEDOUT) {
            throw new ResponseException("You must be signed out");
        }
    }

    private void assertInGame() throws ResponseException{
        if(state != INGAME){
            throw new ResponseException("You must be in a game");
        }
    }

    private boolean contains(int[] array, int key) {
        for (int i : array) {
            if (i == key) {
                return true;
            }
        }
        return false;
    }

    private int[] validateInput(String... params) throws ResponseException {
        String[] coords = new String[2];
        coords[0] = params[0].substring(0,1);
        coords[1] = params[0].substring(1,2);
        if(!Arrays.asList(rowLetters).contains(coords[0]) || !coords[1].matches("\\d+")){
            // If column isn't an actual column letter or the row isn't a number
            throw new ResponseException("Error: Col wasn't an expected letter or Row wasn't a number");
        }
        int row = Integer.parseInt(coords[1]);
        int col = Arrays.asList(rowLetters).indexOf(coords[0]) + 1;
        if(currentColor == BLACK){
            col = 9-col;
        }
        if(row < 1 || row > 8){
            // If the row was out of bounds
            throw new ResponseException("Error: Given Row wasn't between 1 and 8");
        }
        return new int[]{row, col};
    }
}