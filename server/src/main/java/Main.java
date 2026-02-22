import server.Server;
import java.util.UUID;

public static void main(String[] args) {
    Server server = new Server();
    server.run(8080);

    System.out.println("♕ 240 Chess Server");
}

public static String generateToken() {
    return UUID.randomUUID().toString();
}