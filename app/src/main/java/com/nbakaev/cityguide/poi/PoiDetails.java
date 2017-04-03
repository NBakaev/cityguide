package com.nbakaev.cityguide.poi;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nbakaev.cityguide.R;
import com.nbakaev.cityguide.map.CustomPagerAdapter;
import com.nbakaev.cityguide.settings.SettingsService;
import com.nbakaev.cityguide.util.StringUtils;
import com.nbakaev.cityguide.util.UiUtils;

import static com.nbakaev.cityguide.util.UiUtils.hideSystemStatusBar;
import static com.nbakaev.cityguide.util.UiUtils.showSystemStatusBar;

/**
 * Created by ya on 4/3/2017.
 */

public class PoiDetails {

    private final Activity activity;
    private final Toolbar toolbar;
    private final PoiProvider poiProvider;
    private final SettingsService settingsService;
    private final View googleMapsFragment;

    private BottomSheetBehavior mBottomSheetBehavior;
    private final int DEFAULT_BOTTOM_SHEET_HEIGHT = 400;

    private final View bottomSheet;

    public PoiDetails(Activity activity, View googleMapsFragment, PoiProvider poiProvider, SettingsService settingsService) {
        this.activity = activity;
        this.settingsService = settingsService;
        this.poiProvider = poiProvider;
        this.googleMapsFragment = googleMapsFragment;
        this.toolbar = (Toolbar) googleMapsFragment.findViewById(R.id.toolbar);
        this.bottomSheet = googleMapsFragment.findViewById(R.id.bottom_sheet1);
    }

    public void showPoiDialog(Poi poi) {
        if (mBottomSheetBehavior == null){
            init();
        }

        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheet.setVisibility(View.VISIBLE);

        final TextView poiName = (TextView) bottomSheet.findViewById(R.id.poi_details_name);
        final WebView webview = (WebView) bottomSheet.findViewById(R.id.poi_details_descriptionHtml);
        final RatingBar ratingBar = (RatingBar) bottomSheet.findViewById(R.id.poi_details_rating);
//        ratingBar.setRating(poi.getRating());
        ratingBar.setRating(3.5f);

        // show webview if have descriptionHtml in poi or else description as just text
        if (!StringUtils.isEmpty(poi.getDescription())) {

            webview.setVisibility(View.VISIBLE);
            webview.getSettings().setDefaultTextEncodingName("utf-8");
            webview.setFocusable(false);
            webview.clearFocus();

            String descriptionHtml = poi.getDescription();
            // delete default padding in webview
            descriptionHtml = descriptionHtml.concat("<style>body,html{padding-top:4px;margin-top:4px;} a{color: #42A5F5 }</style>");
            webview.loadData(descriptionHtml, "text/html; charset=utf-8", "UTF-8");
        }

        CustomPagerAdapter mCustomPagerAdapter = new CustomPagerAdapter(activity, poiProvider, poi, settingsService);
        ViewPager mViewPager = (ViewPager) googleMapsFragment.findViewById(R.id.pager);
        TabLayout tabLayout = (TabLayout) googleMapsFragment.findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mViewPager, true);

        // show poi name and rating only on first image
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                LinearLayout header = (LinearLayout) googleMapsFragment.findViewById(R.id.poiDetailsHeader);
                if (position == 0) {
                    header.setVisibility(View.VISIBLE);
                } else {
                    header.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (mCustomPagerAdapter.getCount() == 0) {
            mViewPager.getLayoutParams().height = 0;
            mViewPager.setVisibility(View.INVISIBLE);
        } else {
            mViewPager.setVisibility(View.VISIBLE);
            mViewPager.getLayoutParams().height = UiUtils.dpToPixels(activity.getApplicationContext(), 160);
        }
        mViewPager.setAdapter(mCustomPagerAdapter);

        poiName.setText(poi.getName());

        // if our screen is large enough, show not all bottomSheet(which can include and part of description)
        // but include only name, image and rating. So, to see description, user should scroll
        int bottomSheetMainElementsHeight = ratingBar.getHeight() + mViewPager.getHeight() + poiName.getHeight();
        mBottomSheetBehavior.setPeekHeight(bottomSheetMainElementsHeight);
    }

    public void init() {
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setHideable(true);

        int activityHeight = googleMapsFragment.findViewById(R.id.map).getHeight();
        if (activityHeight > 10) {
            mBottomSheetBehavior.setPeekHeight(activityHeight / 3 + activityHeight / 10);
        }

        bottomSheet.setVisibility(View.INVISIBLE);
        mBottomSheetBehavior.setPeekHeight(DEFAULT_BOTTOM_SHEET_HEIGHT);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset > 0.9) {
                    hideSystemStatusBar(activity);
                    toolbar.setVisibility(View.GONE);
                } else {
                    showSystemStatusBar(activity);
                    toolbar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void hide() {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

}
