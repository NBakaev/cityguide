package com.nbakaev.cityguide.eventbus;

import android.support.annotation.NonNull;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Implementation of {@link EventBus} which use rxjava2
 * Created by Nikita Bakaev on 4/6/2017.
 */
public class RxEventBus implements EventBus {

    private final Subject<EventBusEvent> bus = PublishSubject.create();

    public RxEventBus() {
    }

    @Override
    public void post(@NonNull EventBusEvent event) {
        if (this.bus.hasObservers()) {
            this.bus.onNext(event);
        }
    }

    @Override
    public <T> Observable<T> observable(@NonNull Class<T> eventClass) {

        return this.bus
                .filter(o -> o != null) // Filter out null objects, better safe than sorry
                .filter(eventClass::isInstance) // We're only interested in a specific event class
                .cast(eventClass); // Cast it for easier usage
    }

}
