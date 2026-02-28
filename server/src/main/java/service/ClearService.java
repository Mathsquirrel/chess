package service;

import dataaccess.*;

public class ClearService {
    public void clearGames(MemoryGameAccess gameList){
        gameList.deleteGames();
    }

    public void clearUsers(MemoryUserAccess userList){
        userList.clear();
    }

    public void clearAuths(MemoryAuthTokenAccess authList){
        authList.clearAuth();
    }
}
