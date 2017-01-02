package com.nbakaev.cityguide;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import java.lang.reflect.Field;

import javax.inject.Inject;

import com.nbakaev.cityguide.settings.AppSettings;
import com.nbakaev.cityguide.settings.SettingsService;
import com.nbakaev.cityguide.util.AppUtils;

/**
 * Show onboarding and request runtime permissions (also when user disable smth in settings) before run application
 * Created by ya on 12/17/2016.
 */

public class IntroActivity extends AppIntro {

    @Inject
    SettingsService settingsService;

    private final int PERMISSION_LOCATION_CODE = 2;
    private final int PERMISSION_READ_WRITE_EXTERNAL = 3;

    private final int PERMISSION_ALL = 1; // used also in AppIntroBase.class
    private static final String[] ALL_PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.MaterialTheme);
        super.onCreate(savedInstanceState);

        App.getAppComponent().inject(this);

        int bottomColor = getResources().getColor(R.color.colorPrimary);
        addSlide(AppIntroFragment.newInstance(getString(R.string.onboarding_1_title), getString(R.string.onboarding_1_description), R.drawable.onboarding_logo, bottomColor));
        addSlide(AppIntroFragment.newInstance(getString(R.string.onboarding_2_title), getString(R.string.onboarding_2_description), R.drawable.onboarding_logo, bottomColor));
        addSlide(AppIntroFragment.newInstance(getString(R.string.onboarding_3_title), getString(R.string.onboarding_3_description), R.drawable.onboarding_logo, bottomColor));
        addSlide(AppIntroFragment.newInstance(getString(R.string.onboarding_4_title), getString(R.string.onboarding_4_description), R.drawable.onboarding_logo, bottomColor));

        showSkipButton(false);
        setProgressButtonEnabled(true);
        setSwipeLock(false);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        // save that user pass first run onboarding
        AppSettings settings = settingsService.getSettings();
        settings.setFirstRun(false);
        settingsService.saveSettings(settings);

        // Check if we're running on Android 6.0 or higher
        // and restart dagger components if we need runtime permissions
        if (Build.VERSION.SDK_INT >= 23) {
            AppUtils.doRestart(getApplicationContext());
        } else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);

        // TODO: refactor
        try {
            if (newFragment == null) {
                return;
            }

            Field field = Fragment.class.getDeclaredField("mIndex");
            field.setAccessible(true);

            int fragmentIndex = field.getInt(newFragment);

            // position of fragment where ask permission
            if (fragmentIndex == 2) {
                requestPermissionAll();
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0) {
            requestPermissionAll();
            return;
        }

        if (requestCode == PERMISSION_ALL) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                pager.setCurrentItem(pager.getCurrentItem() + 1);
                return;
            } else {
                requestPermissionAll();
            }
        }

        if (requestCode == PERMISSION_LOCATION_CODE) {
            switch (grantResults[0]) {
                case PackageManager.PERMISSION_DENIED:
                    requestPermissionLocation();
                    break;
                case PackageManager.PERMISSION_GRANTED:
                    pager.setCurrentItem(pager.getCurrentItem() + 1);
                    break;
            }
            return;
        }

        if (requestCode == PERMISSION_READ_WRITE_EXTERNAL) {
            switch (grantResults[0]) {
                case PackageManager.PERMISSION_DENIED:
                    requestPermissionStorage();
                    break;
                case PackageManager.PERMISSION_GRANTED:
                    pager.setCurrentItem(pager.getCurrentItem() + 1);
                    break;
            }
        }
    }

    /**
     * request all permissions
     */
    private void requestPermissionAll() {
        ActivityCompat.requestPermissions(this, ALL_PERMISSIONS, PERMISSION_ALL);
    }

    /**
     * request only location permission
     */
    private void requestPermissionLocation() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION_CODE);
    }

    /**
     * request read/write storage permission
     */
    private void requestPermissionStorage() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_READ_WRITE_EXTERNAL);
    }
}