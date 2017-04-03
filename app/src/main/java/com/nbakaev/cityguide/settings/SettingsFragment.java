package com.nbakaev.cityguide.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.nbakaev.cityguide.App;
import com.nbakaev.cityguide.BaseActivity;
import com.nbakaev.cityguide.BaseFragment;
import com.nbakaev.cityguide.R;
import com.nbakaev.cityguide.poi.db.DBService;
import com.nbakaev.cityguide.push.BackgroundNotificationService;

import javax.inject.Inject;

import hu.supercluster.paperwork.Paperwork;

/**
 * Created by ya on 11/26/2016.
 */

public class SettingsFragment extends BaseFragment {

    @Inject
    DBService dbService;

    @Inject
    SettingsService settingsService;

    BaseActivity baseActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        baseActivity = (BaseActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Paperwork paperwork = new Paperwork(baseActivity.getApplicationContext());
        String gitSha = paperwork.get("gitSha");
        String buildTime = paperwork.get("buildTime");

        TextView buildGitView = (TextView) view.findViewById(R.id.aboutBuilderGit);
        TextView buildTimeView = (TextView) view.findViewById(R.id.aboutBuilderTime);
        TextView dbPoisElementsCount = (TextView) view.findViewById(R.id.dbPoisElementsCount);

        buildGitView.setText("git: " + gitSha);
        buildTimeView.setText("build time: " + buildTime);
        dbPoisElementsCount.setText("POIs downloaded: " + Long.toString(dbService.getPoisInDB()));


        SwitchCompat offlineModeSwitch = (SwitchCompat) view.findViewById(R.id.enableExperimentalFeature);
        offlineModeSwitch.setChecked(settingsService.getSettings().isEnableExperimentalFeature());
        offlineModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppSettings settings = settingsService.getSettings();
                settings.setEnableExperimentalFeature(isChecked);
                settingsService.saveSettingsAndRestart(settings);
            }
        });


        SwitchCompat trackMeSwitch = (SwitchCompat) view.findViewById(R.id.trackMeSwitch);
        trackMeSwitch.setChecked( settingsService.getSettings().getTrackMe() );
        trackMeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppSettings settings = settingsService.getSettings();
                settings.setTrackMe(isChecked);
                settingsService.saveSettings(settings);
                Intent serviceIntent = new Intent(baseActivity.getApplicationContext(), BackgroundNotificationService.class);
                if (isChecked) {
                    baseActivity.startService(serviceIntent);
                } else {
                    baseActivity.stopService(serviceIntent);
                }
            }
        });

        return view;
    }

}
