package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import model.*;
import service.*;

import java.util.Collection;

public class Server {
    static Gson serializer = new Gson();
    static MemoryAuthTokenAccess authList = new MemoryAuthTokenAccess();
    static MemoryGameAccess gameList = new MemoryGameAccess();
    static MemoryUserAccess userList = new MemoryUserAccess();
    private final Javalin javalin;


    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.post("/session", Server::handleLogin);
        javalin.delete("/session", Server::handleLogout);
        javalin.delete("/db", Server::handleClear);
        javalin.post("/user", Server::handleRegister);
        javalin.put("/game", Server::handleJoinGame);
        javalin.post("/game", Server::handleCreateGame);
        javalin.get("/game", Server::handleListGames);

        javalin.exception(DataAccessException.class, (e, ctx) -> {
            ctx.status(500); // Set response status
            ctx.result(e.getMessage()); // Set response body
        });
        // Register your endpoints and exception handlers here.
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private static void handleLogin(Context ctx){
        // Handles logging in users
        // Possible handles errors
        LoginRequest loginRequest = serializer.fromJson(ctx.body(), LoginRequest.class);
        LoginService loginService = new LoginService();
        try {
            LoginRegisterResult response = loginService.login(loginRequest, userList, authList);
            ctx.result(serializer.toJson(response));
        } catch (DataAccessException e) {
            ctx.status(401);
            ctx.result(serializer.toJson(e.getMessage()));
        }
    }

    private static void handleLogout(Context ctx){
        // Handles logging user out
        // Possibly handles errors
        String logoutRequest = serializer.fromJson(ctx.header("authorization"), String.class);
        try {
            // Check authorization before logging out
            isAuthorized(logoutRequest);
            LogoutService logoutService = new LogoutService();
            logoutService.logout(logoutRequest, authList);
            ctx.result(serializer.toJson(null));
        }catch(DataAccessException e) {
            ctx.status(401);
            ctx.result(serializer.toJson(e.getMessage()));
        }
    }

    private static void handleClear(Context ctx){
        ctx.result("Handling Clear");
    }

    private static void handleRegister(Context ctx){
        // Handles registering a new user
        // Possible handles errors
        RegisterRequest registerRequest = serializer.fromJson(ctx.body(), RegisterRequest.class);
        RegisterService registerService = new RegisterService();
        try {
            LoginRegisterResult response = registerService.register(registerRequest, userList, authList);
            ctx.result(serializer.toJson(response));
        }catch(DataAccessException e){
            ctx.status(401);
            ctx.result(serializer.toJson(e.getMessage()));
        }
    }

    private static void handleJoinGame(Context ctx){
        ctx.result("Handling JoinGame");
    }

    private static void handleCreateGame(Context ctx){
        // Handles creating a new game
        // In-Progress
        CreateGameRequest gameName = serializer.fromJson(ctx.body(), CreateGameRequest.class);
        String authorization = serializer.fromJson(ctx.header("authorization"), String.class);
        CreateGameService gameService = new CreateGameService();
        try {
            // Check authorization before creating game
            isAuthorized(authorization);
            CreateGameResponse response = gameService.createGame(gameName, gameList);
            ctx.result(serializer.toJson(response));
        }catch(DataAccessException e){
            ctx.status(401);
            ctx.result(serializer.toJson(e.getMessage()));
        }
    }

    private static void handleListGames(Context ctx){
        // Handles listing all games
        // Possible Handles errors
        String listGamesRequest = serializer.fromJson(ctx.header("authorization"), String.class);
        ListGamesService listService = new ListGamesService();
        try {
            // Check for authorization before handling
            isAuthorized(listGamesRequest);
            Collection<ListGamesResponse> response = listService.listGames(gameList, authList);
            ctx.result(serializer.toJson(response));
        }catch(DataAccessException e){
            ctx.status(401);
            ctx.result(serializer.toJson(e.getMessage()));
        }
    }

    private static void isAuthorized(String authToken) throws DataAccessException{
        if(authList.getAuth(authToken) == null){
            // If authtoken doesn't exist
            throw new DataAccessException("unauthorized");
        }
    }
}