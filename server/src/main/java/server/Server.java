package server;

import io.javalin.*;
import io.javalin.http.Context;

public class Server {

    private final Javalin javalin;


    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        /**javalin.get("/login", Server::handleLogin);
        javalin.get("/logout", Server::handleLogout);
        javalin.get("/clear", Server::handleClear);
        javalin.get("/register", Server::handleRegister);
        javalin.get("/joingame", Server::handleJoinGame);
        javalin.get("/creategame", Server::handleCreateGame);
        javalin.get("/updategame", Server::handleUpdateGame);*/

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

    private static void handleUpdateGame(Context ctx){
        ctx.result("Handling UpdateGame");
    }
}