package service;

import chess.ChessGame;
import dataaccess.MemoryAuthTokenAccess;
import dataaccess.MemoryGameAccess;
import dataaccess.MemoryUserAccess;
import org.junit.jupiter.api.*;
import model.*;
import java.util.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceTests {

    // ### TESTING SETUP/CLEANUP ###
    private static MemoryGameAccess gameList;
    private static MemoryUserAccess userList;
    private static MemoryAuthTokenAccess authList;
    private static ClearService clearTestingService = new ClearService();
    private static CreateGameService createGameTestingService = new CreateGameService();
    private static JoinGameService joinGameTestingService = new JoinGameService();
    private static ListGamesService listGamesTestingService = new ListGamesService();
    private static LoginService loginTestingService = new LoginService();
    private static LogoutService logoutTestingService = new LogoutService();
    private static RegisterService registerTestingService = new RegisterService();
    private static MemoryGameAccess expectedGameList;
    private static MemoryUserAccess expectedUserList;
    private static MemoryAuthTokenAccess expectedAuthList;



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
/*
    @Test
    @Order(1)
    @DisplayName("Normal User Login")
    public void loginSuccess() {
        TestAuthResult loginResult = serverFacade.login(existingUser);

        assertHttpOk(loginResult);
        Assertions.assertEquals(existingUser.getUsername(), loginResult.getUsername(),
                "Response did not give the same username as user");
        Assertions.assertNotNull(loginResult.getAuthToken(), "Response did not return authentication String");
    }

    @Test
    @Order(2)
    @DisplayName("Login Bad Request")
    public void loginBadRequest() {
        TestUser[] incompleteLoginRequests = {
                new TestUser(null, existingUser.getPassword()),
                new TestUser(existingUser.getUsername(), null),
        };

        for (TestUser incompleteLoginRequest : incompleteLoginRequests) {
            TestAuthResult loginResult = serverFacade.login(incompleteLoginRequest);

            assertHttpBadRequest(loginResult);
            assertAuthFieldsMissing(loginResult);
        }
    }

    @Test
    @Order(3)
    @DisplayName("Normal User Registration")
    public void registerSuccess() {
        //submit register request
        TestAuthResult registerResult = serverFacade.register(newUser);

        assertHttpOk(registerResult);
        Assertions.assertEquals(newUser.getUsername(), registerResult.getUsername(),
                "Response did not have the same username as was registered");
        Assertions.assertNotNull(registerResult.getAuthToken(), "Response did not contain an authentication string");
    }

    @Test
    @Order(4)
    @DisplayName("Re-Register User")
    public void registerTwice() {
        //submit register request trying to register existing user
        TestAuthResult registerResult = serverFacade.register(existingUser);

        assertHttpForbidden(registerResult);
        assertAuthFieldsMissing(registerResult);
    }

    @Test
    @Order(5)
    @DisplayName("Normal Logout")
    public void logoutSuccess() {
        //log out existing user
        TestResult result = serverFacade.logout(existingAuth);

        assertHttpOk(result);
    }

    @Test
    @Order(6)
    @DisplayName("Invalid Auth Logout")
    public void logoutTwice() {
        //log out user twice
        //second logout should fail
        serverFacade.logout(existingAuth);
        TestResult result = serverFacade.logout(existingAuth);

        assertHttpUnauthorized(result);
    }

    @Test
    @Order(7)
    @DisplayName("Valid Creation")
    public void createGameSuccess() {
        TestCreateResult createResult = serverFacade.createGame(createRequest, existingAuth);

        assertHttpOk(createResult);
        Assertions.assertNotNull(createResult.getGameID(), "Result did not return a game ID");
        Assertions.assertTrue(createResult.getGameID() > 0, "Result returned invalid game ID");
    }

    @Test
    @Order(8)
    @DisplayName("Create with Bad Authentication")
    public void createGameUnauthorized() {
        //log out user so auth is invalid
        serverFacade.logout(existingAuth);

        TestCreateResult createResult = serverFacade.createGame(createRequest, existingAuth);

        assertHttpUnauthorized(createResult);
        Assertions.assertNull(createResult.getGameID(), "Bad result returned a game ID");
    }

    @Test
    @Order(9)
    @DisplayName("Join Created Game")
    public void joinGameSuccess() {
        //create game
        TestCreateResult createResult = serverFacade.createGame(createRequest, existingAuth);

        //join as white
        TestJoinRequest joinRequest = new TestJoinRequest(ChessGame.TeamColor.WHITE, createResult.getGameID());

        //try join
        TestResult joinResult = serverFacade.joinPlayer(joinRequest, existingAuth);

        //check
        assertHttpOk(joinResult);

        TestListResult listResult = serverFacade.listGames(existingAuth);

        Assertions.assertNotNull(listResult.getGames(), "List result did not contain games");
        Assertions.assertEquals(1, listResult.getGames().length, "List result is incorrect size");
        Assertions.assertEquals(existingUser.getUsername(), listResult.getGames()[0].getWhiteUsername(),
                "Username of joined player not present in list result");
        Assertions.assertNull(listResult.getGames()[0].getBlackUsername(), "Username present on non-joined color");
    }

    @Test
    @Order(10)
    @DisplayName("Join Bad Authentication")
    public void joinGameUnauthorized() {
        //create game
        TestCreateResult createResult = serverFacade.createGame(createRequest, existingAuth);

        //try join as white
        TestJoinRequest joinRequest = new TestJoinRequest(ChessGame.TeamColor.WHITE, createResult.getGameID());
        TestResult joinResult = serverFacade.joinPlayer(joinRequest, existingAuth + "bad stuff");

        //check
        assertHttpUnauthorized(joinResult);
    }
*/
    @Test
    @Order(11)
    @DisplayName("List All Games")
    public void listAllGames() {
        GameData firstGame = new GameData(1, "White", "Black", "Game1", new ChessGame());
        GameData secondGame = new GameData(2, "White", "Black", "Game2", new ChessGame());
        GameData thirdGame = new GameData(3, "White", "Black", "Game3", new ChessGame());
        GameData fourthGame = new GameData(4, "White", "Black", "Game4", new ChessGame());
        Collection<GameData> expectedList = new ArrayList<>();
        expectedList.add(firstGame);
        expectedList.add(secondGame);
        expectedList.add(thirdGame);
        expectedList.add(fourthGame);

        gameList.createGame(firstGame);
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
        clearTestingService.clearGames(gameList);
        clearTestingService.clearUsers(userList);
        clearTestingService.clearAuths(authList);
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
        clearTestingService.clearGames(gameList);
        clearTestingService.clearUsers(userList);
        clearTestingService.clearAuths(authList);
        // Assert that original lists are still empty and didn't throw errors
        Assertions.assertEquals(expectedGameList.listGames(), gameList.listGames());
        Assertions.assertEquals(expectedUserList.getUserList(), userList.getUserList());
        Assertions.assertEquals(expectedAuthList.getAuthtokenList(), authList.getAuthtokenList());

    }

}
