package com.example.lexlevi.walkman_android;

/**
 * Created by lexlevi on 11/25/16.
 */
public class UserSession {
    private String token = "";

    private static UserSession ourInstance = new UserSession();

    public static UserSession getInstance() { return ourInstance; }

    private UserSession() { }

    public void setToken(String s) {
        this.token = s;
        PersistentStoreCoordinator.getInstance().persistToken(s);
    }

    public String getToken() {
        return PersistentStoreCoordinator.getInstance().fetchToken();
    }

    public boolean isTokenValid() {
        return PersistentStoreCoordinator.getInstance().tokenExists() || !this.token.isEmpty();
    }

    public void invalidateToken() {
        this.token = null;
    }
}
