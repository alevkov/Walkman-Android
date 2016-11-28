package com.example.lexlevi.walkman_android.Singleton;

/**
 * Created by lexlevi on 11/25/16.
 */
public class UserSession {
    private String token = "";
    private String userId = "";

    private static UserSession ourInstance = new UserSession();

    public static UserSession getInstance() { return ourInstance; }

    private UserSession() { }

    public void setToken(String t) {
        this.token = t;
        PersistentStoreCoordinator.getInstance().persistToken(t);
    }

    public void setUserId(String id) {
        this.userId = id;
        PersistentStoreCoordinator.getInstance().persistUserId(id);
    }

    public String getToken() {
        return PersistentStoreCoordinator.getInstance().fetchToken();
    }

    public String getUserId() {
        return PersistentStoreCoordinator.getInstance().fetchUserId();
    }

    public boolean isTokenValid() {
        return PersistentStoreCoordinator.getInstance().tokenExists() || !this.token.isEmpty();
    }

    public void invalidateToken() {
        this.token = null;
    }
}
