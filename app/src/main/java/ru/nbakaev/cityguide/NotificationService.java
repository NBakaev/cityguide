package ru.nbakaev.cityguide;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import ru.nbakaev.cityguide.poi.Poi;

/**
 * Created by ya on 11/22/2016.
 */

public class NotificationService {

    private static final String TAG = NotificationService.class.getSimpleName();
    private static final String NOTIFICATION_KEY = "cityguide_notifications";

    private Context context;
    private NotificationManagerCompat notificationManager;
    private final static int MAX_NOTIFICATION_IN_ONE_TIME = 6;

    public NotificationService(Context context) {
        this.context = context;
        //notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager =
                NotificationManagerCompat.from(context);
    }

    /**
     * show notifications for POIs. If number of POIs > MAX_NOTIFICATION_IN_ONE_TIME - show only first
     *
     * @param newPoi       POIs for which show notifications
     * @param prevLocation current user location
     */
    public void showNotification(List<Poi> newPoi, @Nullable Location prevLocation) {

        if (newPoi.size() < 1) {
            return;
        }
        if (1==newPoi.size())
        {
            singleNotification(newPoi.get(0), prevLocation);
        }
        else {
            multipleNotification(newPoi);
        }
    }

    void multipleNotification(List<Poi> newPoi) {
        int size = newPoi.size();
        if (newPoi.size() > MAX_NOTIFICATION_IN_ONE_TIME) {
            newPoi = newPoi.subList(0, MAX_NOTIFICATION_IN_ONE_TIME);
        }
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.onboarding_logo);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_globe)
                .setLargeIcon(bitmap)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                .setContentTitle("Nearest POIs ")
                .setContentText("Received " + size + " POIs");
        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        // Sets a title for the Inbox in expanded layout
        inboxStyle.setBigContentTitle("You would be interested in:");

        // Moves events into the expanded layout
        for (Poi poi : newPoi) {
            inboxStyle.addLine(poi.getName());
        }
        if (size > MAX_NOTIFICATION_IN_ONE_TIME) {
            inboxStyle.addLine("and " + size + (size == MAX_NOTIFICATION_IN_ONE_TIME + 1 ? "POI" : "POIs") + " more");
        }
        // Moves the expanded layout object into the notification object.
        inboxStyle.setSummaryText("Nearest POIs " + size);
        mBuilder.setStyle(inboxStyle);
        // Issue the notification here.
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent launchIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        mBuilder.setContentIntent(launchIntent)
                .setAutoCancel(true);
        Notification notification = mBuilder.build();
        notificationManager.notify(0, notification);
    }

    void singleNotification(final Poi poi, final Location prevLocation) {
        if (poi.getImageUrl() != null&&!poi.getImageUrl().trim().equals("")) {
            Picasso.with(context).load(poi.getImageUrl()).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    postSingleNotification(poi, prevLocation, bitmap, true);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.onboarding_logo);
            postSingleNotification(poi, prevLocation, bitmap, false);
        }

    }

    void postSingleNotification(Poi poi, Location prevLocation, Bitmap bitmap, boolean largeImage) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_globe)
                .setLargeIcon(bitmap)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                .setContentTitle(poi.getName())
                .setContentText(poi.getDescription());
        if (largeImage) {
            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
            bigPictureStyle.setBigContentTitle(poi.getName());
            bigPictureStyle.bigPicture(bitmap);
            if (prevLocation != null) {
                Location location = new Location("M");
                location.setLatitude(poi.getLocation().getLatitude());
                location.setLongitude(poi.getLocation().getLongitude());
                float v = prevLocation.distanceTo(location);
                bigPictureStyle.setSummaryText(Float.toString(v));
            }
            mBuilder.setStyle(bigPictureStyle);
        } else {
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.setBigContentTitle(poi.getName());
            bigTextStyle.bigText(poi.getDescription());
            if (prevLocation != null) {
                Location location = new Location("M");
                location.setLatitude(poi.getLocation().getLatitude());
                location.setLongitude(poi.getLocation().getLongitude());
                float v = prevLocation.distanceTo(location);
                bigTextStyle.setSummaryText(Float.toString(v));
            }
            mBuilder.setStyle(bigTextStyle);

        }
        Intent notificationIntent = new Intent(context, MapsActivity.class);
        notificationIntent.setAction(Long.toString(System.currentTimeMillis()));
        notificationIntent.putExtra("MOVE_TO_POI_ID", poi.getId());
//        PendingIntent launchIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        PendingIntent launchIntent = PendingIntent.getActivity(context,0, notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(launchIntent)
                .setAutoCancel(true);
        Notification notification = mBuilder.build();
        notificationManager.notify(0, notification);
    }

}
