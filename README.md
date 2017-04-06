# CityGuide

 ![](https://github.com/NBakaev/binary/raw/master/cityguide/all2.png)

Related resources

 - [Trello](https://trello.com/b/CvkJB18L/hse-cityguide)
 - [Admin web UI](https://cityguide.nbakaev.com)
 - [server repo](https://github.com/NBakaev/cityguide-server)

## Android requirements
 - Android 4.4+. Testing on Android 4.4, 7.0

### Standard telephone with
 - Google Play
 - WebView (such as chrome)
 
## Architecture overview

### POIs

PoiProvider interface with 2 implementations:

 - OfflinePoiProvider.java - saved to SQLite via [greendao ORM](http://greenrobot.org/greendao/documentation/). Images saved to app cache storage (which does not require permission)
 - ServerPoiProvider.java - REST requests with retrofit2

Internally, then(at app startup) created WrappedPoiProvider object which delegate calls to necessary implementation (which is determined by SettingsService#isOffline)

And every object just inject via Dagger2

WrappedPoiProvider is used to allow change implementation at runtime without change dagger graph

### Event bus

Main implementation is `RxEventBus` which use rxjava.
You can inject `EventBus` as dagger component(injected to `BaseFragment` and `BaseActivity`, by default)

You can send event :
```java
eventBus.post(new ReloadLocationProvider());
```

And subscribe e.g:
```java
 private void subscribeToReInjectPoiProvider() {
        eventBus.observable(ReInjectPoiProvider.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<ReInjectPoiProvider>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(ReInjectPoiProvider value) {
                // doSomethingOnNewIvent();
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }
```

### code snippets

FragmentsWalker can be used to walk(replace) fragment to each other

#### start map with some POI open
```java
FragmentsWalker.startMapFragmentWithPoiOpen(getSupportFragmentManager(), poiId);
```

#### pull db from device via adb
`adb pull /data/data/com.nbakaev.cityguide/databases/poi-db.db .`

#### test notification on boot

 1. set in _Developer options_ to _Select debug app_ debug com.nbakaev.cityguide
 2. check wait to debugger
 3. `adb shell su 0 am broadcast -a android.intent.action.BOOT_COMPLETED`
 4. On android you will see prompt to debug app
 4. in idea click `attach debugger to android process`

## debug / release builds
Timber is used to log

| feature                                           | debug                                           | release | notes                                                                                                                                                                                             |
| ------------------------------------------------- | ----------------------------------------------- | --------- | ------------------------------------------------------------------------------------------------- |
| logs                 |   to standard logs output (logcat)                                |    send to [logentries](https://logentries.com)     |
| crashes                 |   to standard logs output (logcat)                                |    send to [Crashlytics](https://fabric.io)    |

Notes:
 - POIs in DB stored as com.nbakaev.cityguide.poi.db.PoiDb but all methods operate with com.nbakaev.cityguide.poi.Poi. You can transform between tho methods with static methods of PoiDb.class
 - Android icons from https://material.io/icons
 - https://developers.google.com/maps/documentation/android-api/utility/marker-clustering
 - mockups from http://mockuphone.com/nexus6p

### Offline distance algorithms
 - http://goodenoughpractices.blogspot.ru/2011/08/query-by-proximity-in-android.html - currently used
 - http://stackoverflow.com/questions/15372705/calculating-a-radius-with-longitude-and-lattitude
 - https://github.com/sozialhelden/wheelmap-android/wiki/Sqlite,-Distance-calculations
