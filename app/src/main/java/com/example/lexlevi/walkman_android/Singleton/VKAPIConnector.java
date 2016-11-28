package com.example.lexlevi.walkman_android.Singleton;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by lexlevi on 11/26/16.
 */
public class VKAPIConnector {
    // https://api.vk.com/method/audio.search?q=bitchplease&auto_complete=1&lyrics=0&performer_only=0&sort=2&search_own=0&offset=0&count=100&access_token=TOKEN
    private final String apiBaseURL = "https://api.vk.com/method/";
    // methods
    private final String getSongsForUserMethod = "audio.get";
    private final String searchSongsMethod = "audio.search";
    // params
    private final String ownerIdParam = "owner_id=";
    private final String countParam = "count=";
    private final String offsetParam = "offset=";
    private final String needUserParam = "need_user=";
    private final String queryParam = "q=";
    private final String autoCompleteParam = "auto_complete=";
    private final String lyricsParam = "lyrics=";
    private final String performerOnlyParam = "performer_only=";
    private final String sortParam = "sort=";
    private final String searchOwnParam = "search_own=";
    private final String accessTokenParam = "access_token=";
    // singleton properties
    private final OkHttpClient client = new OkHttpClient();
    // singleton instance
    private static VKAPIConnector ourInstance = new VKAPIConnector();
    // singleton getter
    public static VKAPIConnector getInstance() {
        return ourInstance;
    }

    private VKAPIConnector() { }

    /**
     * GET Requests
     *
     * Typical structure for calling GET:
     * VKAPIConnector.getInstance().GET([param], new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { }
            @Override
            public void onResponse(Call call, Response response) throws IOException { }
        })
     */

    public Call GET_SongsByUserID(String id, Callback callback) {
        String s = apiBaseURL + getSongsForUserMethod +
                "?" + ownerIdParam + id + "&" + needUserParam +
                "0&" + countParam + "0&" + offsetParam + "0&"
                + accessTokenParam + UserSession.getInstance().getToken();
        Request request = new Request.Builder()
                .url(s)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public Call GET_songsByQuery(String q, Callback callback) {
        String s = apiBaseURL + searchSongsMethod +
                "?" + queryParam + q + "&" + autoCompleteParam +
                "1&" + lyricsParam + "0&" + performerOnlyParam +
                "0&" + sortParam + "2&" + searchOwnParam + "0&" +
                offsetParam + "0&" + countParam + "100&" + accessTokenParam +
                UserSession.getInstance().getToken();
        Request request = new Request.Builder()
                .url(s)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }
}
