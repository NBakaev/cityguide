package com.nbakaev.cityguide.eventbus;

import android.support.annotation.NonNull;

import io.reactivex.Observable;

/**
 * Created by Nikita Bakaev on 4/6/2017.
 */

public interface EventBus {

    void post(@NonNull EventBusEvent event);

    <T> Observable<T> observable(@NonNull Class<T> eventClass);

}
