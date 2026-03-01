package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import model.*;
import service.*;

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

        // Register your endpoints and exception handlers here.
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private static void handleLogin(Context ctx) throws DataAccessException, BadRequestException{
        // Handles logging in users
        // Possible handles errors
        LoginRequest loginRequest = serializer.fromJson(ctx.body(), LoginRequest.class);
        LoginService loginService = new LoginService();
        LoginRegisterResult response = loginService.login(loginRequest, userList, authList);
        ctx.result(serializer.toJson(response));
    }

    private static void handleLogout(Context ctx) throws DataAccessException{
        // Handles logging user out
        // Possibly handles errors
        String logoutRequest = ctx.header("authorization");
        // Check authorization before logging out
        isAuthorized(logoutRequest);
        LogoutService logoutService = new LogoutService();
        logoutService.logout(logoutRequest, authList);
        ctx.result(serializer.toJson(new LogoutResponse("{}")));
    }

    private static void handleClear(Context ctx){
        // Handles clearing the databases
        ClearService clearService = new ClearService();
        clearService.clearAuths(authList);
        clearService.clearUsers(userList);
        clearService.clearGames(gameList);
        ctx.result(serializer.toJson(new LogoutResponse("{}")));
    }

    private static void handleRegister(Context ctx) throws BadRequestException, AlreadyTakenException{
        // Handles registering a new user
        // Possible handles errors
        RegisterRequest registerRequest = serializer.fromJson(ctx.body(), RegisterRequest.class);
        RegisterService registerService = new RegisterService();
        LoginRegisterResult response = registerService.register(registerRequest, userList, authList);
        ctx.result(serializer.toJson(response));
    }

    private static void handleJoinGame(Context ctx) throws DataAccessException, BadRequestException, AlreadyTakenException{
        // Handles joining a game
        JoinGameRequest gameRequest = serializer.fromJson(ctx.body(), JoinGameRequest.class);
        // Check authorization before creating game
        String joinAuthorization = ctx.header("authorization");
        isAuthorized(joinAuthorization);

        JoinGameService joinService = new JoinGameService();
        joinService.joinGame(gameRequest, joinAuthorization, gameList, authList);
        ctx.result(serializer.toJson(null));
    }

    private static void handleCreateGame(Context ctx) throws DataAccessException, BadRequestException{
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

    private static void handleListGames(Context ctx){
        // Handles listing all games
        // Possible Handles errors
        ListGamesService listService = new ListGamesService();
        ListGamesResponse response = listService.listGames(gameList);
        ctx.result(serializer.toJson(response));
    }

    private static void isAuthorized(String authToken) throws DataAccessException{
        if(authList.getAuth(authToken) == null){
            // If authtoken doesn't exist
            throw new DataAccessException("{ message: Error: unauthorized }");
        }
    }
}