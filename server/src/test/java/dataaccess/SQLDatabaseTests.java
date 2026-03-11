package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import org.junit.jupiter.api.*;
import model.*;
import passoff.model.*;
import passoff.server.TestServerFacade;
import server.Server;
import service.*;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
    private static final AuthData BAD_AUTH = new AuthData(null, "example-auth");


    private static TestServerFacade serverFacade;
    private static Server server;
    private static Class<?> databaseManagerClass;

    @BeforeAll
    public static void startServer() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

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
        server.stop();
    }

    // ### DAO-TESTS ###

    @Test
    @DisplayName("Create User Positive Test")
    @Order(1)
    public void createUserPos() {
        // Test that User was created
        int initialRowCount = getDatabaseRows();
        try {
            userList.createUser(TEST_USER);
            Assertions.assertTrue(initialRowCount < getDatabaseRows(), "No new data added to database");
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
        int initialRowCount = getDatabaseRows();
        try {
            gameList.createGame(TEST_GAME);
            Assertions.assertTrue(initialRowCount < getDatabaseRows(), "No new data added to database");
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
            gameList.updateGame(BAD_GAME);
            Assertions.assertEquals(1, getDatabaseRows());
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
            Assertions.assertEquals(1, getDatabaseRows());
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
            Assertions.assertEquals(0, getDatabaseRows());
        }catch(Exception e){
            System.out.println("Failed Test");
        }
    }

    @Test
    @DisplayName("Get Auth Negative Test")
    @Order(20)
    public void deleteAuthNeg() {
        // Test that Delete doesn't change database on wrong Auth
        try{
            authList.createAuth(TEST_AUTH);
            authList.deleteAuth(BAD_AUTH);
            Assertions.assertEquals(1, getDatabaseRows());
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
            Assertions.assertEquals(0, getDatabaseRows());
        }catch(ResponseException e){
            System.out.println("Failed Test");
        }
    }

    private int getDatabaseRows() {
        AtomicInteger rows = new AtomicInteger();
        executeForAllTables((tableName, connection) -> {
            try (var statement = connection.createStatement()) {
                var sql = "SELECT count(*) FROM " + tableName;
                try (var resultSet = statement.executeQuery(sql)) {
                    if (resultSet.next()) {
                        rows.addAndGet(resultSet.getInt(1));
                    }
                }
            }
        });

        return rows.get();
    }

    private void executeForAllTables(SQLDatabaseTests.TableActionTests tableAction) {
        String sql = """
                    SELECT table_name
                    FROM information_schema.tables
                    WHERE table_schema = DATABASE();
                """;

        try (Connection conn = getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    tableAction.execute(resultSet.getString(1), conn);
                }
            }
        } catch (ReflectiveOperationException | SQLException e) {
            Assertions.fail(e.getMessage(), e);
        }
    }

    private Connection getConnection() throws ReflectiveOperationException {
        Class<?> clazz = findDatabaseManager();
        Method getConnectionMethod = clazz.getDeclaredMethod("getConnection");
        getConnectionMethod.setAccessible(true);

        Object obj = clazz.getDeclaredConstructor().newInstance();
        return (Connection) getConnectionMethod.invoke(obj);
    }

    private Class<?> findDatabaseManager() throws ClassNotFoundException {
        if(databaseManagerClass != null) {
            return databaseManagerClass;
        }

        for (Package p : getClass().getClassLoader().getDefinedPackages()) {
            try {
                Class<?> clazz = Class.forName(p.getName() + ".DatabaseManager");
                clazz.getDeclaredMethod("getConnection");
                databaseManagerClass = clazz;
                return clazz;
            } catch (ReflectiveOperationException ignored) {}
        }
        throw new ClassNotFoundException("Unable to load database in order to verify persistence. " +
                "Are you using DatabaseManager to set your credentials? " +
                "Did you edit the signature of the getConnection method?");
    }

    private interface TableActionTests {
        void execute(String tableName, Connection connection) throws SQLException;
    }
}
