package server;

import com.google.gson.Gson;
import dataaccess.*;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.DataAccessException;
import exception.ResponseException;
import io.javalin.*;
import io.javalin.http.Context;
import model.*;
import service.*;

public class Server {
    static Gson serializer = new Gson();
    static AuthTokenAccess authList;
    static GameAccess gameList;
    static UserAccess userList;
    private final Javalin javalin;


    public Server() {
        authList = new SQLAuthTokenAccess();
        gameList = new SQLGameAccess();
        userList = new SQLUserAccess();
        try{
            new DatabaseManager();
        }catch(ResponseException e){
            System.out.printf("Unable to start server: %s%n", e.getMessage());
        }
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.post("/session", Server::handleLogin);
        javalin.delete("/session", Server::handleLogout);
        javalin.delete("/db", Server::handleClear);
        javalin.post("/user", Server::handleRegister);
        javalin.put("/game", Server::handleJoinGame);
        javalin.post("/game", Server::handleCreateGame);
        javalin.get("/game", Server::handleListGames);

        javalin.exception(BadRequestException.class, (e, ctx) -> {
            ctx.status(400);
            ErrorResponse returnedError = new ErrorResponse(e.getMessage());
            ctx.result(serializer.toJson(returnedError));
        });

        javalin.exception(DataAccessException.class, (e, ctx) -> {
            ctx.status(401);
            ErrorResponse returnedError = new ErrorResponse(e.getMessage());
            ctx.result(serializer.toJson(returnedError));
        });

        javalin.exception(AlreadyTakenException.class, (e, ctx) -> {
            ctx.status(403);
            ErrorResponse returnedError = new ErrorResponse(e.getMessage());
            ctx.result(serializer.toJson(returnedError));
        });

        javalin.exception(ResponseException.class, (e, ctx) -> {
            ctx.status(500);
            ErrorResponse returnedError = new ErrorResponse(e.getMessage());
            ctx.result(serializer.toJson(returnedError));
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

    private static void handleLogin(Context ctx) throws DataAccessException, BadRequestException, ResponseException {
        // Handles logging in users
        // Possible handles errors
        LoginRequest loginRequest = serializer.fromJson(ctx.body(), LoginRequest.class);
        LoginService loginService = new LoginService();
        LoginRegisterResult response = loginService.login(loginRequest, userList, authList);
        ctx.result(serializer.toJson(response));
    }

    private static void handleLogout(Context ctx) throws DataAccessException, ResponseException {
        // Handles logging user out
        // Possibly handles errors
        String logoutRequest = ctx.header("authorization");
        // Check authorization before logging out
        isAuthorized(logoutRequest);
        LogoutService logoutService = new LogoutService();
        logoutService.logout(logoutRequest, authList);
        ctx.result(serializer.toJson(new LogoutResponse("{}")));
    }

    private static void handleClear(Context ctx) throws ResponseException {
        // Handles clearing the databases
        ClearService clearService = new ClearService();
        clearService.clearAuths(authList);
        clearService.clearUsers(userList);
        clearService.clearGames(gameList);
        ctx.result(serializer.toJson(new LogoutResponse("{}")));
    }

    private static void handleRegister(Context ctx) throws BadRequestException, AlreadyTakenException, ResponseException {
        // Handles registering a new user
        // Possible handles errors
        RegisterRequest registerRequest = serializer.fromJson(ctx.body(), RegisterRequest.class);
        RegisterService registerService = new RegisterService();
        LoginRegisterResult response = registerService.register(registerRequest, userList, authList);
        ctx.result(serializer.toJson(response));
    }

    private static void handleJoinGame(Context ctx) throws DataAccessException, BadRequestException, AlreadyTakenException, ResponseException {
        // Handles joining a game
        JoinGameRequest gameRequest = serializer.fromJson(ctx.body(), JoinGameRequest.class);
        // Check authorization before creating game
        String joinAuthorization = ctx.header("authorization");
        isAuthorized(joinAuthorization);

        JoinGameService joinService = new JoinGameService();
        joinService.joinGame(gameRequest, joinAuthorization, gameList, authList);
        ctx.result(serializer.toJson(null));
    }

    private static void handleCreateGame(Context ctx) throws DataAccessException, BadRequestException, ResponseException {
        // Handles creating a new game
        // Possible Handles errors
        CreateGameRequest gameName = serializer.fromJson(ctx.body(), CreateGameRequest.class);
        String authorization = ctx.header("authorization");
        CreateGameService createService = new CreateGameService();
        // Check authorization before creating game
        isAuthorized(authorization);
        CreateGameResponse response = createService.createGame(gameName, gameList);
        ctx.result(serializer.toJson(response));
    }

    private static void handleListGames(Context ctx) throws DataAccessException, ResponseException {
        // Handles listing all games
        // Possible Handles errors
        String authorization = ctx.header("authorization");
        // Check authorization before listing games
        isAuthorized(authorization);
        ListGamesService listService = new ListGamesService();
        ListGamesResponse response = listService.listGames(gameList);
        ctx.result(serializer.toJson(response));
    }

    private static void isAuthorized(String authToken) throws DataAccessException, ResponseException {
        if(authList.getAuth(authToken) == null){
            // If authtoken doesn't exist
            throw new DataAccessException("{ message: Error: unauthorized }");
        }
    }
}