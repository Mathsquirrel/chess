package dataaccess;
import passoff.server.*;
import chess.ChessGame;
import exception.ResponseException;
import org.junit.jupiter.api.*;
import model.*;
import passoff.model.*;
import passoff.server.*;
import server.Server;
import service.*;

import java.sql.*;
import java.util.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SQLDatabaseTests {

    // ### TESTING SETUP/CLEANUP ###
    private static SQLGameAccess gameList;
    private static SQLUserAccess userList;
    private static SQLAuthTokenAccess authList;
    private static final GameData TEST_GAME = new GameData(1, null, null, "Game1", new ChessGame());
    private static final GameData UPDATED_GAME = new GameData(1, null, "BLACK PLAYER", "Game1", new ChessGame());

    private static final GameData BAD_GAME = new GameData(1000, null, null, null, new ChessGame());
    private static final UserData TEST_USER = new UserData("TestUser", "TestPassword", "Test@gmail.com");
    private static final UserData BAD_USER = new UserData("BadUser", "badUserPassword", null);
    private static final AuthData TEST_AUTH = new AuthData("TestUser", "example-auth");
    private static final AuthData BAD_AUTH = new AuthData(null, null);


    private static TestServerFacade serverFacade;
    private static Server sqlServer;

    @BeforeAll
    public static void startSQLServer() {
        sqlServer = new Server();
        var port = sqlServer.run(0);

        serverFacade = new TestServerFacade("localhost", Integer.toString(port));
    }

    @BeforeEach
    public void setUp() {
        serverFacade.clear();
    }

    @BeforeEach
    public void init() {
        // Setup Classes for DAOs
        gameList = new SQLGameAccess();
        userList = new SQLUserAccess();
        authList = new SQLAuthTokenAccess();
    }

    @AfterAll
    static void stopServer() {
        sqlServer.stop();
    }

    // ### DAO-TESTS ###

    @Test
    @DisplayName("Create User Positive Test")
    @Order(1)
    public void createUserPos() {
        // Test that User was created
        try {
            userList.createUser(TEST_USER);
            UserData testing = userList.getUser("TestUser");
            Assertions.assertNotNull(testing);
        }catch(Exception e){
            System.out.println("Failed Test");
        }
    }

    @Test
    @DisplayName("Create User Negative Test")
    @Order(2)
    public void createUserNeg() {
        // Test that User with Missing Credentials not Registered
        Assertions.assertThrows(ResponseException.class, () -> userList.createUser(BAD_USER));
    }

    @Test
    @DisplayName("Get User Positive Test")
    @Order(3)
    public void getUserPos() {
        // Test that User can be retrieved
        try {
            userList.createUser(TEST_USER);
            UserData retrievedUser = userList.getUser("TestUser");
            Assertions.assertNotNull(retrievedUser);
        }catch(Exception e){
            System.out.println("Failed Test");
        }
    }

    @Test
    @DisplayName("Get User Negative Test")
    @Order(4)
    public void getUserNeg() {
        // Test that User Not registered returns null
        try{
            UserData retrievedUser = userList.getUser("FakeUser");
            Assertions.assertNull(retrievedUser);
        }catch(ResponseException e){
            System.out.println("Failed Test");
        }
    }

    @Test
    @DisplayName("Get UserList Positive Test")
    @Order(5)
    public void getUserListPos() {
        // Test that UserList can be retrieved
        try {
            userList.createUser(TEST_USER);
            Assertions.assertNotNull(userList.getUserList());
        }catch(Exception e){
            System.out.println("Failed Test");
        }
    }

    @Test
    @DisplayName("Get UserList No Users Test")
    @Order(6)
    public void getUserListEmpty() {
        // Test that UserList returns nothing if no users
        try{
            Collection<UserData> checkingList = new ArrayList<>();
            Assertions.assertEquals(checkingList, userList.getUserList());
        }catch(ResponseException e){
            System.out.println("Failed Test");
        }
    }

    @Test
    @DisplayName("Verify Passwords Positive Test")
    @Order(7)
    public void verifyPasswordsPos() {
        // Test that a User's password matches the hash
        try {
            userList.createUser(TEST_USER);
            Assertions.assertTrue(userList.verifyPasswords("TestUser", "TestPassword"));
        }catch(Exception e){
            System.out.println("Failed Test");
        }
    }

    @Test
    @DisplayName("Verify Passwords Negative Test")
    @Order(8)
    public void verifyPasswordsNeg() {
        // Test that incorrect password does not verify
        try{
            userList.createUser(TEST_USER);
            Assertions.assertFalse(userList.verifyPasswords("TestUser", "WrongPassword"));
        }catch(ResponseException e){
            System.out.println("Failed Test");
        }
    }

    @Test
    @DisplayName("Create Game Positive Test")
    @Order(9)
    public void createGamePos() {
        // Test that Game was created
        try {
            gameList.createGame(TEST_GAME);
            GameData testing = gameList.getGame(1);
            Assertions.assertNotNull(testing);
        }catch(Exception e){
            System.out.println("Failed Test");
        }
    }

    @Test
    @DisplayName("Create Game Negative Test")
    @Order(10)
    public void createGameNeg() {
        // Test that Game with Missing Info not Created
        Assertions.assertThrows(ResponseException.class, () -> gameList.createGame(BAD_GAME));
    }

    @Test
    @DisplayName("ListGames Positive Test")
    @Order(11)
    public void listGamesPos() {
        // Test that GameList can be retrieved
        try {
            gameList.createGame(TEST_GAME);
            Assertions.assertNotNull(gameList.listGames());
        }catch(Exception e){
            System.out.println("Failed Test");
        }
    }

    @Test
    @DisplayName("ListGames No Games Test")
    @Order(12)
    public void listGamesEmpty() {
        // Test that listGames returns nothing if no games
        try{
            Collection<GameData> checkingList = new ArrayList<>();
            Assertions.assertEquals(checkingList, gameList.listGames());
        }catch(ResponseException e){
            System.out.println("Failed Test");
        }
    }

    @Test
    @DisplayName("Get Game Positive Test")
    @Order(13)
    public void getGamePos() {
        // Test that Game can be retrieved
        try {
            gameList.createGame(TEST_GAME);
            GameData retrievedGame = gameList.getGame(1);
            Assertions.assertNotNull(retrievedGame);
        }catch(Exception e){
            System.out.println("Failed Test");
        }
    }

    @Test
    @DisplayName("Get Game Negative Test")
    @Order(14)
    public void getGameNeg() {
        // Test that Game Not Created returns null
        try{
            GameData retrievedGame = gameList.getGame(10000);
            Assertions.assertNull(retrievedGame);
        }catch(ResponseException e){
            System.out.println("Failed Test");
        }
    }

    @Test
    @DisplayName("Update Game Positive Test")
    @Order(15)
    public void updateGamePos() {
        // Test that UpdateGame properly updates by checking name of player
        try {
            gameList.createGame(TEST_GAME);
            gameList.updateGame(UPDATED_GAME);
            GameData retrievedGame = gameList.getGame(1);
            Assertions.assertEquals(UPDATED_GAME.blackUsername(), retrievedGame.blackUsername());
        }catch(Exception e){
            System.out.println("Failed Test");
        }
    }

    @Test
    @DisplayName("Update Game Negative Test")
    @Order(16)
    public void updateGameNeg() {
        // Test that UpdateGame with bad info does nothing
        try{
            gameList.createGame(TEST_GAME);
            Collection<GameData> testing = gameList.listGames();
            gameList.updateGame(BAD_GAME);
            Assertions.assertEquals(testing, gameList.listGames());
        }catch(ResponseException e){
            System.out.println("Failed Test");
        }
    }

    @Test
    @DisplayName("Create Auth Positive Test")
    @Order(17)
    public void createAuthPos() {
        // Test that Create Auth adds auth to database
        try {
            authList.createAuth(TEST_AUTH);
            AuthData testing = authList.getAuth("example-auth");
            Assertions.assertNotNull(testing);
        }catch(Exception e){
            System.out.println("Failed Test");
        }
    }

    @Test
    @DisplayName("Create Auth Negative Test")
    @Order(18)
    public void createAuthNeg() {
        // Test that createAuth with bad info throws error
        Assertions.assertThrows(ResponseException.class, () -> authList.createAuth(BAD_AUTH));

    }

    @Test
    @DisplayName("Get Auth Positive Test")
    @Order(19)
    public void getAuthPos() {
        // Test that Auth can be retrieved
        try {
            authList.createAuth(TEST_AUTH);
            AuthData retrievedAuth = authList.getAuth("example-auth");
            Assertions.assertNotNull(retrievedAuth);
        }catch(Exception e){
            System.out.println("Failed Test");
        }
    }

    @Test
    @DisplayName("Get Auth Negative Test")
    @Order(20)
    public void getAuthNeg() {
        // Test that Auth not created returns null
        try{
            AuthData retrievedAuth = authList.getAuth("Fake-token");
            Assertions.assertNull(retrievedAuth);
        }catch(ResponseException e){
            System.out.println("Failed Test");
        }
    }

    @Test
    @DisplayName("Delete Auth Positive Test")
    @Order(21)
    public void deleteAuthPos() {
        // Test that Auth is properly Deleted
        try {
            authList.createAuth(TEST_AUTH);
            authList.deleteAuth(TEST_AUTH);
            AuthData testing = authList.getAuth("example-auth");
            Assertions.assertNull(testing);
        }catch(Exception e){
            System.out.println("Failed Test");
        }
    }

    @Test
    @DisplayName("Delete Auth Negative Test")
    @Order(22)
    public void deleteAuthNeg() {
        // Test that Delete doesn't change database on wrong Auth
        try{
            authList.createAuth(TEST_AUTH);
            Assertions.assertDoesNotThrow(() -> authList.deleteAuth(BAD_AUTH));
        }catch(ResponseException e){
            System.out.println("Failed Test");
        }
    }

    @Test
    @DisplayName("Clear All Databases")
    @Order(23)
    public void clearDataBases() {
        // Test that all Databases are Cleared
        try{
            userList.createUser(TEST_USER);
            authList.createAuth(TEST_AUTH);
            gameList.createGame(TEST_GAME);
            userList.clear();
            authList.clearAuth();
            gameList.deleteGames();
            AuthData testAuth = authList.getAuth("example-auth");
            GameData testGame = gameList.getGame(1);
            UserData testUser = userList.getUser("TestUser");
            Assertions.assertNull(testAuth);
            Assertions.assertNull(testGame);
            Assertions.assertNull(testUser);
        }catch(ResponseException e){
            System.out.println("Failed Test");
        }
    }
}
