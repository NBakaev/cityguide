package ru.nbakaev.cityguide.poi;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Nikita on 10/14/2016.
 */

public interface ServerPOIsProvider {

    @POST("poi/search")
    Observable<List<Poi>> getPoiInRadius(@Body SearchRequest searchRequest);

    @GET("{fullUrl}")
    Call<ResponseBody> getIcon(@Path(value = "fullUrl", encoded = true) String fullUrl);
}
