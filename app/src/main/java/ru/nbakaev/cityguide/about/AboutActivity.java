package ru.nbakaev.cityguide.about;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import javax.inject.Inject;

import hu.supercluster.paperwork.Paperwork;
import ru.nbakaev.cityguide.App;
import ru.nbakaev.cityguide.BaseActivity;
import ru.nbakaev.cityguide.R;
import ru.nbakaev.cityguide.poi.db.DBService;

/**
 * Created by ya on 11/26/2016.
 */

public class AboutActivity extends BaseActivity {

    @Inject
    DBService dbService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.getAppComponent().inject(this);
        setContentView(R.layout.activity_about);

        setUpToolbar();
        setUpDrawer();

        Paperwork paperwork = new Paperwork(getApplicationContext());
        String gitSha = paperwork.get("gitSha");
        String buildTime = paperwork.get("buildTime");

        TextView buildGitView = (TextView) findViewById(R.id.aboutBuilderGit);
        TextView buildTimeView = (TextView) findViewById(R.id.aboutBuilderTime);
        TextView dbPoisElementsCount = (TextView) findViewById(R.id.dbPoisElementsCount);

        buildGitView.setText("git: " + gitSha);
        buildTimeView.setText("build time: " + buildTime);
        dbPoisElementsCount.setText("POIs downloaded: " + Long.toString(dbService.getPoisInDB()));
    }
}