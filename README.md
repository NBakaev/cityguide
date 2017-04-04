# CityGuide

 ![](https://github.com/NBakaev/binary/raw/master/cityguide/all.png)

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

#### POIs

2 implementations:

 - OfflinePoiProvider.java - saved to SQLite with [greendao ORM](http://greenrobot.org/greendao/documentation/). Images saved to app internal storage (which does not require permission)
 - ServerPoiProvider.java - REST requests with retrofit2

Implementation injects with Dagger2 in sturtup (depend get property offline of AppSettings.java object from SQLite with SettingsService.java helper)

 - results -> Observable (rxjava2)
 

#### code snippets

##### start map with some POI
```java
FragmentsWalker.startMapFragmentWithPoiOpen(getSupportFragmentManager(), poiId);
```

##### pull db from device via adb
`adb pull /data/data/com.nbakaev.cityguide/databases/poi-db.db .`

##### test notification on boot

 1. set in _Developer options_ to _Select debug app_ debug com.nbakaev.cityguide
 2. check wait to debugger
 3. `adb shell su 0 am broadcast -a android.intent.action.BOOT_COMPLETED`
 4. On android you will see prompt to debug app
 4. in idea click `attach debugger to android process`

## debug / release builds

| feature                                           | debug                                           | release | notes                                                                                                                                                                                             |
| ------------------------------------------------- | ----------------------------------------------- | --------- | ------------------------------------------------------------------------------------------------- |
| logs                 |   to standard logs output (logcat)                                |    send to [logentries](https://logentries.com)     |
| crashes                 |   to standard logs output (logcat)                                |    send to [Crashlytics](https://fabric.io)    |

Notes:
 - POIs in DB stored as com.nbakaev.cityguide.poi.db.PoiDb but all methods operate with com.nbakaev.cityguide.poi.Poi. You can transform between tho methods with static methods of PoiDb.class
 - Android icons from https://material.io/icons
 - https://developers.google.com/maps/documentation/android-api/utility/marker-clustering
 - mockups from http://mockuphone.com/nexus6p

### Offline distance
 - http://goodenoughpractices.blogspot.ru/2011/08/query-by-proximity-in-android.html
 - http://stackoverflow.com/questions/15372705/calculating-a-radius-with-longitude-and-lattitude
 - https://github.com/sozialhelden/wheelmap-android/wiki/Sqlite,-Distance-calculations
