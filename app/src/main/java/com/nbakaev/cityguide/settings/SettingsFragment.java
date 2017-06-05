package com.nbakaev.cityguide.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by Nikita Bakaev on 11/26/2016.
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toolbar.setTitle(R.string.settings);
    }

    @Override
    protected void inject() {
        App.getAppComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject();
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Paperwork paperwork = new Paperwork(baseActivity.getApplicationContext());
        String gitSha = paperwork.get("gitSha");
        String buildTime = paperwork.get("buildTime");

        TextView buildGitView = (TextView) view.findViewById(R.id.aboutBuilderGit);
        TextView buildTimeView = (TextView) view.findViewById(R.id.aboutBuilderTime);
        TextView dbPoisElementsCount = (TextView) view.findViewById(R.id.dbPoisElementsCount);

        buildGitView.setText(getString(R.string.git) + gitSha);
        buildTimeView.setText(getString(R.string.settings_fragment_time) + buildTime);
        dbPoisElementsCount.setText(getString(R.string.settings_fragment_poi_offline) + Long.toString(dbService.getPoisInDB()));


        SwitchCompat offlineModeSwitch = (SwitchCompat) view.findViewById(R.id.enableExperimentalFeature);
        offlineModeSwitch.setChecked(settingsService.getSettings().isEnableExperimentalFeature());
        offlineModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppSettings settings = settingsService.getSettings();
            settings.setEnableExperimentalFeature(isChecked);
            settingsService.saveSettings(settings);
            settingsService.saveSettingsAndRestart(settings);
        });


        SwitchCompat trackMeSwitch = (SwitchCompat) view.findViewById(R.id.trackMeSwitch);
        trackMeSwitch.setChecked( settingsService.getSettings().getTrackMe() );
        trackMeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppSettings settings = settingsService.getSettings();
            settings.setTrackMe(isChecked);
            settingsService.saveSettings(settings);
            Intent serviceIntent = new Intent(baseActivity.getApplicationContext(), BackgroundNotificationService.class);
            if (isChecked) {
                baseActivity.startService(serviceIntent);
            } else {
                baseActivity.stopService(serviceIntent);
            }
        });

        return view;
    }

}
