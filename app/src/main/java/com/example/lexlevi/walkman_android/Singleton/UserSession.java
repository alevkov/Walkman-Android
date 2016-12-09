package com.example.lexlevi.walkman_android.Singleton;

import android.content.Context;

/**
 * Created by lexlevi on 11/25/16.
 */
public class UserSession {
    private String token = "";
    private String userId = "";

    private static UserSession ourInstance = new UserSession();

    public static UserSession getInstance() { return ourInstance; }

    private Context ctx;

    private UserSession() { }

    public void setContext(Context ctx) {
        this.ctx = ctx;
    }

    public void setToken(String t) {
        this.token = t;
        PersistentStoreCoordinator.getInstance(ctx).persistToken(t);
    }

    public void setUserId(String id) {
        this.userId = id;
        PersistentStoreCoordinator.getInstance(ctx).persistUserId(id);
    }

    public String getToken() {
        return PersistentStoreCoordinator.getInstance(ctx).fetchToken();
    }

    public String getUserId() {
        return PersistentStoreCoordinator.getInstance(ctx).fetchUserId();
    }

    public boolean isTokenValid() {
        return PersistentStoreCoordinator.getInstance(ctx).tokenExists() || !this.token.isEmpty();
    }

    public void invalidateCredentials() {
        PersistentStoreCoordinator.getInstance(ctx).destroyCredentials();
        this.token = "";
        this.userId = "";
    }
}
