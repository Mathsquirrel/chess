package server;

import io.javalin.*;
import io.javalin.http.Context;

public class Server {

    private final Javalin javalin;


    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.get("/session", Server::handleLogin);
        javalin.get("/session", Server::handleLogout);
        javalin.get("/db", Server::handleClear);
        javalin.get("/user", Server::handleRegister);
        javalin.get("/game", Server::handleJoinGame);
        javalin.get("/game", Server::handleCreateGame);
        javalin.get("/game", Server::handleListGames);

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
        ctx.result("Handling Login");
    }

    private static void handleLogout(Context ctx){
        ctx.result("Handling Logout");
    }

    private static void handleClear(Context ctx){
        ctx.result("Handling Clear");
    }

    private static void handleRegister(Context ctx){
        ctx.result("Handling Register");
    }

    private static void handleJoinGame(Context ctx){
        ctx.result("Handling JoinGame");
    }

    private static void handleCreateGame(Context ctx){
        ctx.result("Handling CreateGame");
    }

    private static void handleListGames(Context ctx){
        ctx.result("Handling UpdateGame");
    }
}