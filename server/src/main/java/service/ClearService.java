package service;

import dataaccess.*;
import exception.ResponseException;

public class ClearService {
    public void clearGames(GameAccess gameList) {
        gameList.deleteGames();
    }

    public void clearUsers(UserAccess userList) throws ResponseException {
        userList.clear();
    }

    public void clearAuths(AuthTokenAccess authList) throws ResponseException {
        authList.clearAuth();
    }
}
