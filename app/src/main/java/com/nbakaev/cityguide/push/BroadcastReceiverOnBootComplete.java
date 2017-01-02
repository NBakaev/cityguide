package com.nbakaev.cityguide.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;

import com.nbakaev.cityguide.App;
import com.nbakaev.cityguide.settings.SettingsService;

public class BroadcastReceiverOnBootComplete extends BroadcastReceiver {

    @Inject
    SettingsService settingsService;

    @Override
    public void onReceive(Context context, Intent intent) {
        App.getAppComponent().inject(this);

        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            if (settingsService.getSettings().getTrackMe()) {
                Intent serviceIntent = new Intent(context.getApplicationContext(), BackgroundNotificationService.class);
                context.startService(serviceIntent);
            }
        }
    }
}