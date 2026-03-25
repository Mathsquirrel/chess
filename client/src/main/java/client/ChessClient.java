package client;

import chess.*;
import chess.ChessGame.TeamColor;
import model.*;
import server.ServerFacade;
import exception.ResponseException;
import ui.PrintBoard;

import java.util.*;

import static chess.ChessGame.TeamColor.*;

public class ChessClient {
    private String visitorName = null;
    private String visitorAuth = "";
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private static int[] gameNums = new int[100];

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
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
                case "join" -> joinGame(params); // REMOVE COMMENTED CODE IF NEEDED
                case "create" -> createGame(params);
                case "observe" -> observeGame(params);
                case "quit" -> "quit";
                case "clear" -> clear(); // REMOVE AFTER PASSOFF
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
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
                if(!Collections.singletonList(gameNums).contains(id)){
                    throw new ResponseException("Error: Game ID does not exist");
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
                    PrintBoard.print(game.chessGame().game(), color);
                    return String.format("You has joined Game %d as %s", id, playerColor);
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

    // Solely for Testing Purposes
    public String clear() throws ResponseException {
        server.clear();
        state = State.SIGNEDOUT;
        return "You have cleared all data from the server";
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
                PrintBoard.print(game.chessGame().game(), WHITE);
                foundGame = true;
            }
        }
        if(foundGame){
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
        }
        return """
                - list - lists all games
                - join - <ID> [WHITE | BLACK] - joins a game
                - create - <NAME> - creates a game
                - observe - <ID> - observe a game
                - logout - logs out of account
                - quit - quit playing chess
                - help - shows all possible commands
                """;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException("You must sign in");
        }
    }

    private void assertSignedOut() throws ResponseException {
        if (state == State.SIGNEDIN) {
            throw new ResponseException("You must be signed out");
        }
    }
}