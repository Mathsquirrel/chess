package client;

import chess.ChessGame;
import exception.ResponseException;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    RegisterRequest registerAttempt = new RegisterRequest("player1", "password", "p1@email.com");
    CreateGameRequest createGameAttempt = new CreateGameRequest("GameName");
    JoinGameRequest joinGameAttempt = new JoinGameRequest(ChessGame.TeamColor.WHITE, 1);


    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        var url = "http://localhost:"+ port;
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(url);
    }

    @BeforeEach
    public void clearServer() throws ResponseException {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void registerPos() throws Exception {
        var authData = facade.register(registerAttempt);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void registerNeg() {
        RegisterRequest badRegister = new RegisterRequest(null, null, null);
        assertThrows(ResponseException.class, () -> facade.register(badRegister));
    }

    @Test
    void loginPos() throws Exception {
        LoginRequest loginAttempt = new LoginRequest("player1", "password");
        facade.register(registerAttempt);
        var authData = facade.login(loginAttempt);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void logoutPos() throws Exception {
        LoginRequest loginAttempt = new LoginRequest("player1", "password");
        facade.register(registerAttempt);
        var authData = facade.login(loginAttempt);
        try {
            Assertions.assertDoesNotThrow(() -> facade.logout(authData.authToken()));
        }catch(Exception e){
            Assertions.fail();
        }
    }

    @Test
    void logoutNeg() throws Exception {
        facade.register(registerAttempt);
        Assertions.assertThrows(ResponseException.class, () -> facade.logout(""));
    }


    @Test
    void createGamePos() throws Exception {
        var authData = facade.register(registerAttempt);
        CreateGameRequest createGameAttempt = new CreateGameRequest("GameName");
        Assertions.assertDoesNotThrow(() -> facade.createGame(createGameAttempt, authData.authToken()));
    }

    @Test
    void createGameNeg() {
        CreateGameRequest createGameAttemptFail = new CreateGameRequest(null);

        Assertions.assertThrows(ResponseException.class, () -> facade.createGame(createGameAttemptFail, ""));
    }

    @Test
    void joinGamePos() throws Exception {
        var authData = facade.register(registerAttempt);
        facade.createGame(createGameAttempt, authData.authToken());
        Assertions.assertDoesNotThrow(() -> facade.joinGame(joinGameAttempt, authData.authToken()));
    }

    @Test
    void joinGameNeg() throws Exception {
        var authData = facade.register(registerAttempt);
        JoinGameRequest joinGameBadAttempt = new JoinGameRequest(null, 1);
        facade.createGame(createGameAttempt, authData.authToken());
        Assertions.assertThrows(ResponseException.class, () -> facade.joinGame(joinGameBadAttempt, ""));
    }

    @Test
    void listGames() throws Exception {
        var authData = facade.register(registerAttempt);
        CreateGameRequest createGameAttempt2 = new CreateGameRequest("Game2");
        CreateGameRequest createGameAttempt3 = new CreateGameRequest("Game3");
        facade.createGame(createGameAttempt, authData.authToken());
        facade.createGame(createGameAttempt2, authData.authToken());
        facade.createGame(createGameAttempt3, authData.authToken());
        ListGamesResponse gameList = facade.listGames(authData.authToken());
        assertEquals(3, gameList.games().size());
    }

    @Test
    void listGamesAlt() throws Exception {
        var authData = facade.register(registerAttempt);
        ListGamesResponse gameList = facade.listGames(authData.authToken());
        assertEquals(0, gameList.games().size());
    }

    @Test
    void testClear() throws Exception {
        var authData = facade.register(registerAttempt);
        CreateGameRequest createGameAttempt2 = new CreateGameRequest("Game2");
        CreateGameRequest createGameAttempt3 = new CreateGameRequest("Game3");
        facade.createGame(createGameAttempt, authData.authToken());
        facade.createGame(createGameAttempt2, authData.authToken());
        facade.createGame(createGameAttempt3, authData.authToken());
        facade.clear();
        authData = facade.register(registerAttempt);
        ListGamesResponse gameList = facade.listGames(authData.authToken());
        assertEquals(0, gameList.games().size());
    }
}
