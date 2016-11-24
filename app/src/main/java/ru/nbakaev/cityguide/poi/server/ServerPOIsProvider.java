package ru.nbakaev.cityguide.poi.server;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import ru.nbakaev.cityguide.poi.Poi;

/**
 * Retrofit2
 * Created by Nikita on 10/14/2016.
 */

public interface ServerPOIsProvider {

    @POST("poi/search")
    Observable<List<Poi>> getPoiInRadius(@Body SearchRequestDto searchRequest);

    @GET("{fullUrl}")
    Observable<ResponseBody> getIcon(@Path(value = "fullUrl", encoded = true) String fullUrl);
}
