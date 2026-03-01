package service;

import chess.ChessGame;
import dataaccess.*;
import org.junit.jupiter.api.*;
import model.*;

import java.util.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceTests {

    // ### TESTING SETUP/CLEANUP ###
    private static MemoryGameAccess gameList;
    private static MemoryUserAccess userList;
    private static MemoryAuthTokenAccess authList;
    private static final ClearService clearTesting = new ClearService();
    private static final CreateGameService createTesting = new CreateGameService();
    private static final JoinGameService joinTesting = new JoinGameService();
    private static final LoginService loginTesting = new LoginService();
    private static final LogoutService logoutTesting = new LogoutService();
    private static final RegisterService registerTesting = new RegisterService();
    private static MemoryGameAccess expectedGameList;
    private static MemoryUserAccess expectedUserList;
    private static MemoryAuthTokenAccess expectedAuthList;
    private static final GameData testGame = new GameData(1, null, null, "Game1", new ChessGame());
    private static final UserData testUser = new UserData("TestUser", "TestPassword", "Test@gmail.com");
    private static final AuthData testAuth = new AuthData("TestUser", "example-auth");



    @BeforeEach
    public void init() {
        // Reset server
        gameList = new MemoryGameAccess();
        userList = new MemoryUserAccess();
        authList = new MemoryAuthTokenAccess();
        expectedGameList = new MemoryGameAccess();
        expectedUserList = new MemoryUserAccess();
        expectedAuthList = new MemoryAuthTokenAccess();
    }

    // ### SERVICE-LEVEL TESTS ###

    @Test
    @Order(1)
    @DisplayName("Successful User Login")
    public void loginSuccess() {
        userList.createUser(testUser);
        try{
            loginTesting.login(new LoginRequest("TestUser", "TestPassword"), userList, authList);
            // Assert that the user was signed in and given an authtoken
            Assertions.assertNotNull(authList.getAuthtokenList());
        } catch (DataAccessException | BadRequestException e) {
            // If any exceptions are thrown, fail test
            Assertions.fail();
        }
    }

    @Test
    @Order(2)
    @DisplayName("Login Incorrect Information")
    public void loginIncorrectInfo() {
        userList.createUser(testUser);
        // Assert that using the wrong password throws DataAccessException
        Assertions.assertThrows(DataAccessException.class, () -> loginTesting.login(new LoginRequest("TestUser", "TestPasswordWrong"), userList, authList));
    }

    @Test
    @Order(3)
    @DisplayName("Successful User Registration")
    public void registerSuccess() {
        expectedUserList.createUser(new UserData("TestUser", "TestPassword", "TestEmail"));
        try{
            registerTesting.register(new RegisterRequest("TestUser", "TestPassword", "TestEmail"), userList, authList);
            // Assert that the user was created properly and that they were given an authtoken
            Assertions.assertEquals(expectedUserList.getUserList(), userList.getUserList());
            Assertions.assertNotNull(authList.getAuthtokenList());
        }catch(BadRequestException | AlreadyTakenException e){
            //If exception thrown, fail test
            Assertions.fail();
        }
    }

    @Test
    @Order(4)
    @DisplayName("Missing Register Data")
    public void missingRegisterData() {
        // Assert that missing data fields are caught by exceptions
        Assertions.assertThrows(BadRequestException.class, () -> registerTesting.register(new RegisterRequest("TestUser", null, "TestUser@gmail.com"), userList, authList));
        Assertions.assertThrows(BadRequestException.class, () -> registerTesting.register(new RegisterRequest(null, "TestPassword", "TestUser@gmail.com"), userList, authList));
        Assertions.assertThrows(BadRequestException.class, () -> registerTesting.register(new RegisterRequest("TestUser", "TestPassword", null), userList, authList));
    }

    @Test
    @Order(5)
    @DisplayName("Valid Logout")
    public void logoutSucceeds() {
        // Successful login
        userList.createUser(testUser);
        authList.createAuth(testAuth);
        logoutTesting.logout("example-auth", authList);

        // Assert that the authList is now empty from logout
        Assertions.assertEquals(expectedAuthList.getAuthtokenList(), authList.getAuthtokenList());
    }


    @Test
    @Order(6)
    @DisplayName("Logout Multiple Users")
    public void logoutTwice() {
        // Successful login
        userList.createUser(testUser);
        authList.createAuth(testAuth);
        userList.createUser(new UserData("TestUser2", "TestPassword2", "Test2@gmail.com"));
        authList.createAuth(new AuthData("TestUser2", "example-auth2"));
        logoutTesting.logout("example-auth", authList);
        logoutTesting.logout("example-auth2", authList);
        // Assert that the authList is now empty from logout
        Assertions.assertEquals(expectedAuthList.getAuthtokenList(), authList.getAuthtokenList());
    }

    @Test
    @Order(7)
    @DisplayName("Normal Creation")
    public void createGameSucceeds() {
        try {
            createTesting.createGame(new CreateGameRequest("TestGame"), gameList);
            // Assert that the game is successfully created
            Assertions.assertNotNull(gameList.getGame(1));
        } catch (BadRequestException e) {
            // If an error is thrown, fail the test
            Assertions.fail();
        }
    }

    @Test
    @Order(8)
    @DisplayName("Create with No Name")
    public void createGameBadRequest() {
        // Assert that games with no name are caught
        Assertions.assertThrows(BadRequestException.class, () -> createTesting.createGame(new CreateGameRequest(null), gameList));
    }

    @Test
    @Order(9)
    @DisplayName("Join Normal Game")
    public void joinGameSucceeds() {
        gameList.createGame(testGame);
        userList.createUser(testUser);
        authList.createAuth(testAuth);
        GameData expectedGame = new GameData(1, null, "TestUser", "Game1", new ChessGame());
        try {
            joinTesting.joinGame(new JoinGameRequest(ChessGame.TeamColor.BLACK, 1), "example-auth", gameList, authList);

            // Assert user joined as black
            Assertions.assertEquals(expectedGame, gameList.getGame(1));
        }catch(AlreadyTakenException | BadRequestException e){
            // If error is thrown, fail test
            Assertions.fail();
        }
    }

    @Test
    @Order(10)
    @DisplayName("Join Negative Test")
    public void joinGameBadID() {
        gameList.createGame(testGame);
        authList.createAuth(testAuth);
        // Try to join Game that doesn't exist. Should throw Bad Request Exception
        Assertions.assertThrows(BadRequestException.class, () -> joinTesting.joinGame(new JoinGameRequest(ChessGame.TeamColor.BLACK, 2), "example-auth", gameList, authList));
    }

    @Test
    @Order(11)
    @DisplayName("List All Games")
    public void listAllGames() {
        GameData secondGame = new GameData(2, "White", "Black", "Game2", new ChessGame());
        GameData thirdGame = new GameData(3, "White", "Black", "Game3", new ChessGame());
        GameData fourthGame = new GameData(4, "White", "Black", "Game4", new ChessGame());

        Collection<GameData> expectedList = new ArrayList<>();
        expectedList.add(testGame);
        expectedList.add(secondGame);
        expectedList.add(thirdGame);
        expectedList.add(fourthGame);

        gameList.createGame(testGame);
        gameList.createGame(secondGame);
        gameList.createGame(thirdGame);
        gameList.createGame(fourthGame);

        // Assert that the game list was updated properly
        Assertions.assertEquals(expectedList, gameList.listGames());
    }

    @Test
    @Order(12)
    @DisplayName("List Updated Games")
    public void listUpdatedGames() {
        GameData firstGame = new GameData(1, "White", "Black", "Game1", new ChessGame());
        GameData secondGame = new GameData(2, "White", "Black", "Game2", new ChessGame());
        GameData preUpdate = new GameData(3, "White", "Black", "PreUpdate", new ChessGame());
        GameData postUpdate = new GameData(3, "White", "Black", "PostUpdate", new ChessGame());
        expectedGameList.createGame(firstGame);
        expectedGameList.createGame(secondGame);
        expectedGameList.createGame(postUpdate);

        gameList.createGame(firstGame);
        gameList.createGame(secondGame);
        gameList.createGame(preUpdate);
        gameList.updateGame(postUpdate);
        // Assert that the game was updated properly
        Assertions.assertEquals(expectedGameList.listGames(), gameList.listGames());
    }

    @Test
    @Order(13)
    @DisplayName("Clear Test")
    public void clearData() {
        // Add data into database to test clearing against
        GameData clearGameData = new GameData(1, "White", "Black", "ClearGame", new ChessGame());
        UserData clearUserData = new UserData("Goose", "Goose123", "Goose@gmail.com");
        AuthData clearAuthData = new AuthData("Goose", "example-auth");
        userList.createUser(clearUserData);
        authList.createAuth(clearAuthData);
        gameList.createGame(clearGameData);
        clearTesting.clearGames(gameList);
        clearTesting.clearUsers(userList);
        clearTesting.clearAuths(authList);
        // Assert that all lists are empty
        Assertions.assertEquals(expectedGameList.listGames(), gameList.listGames());
        Assertions.assertEquals(expectedUserList.getUserList(), userList.getUserList());
        Assertions.assertEquals(expectedAuthList.getAuthtokenList(), authList.getAuthtokenList());
    }

    @Test
    @Order(14)
    @DisplayName("Empty Before Clear")
    public void clearEmptyLists() {
        // Clear empty lists
        clearTesting.clearGames(gameList);
        clearTesting.clearUsers(userList);
        clearTesting.clearAuths(authList);
        // Assert that original lists are still empty and didn't throw errors
        Assertions.assertEquals(expectedGameList.listGames(), gameList.listGames());
        Assertions.assertEquals(expectedUserList.getUserList(), userList.getUserList());
        Assertions.assertEquals(expectedAuthList.getAuthtokenList(), authList.getAuthtokenList());

    }

}
