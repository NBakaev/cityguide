package ru.nbakaev.cityguide;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.nbakaev.cityguide.poi.Poi;
import ru.nbakaev.cityguide.poi.PoiProvider;

/**
 * Created by user on 21.12.2016.
 */

public class QrScanFragment extends BaseFragment {

    @Inject
    PoiProvider poiProvider;

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
        View view = inflater.inflate(R.layout.qr_read_activity, container, false);

        Button btnRetry = (Button) view.findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getQrCode();
            }
        });

        getQrCode();
        return view;
    }

    protected void getQrCode() {
        IntentIntegrator.forSupportFragment(this).initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            String lastScannedCode = result.getContents();
            if (lastScannedCode != null) {
                if (isOurQrCode(lastScannedCode)) {
                    String poiId = getPoiFromUrl(lastScannedCode);
                    getPoi(poiId);
                }
            }
        }
    }

    protected boolean isOurQrCode(String lScannedCode) {
        String teml = "(https://s2.nbakaev.ru/#/poi/).+";
        Pattern pattern = Pattern.compile(teml);
        Matcher matcher = pattern.matcher(lScannedCode);
        return matcher.matches();
    }

    protected String getPoiFromUrl(String Url) {
        String templ = "(https://s2.nbakaev.ru/#/poi/)";
        return Url.split(templ)[1];
    }

    protected void getPoi(String poiId) {
        poiProvider.getById(poiId).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(new Observer<Poi>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Poi value) {
                        fillPOiFields(value);

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    protected void fillPOiFields(Poi poi) {
        FragmentTransaction fragmentTransaction = baseActivity.getSupportFragmentManager().beginTransaction();
        MapsFragment mapsFragment = new MapsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("MOVE_TO_POI_ID", poi.getId());
        mapsFragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.main_fragment_content, mapsFragment);
        fragmentTransaction.commit();
    }

}


