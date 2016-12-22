package ru.nbakaev.cityguide.poi.server;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import ru.nbakaev.cityguide.poi.City;
import ru.nbakaev.cityguide.poi.Poi;

/**
 * Created by Наташа on 22.12.2016.
 */

public interface ServerCitiesProvider  {
    @GET("city")
    List<City> getCities();
}
