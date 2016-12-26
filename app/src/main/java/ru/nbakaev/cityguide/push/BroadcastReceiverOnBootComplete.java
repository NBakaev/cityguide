package ru.nbakaev.cityguide.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ru.nbakaev.cityguide.util.SharedPreferencesUtils;

public class BroadcastReceiverOnBootComplete extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            SharedPreferencesUtils spUtils = new SharedPreferencesUtils(context);
            if (spUtils.getTrackMe()) {
                Intent serviceIntent = new Intent(context.getApplicationContext(), BackgrounNotificationService.class);
                context.startService(serviceIntent);
            }
        }
    }
}