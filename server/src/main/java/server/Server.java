package server;

import io.javalin.*;
import io.javalin.http.Context;

import static java.lang.IO.print;

public class Server {

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
        // Goes to proper handler based on request
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