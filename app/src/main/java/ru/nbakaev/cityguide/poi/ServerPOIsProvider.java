package ru.nbakaev.cityguide.poi;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Nikita on 10/14/2016.
 */

public interface ServerPOIsProvider {

    @POST("poi/search")
    Observable<List<Poi>> getPoiInRadius(@Body SearchRequest searchRequest);
}
