package com.nbakaev.cityguide.ui.navigationdrawer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nbakaev.cityguide.BaseActivity;
import com.nbakaev.cityguide.R;
import com.nbakaev.cityguide.auth.CurrentUserService;
import com.nbakaev.cityguide.settings.SettingsService;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

import static com.nbakaev.cityguide.util.UiUtils.getCroppedBitmap;

public class NavigationDrawerFragment extends Fragment {

    private static final String TAG = "NavigationDrawerFragmen";
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private BaseActivity baseActivity;

    private CurrentUserService currentUserService;
    private SettingsService settingsService;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.baseActivity = (BaseActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
    }

    public void setUpRecyclerView(DrawerLayout mDrawerLayout, CurrentUserService currentUserService, SettingsService settingsService) {
        this.currentUserService = currentUserService;
        this.settingsService = settingsService;
        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.drawerList);
        showCurrentUser();

        NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(getActivity(), NavigationDrawerItemsProvider.getData(), baseActivity.getSupportFragmentManager(), mDrawerLayout);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        baseActivity.setNavigationDrawerAdapter(adapter);
    }

    private void showCurrentUser() {
        TextView displayName = (TextView) getView().findViewById(R.id.nav_current_displayName);
        ImageView currentUserPreview = (ImageView) getView().findViewById(R.id.imageView2);
        displayName.setText(this.currentUserService.getDisplayName());

        Observable<ResponseBody> icon = this.currentUserService.getUserImage();
        Observer<ResponseBody> iconResult = new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, d.toString());
            }

            @Override
            public void onNext(ResponseBody value) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(value.byteStream(), null, NavigationDrawerFragment.this.settingsService.getDefaultBitmapOptionsAvatar());
                    currentUserPreview.setImageBitmap(getCroppedBitmap(bitmap));
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, e.toString());
            }

            @Override
            public void onComplete() {
            }
        };

        icon.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(iconResult);


    }

    public void setUpDrawer(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
//                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
//                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                // Do something of Slide of Drawer
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerLayout.post(() -> mDrawerToggle.syncState());
    }
}
