package server;

public class ServerMain {
    static void main() {
        Server server = new Server();
        server.run(8080);
        System.out.println("♕ 240 Chess Server Started");
    }
}
