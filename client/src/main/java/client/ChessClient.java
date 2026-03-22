package client;

import chess.ChessGame.TeamColor;
import com.google.gson.Gson;
import model.*;
import server.ServerFacade;
import exception.ResponseException;

import java.util.Arrays;
import java.util.Scanner;

import static chess.ChessGame.TeamColor.*;

public class ChessClient {
    private String visitorName = null;
    private String visitorAuth = null;
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println(" Welcome to the Chess Server. Sign in to start.");
        System.out.print(help());

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
                case "join" -> joinGame(params);
                case "create" -> createGame(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            // Correct number of parameters
            RegisterRequest registerAttempt = new RegisterRequest(params[0], params[1], params[2]);
            LoginRegisterResult result = server.register(registerAttempt);
            if(result != null){
                visitorAuth = result.authToken();
                state = State.SIGNEDIN;
                visitorName = params[0];
                return String.format("Logged in as %s", visitorName);
            }else{
                return "Error: Username Already Taken";
            }
        }
        throw new ResponseException("Expected Format: 'username' 'password' 'email'");
    }

    public String login(String... params) throws ResponseException {
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
        ListGamesResponse games = server.listGames();
        var result = new StringBuilder();
        var gson = new Gson();
        for (ListGamesData game : games.games()) {
            result.append(gson.toJson(game)).append('\n');
        }
        return result.toString();
    }

    public String joinGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 2) {
            try {
                int id = Integer.parseInt(params[0]);
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
                    server.joinGame(joinAttempt);
                    return String.format("You has joined Game %d as %s", id, playerColor);
                }
            } catch (NumberFormatException ignored) {
                throw new ResponseException("Error: First parameter should be a number");
            }
        }
        throw new ResponseException("Expected: <game id> 'WHITE' OR 'BLACK'");
    }

    public String createGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1) {
            System.out.println(params[0]);
            CreateGameRequest createAttempt = new CreateGameRequest(params[0]);
            server.createGame(createAttempt);
            return "You have successfully created a game";
        }
        throw new ResponseException("Expected: <NAME>");
    }

    // Solely for Testing Purposes
    public String clear() throws ResponseException {
        assertSignedIn();
        server.clear();
        return "You have cleared all data from the server";
    }

    private ListGamesData getGame(int id) throws ResponseException {
        for (ListGamesData game : server.listGames().games()) {
            if (game.gameID() == id) {
                return game;
            }
        }
        return null;
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
}