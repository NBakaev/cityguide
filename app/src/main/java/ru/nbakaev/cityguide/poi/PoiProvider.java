package ru.nbakaev.cityguide.poi;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * Created by Nikita on 10/11/2016.
 */
public interface PoiProvider {

    int DISTANCE_POI_DOWNLOAD_MOVE_CAMERA_REFRESH = 7000; // download all data for 1km
    int DISTANCE_POI_DOWNLOAD = DISTANCE_POI_DOWNLOAD_MOVE_CAMERA_REFRESH * 2;

    Observable<List<Poi>> getData(double x0, double y0, int radius);
    Observable<Poi> getById(String id);
    Observable<ResponseBody> getIcon(Poi poi);
    Observable<ResponseBody> getIcon(City city);

    Observable<ResponseBody> downloadData(String url);

}
