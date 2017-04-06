package com.nbakaev.cityguide.background;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import timber.log.Timber;

/**
 * Observable to get notification
 * when app is in ApplicationBackgroundStatus.FOREGROUND or ApplicationBackgroundStatus.BACKGROUND
 * Created by ya on 12/17/2016.
 */

public class AndroidBackgroundAware implements ComponentCallbacks2 {

    private static final String TAG = "AndroidBackgroundAware";
    private Context context;
    private List<ObservableEmitter<ApplicationBackgroundStatus>> observableEmitter = new CopyOnWriteArrayList<>();
    private Observable<ApplicationBackgroundStatus> statusObservable;
    private ApplicationBackgroundStatus status = ApplicationBackgroundStatus.UNKNOWN;

    // app will be in foreground once
    private boolean appEverBeenForeground = false;

    public AndroidBackgroundAware(Context context) {
        // process background / foreground app
        if (context instanceof Application) {
            context.registerComponentCallbacks(this);
            ((Application) context).registerActivityLifecycleCallbacks(new BackgroundActivityLifecycle());
        }else{
            throw new IllegalArgumentException("Context should be Application context");
        }
        this.context = context;

        statusObservable = Observable.create(e -> {
            observableEmitter.add(e);
            if (ApplicationBackgroundStatus.UNKNOWN == status && !appEverBeenForeground){
                status = ApplicationBackgroundStatus.BACKGROUND;
            }

            if (status != null) {
                e.onNext(status);
            }
        });
    }

    // called on main thread
    @Override
    public void onTrimMemory(int level) {
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
//            Log.d(TAG, "APP in BACKGROUND");
            Timber.i("APP in BACKGROUND");
            status = ApplicationBackgroundStatus.BACKGROUND;
            sendNewStatus();
        }
    }

    private void sendNewStatus() {
        for (ObservableEmitter<ApplicationBackgroundStatus> emitter : observableEmitter) {
            try {
                emitter.onNext(status);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    public Observable<ApplicationBackgroundStatus> getStatusObservable() {
        return statusObservable;
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
    }

    @Override
    public void onLowMemory() {
    }

    class BackgroundActivityLifecycle implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (status.equals(ApplicationBackgroundStatus.BACKGROUND)) {
                Timber.i("APP in FOREGROUND");
                status = ApplicationBackgroundStatus.FOREGROUND;
                sendNewStatus();
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
            appEverBeenForeground = true;

            if (status.equals(ApplicationBackgroundStatus.BACKGROUND)) {
                Timber.i("APP in FOREGROUND");
                status = ApplicationBackgroundStatus.FOREGROUND;
                sendNewStatus();
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }
}
