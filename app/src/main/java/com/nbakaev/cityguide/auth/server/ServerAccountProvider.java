package com.nbakaev.cityguide.auth.server;

import android.content.Context;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

/**
 * Created by Nikita on 10/14/2016.
 */

public class ServerAccountProvider{

    private final Context context;
    private ServerAccountRestProvider serverAccountRestProvider;

    public ServerAccountProvider(Context context, Retrofit retrofit) {
        this.context = context;
        serverAccountRestProvider = retrofit.create(ServerAccountRestProvider.class);
    }

    public Observable<ResponseBody> loadData(String url) {
        return serverAccountRestProvider.downloadContent(url);
    }

}
