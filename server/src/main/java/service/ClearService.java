package service;

import dataaccess.*;

public class ClearService {
    public void clearGames(GameAccess gameList) {
        gameList.deleteGames();
    }

    public void clearUsers(UserAccess userList) {
        userList.clear();
    }

    public void clearAuths(AuthTokenAccess authList){
        authList.clearAuth();
    }
}
