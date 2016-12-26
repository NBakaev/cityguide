package ru.nbakaev.cityguide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private void firstRunOrNeedPermissions() {
        Intent intent = new Intent(this, IntroActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.MaterialTheme);
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);

        if (settingsService.isFirstRun()) {
            firstRunOrNeedPermissions();
            return;
        }

        setContentView(R.layout.activity_main);


        setUpToolbar();
        setUpDrawer();
        toolbar.setTitle(getString(R.string.title_activity_main));

        setupMainFragment();
    }

    private void setupMainFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MapsFragment mapsFragment = new MapsFragment();
        fragmentTransaction.replace(R.id.main_fragment_content, mapsFragment);
        fragmentTransaction.commit();
    }

}
