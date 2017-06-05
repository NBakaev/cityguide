package com.nbakaev.cityguide.auth.server;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Retrofit2
 * Created by Nikita on 10/14/2016.
 */

public interface ServerAccountRestProvider {

    @GET
    Observable<ResponseBody> downloadContent(@Url String fullUrl);

}

