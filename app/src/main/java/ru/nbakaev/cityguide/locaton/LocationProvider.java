package ru.nbakaev.cityguide.locaton;

import android.location.Location;

import io.reactivex.Observable;

/**
 * Created by Nikita on 10/11/2016.
 */
public interface LocationProvider {
    Observable<Location> getCurrentUserLocation();
}