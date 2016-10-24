package ru.nbakaev.cityguide.poi;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by Nikita on 10/11/2016.
 */
public interface PoiProvider {

    int DISTANCE_POI_DOWNLOAD_MOVE_CAMERA_REFRESH = 2000; // download all data for 1km
    int DISTANCE_POI_DOWNLOAD = DISTANCE_POI_DOWNLOAD_MOVE_CAMERA_REFRESH * 2;

    Observable<List<Poi>> getData(double x0, double y0, int radius);
    Call<ResponseBody> getIcon(String fullUrl);

}
