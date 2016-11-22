package ru.nbakaev.cityguide;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.NotificationCompat;

import java.util.List;

import ru.nbakaev.cityguide.poi.Poi;

import static ru.nbakaev.cityguide.utils.MapUtils.printDistance;

/**
 * Created by ya on 11/22/2016.
 */

public class NotificationService {

    private static final String TAG = NotificationService.class.getSimpleName();

    private Context context;
    private NotificationManager notificationManager;
    public final static int MAX_NOTIFICATION_IN_ONE_TIME = 6;

    public NotificationService(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void showNotification(List<Poi> newPoi, Location prevLocation) {

        // if we have more than OFFLINE_CHUNK_SIZE number of notification in one time, show only part of this
        if (newPoi.size() > MAX_NOTIFICATION_IN_ONE_TIME){
            newPoi = newPoi.subList(0,MAX_NOTIFICATION_IN_ONE_TIME);
        }

        for (Poi poi : newPoi) {
            NotificationCompat.Builder b = new NotificationCompat.Builder(context);

            b.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.cast_ic_notification_small_icon)
                    .setTicker(poi.getName())
                    .setContentTitle(poi.getName())
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
//                    .setContentInfo("Info")
            ;
            Intent notificationIntent = new Intent(context, MapsActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            b.setContentIntent(contentIntent);

            Location location = new Location("M");
            location.setLatitude(poi.getLocation().getLatitude());
            location.setLongitude(poi.getLocation().getLongitude());

            if (prevLocation != null) {
                float v = prevLocation.distanceTo(location);
                b.setContentText(poi.getDescription() != null ? poi.getDescription() + "," + printDistance(v) : printDistance(v));
            }

            notificationManager.notify(poi.getId().hashCode(), b.build());
        }

    }

}
