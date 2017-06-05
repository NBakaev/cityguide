package com.nbakaev.cityguide.scan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.nbakaev.cityguide.App;
import com.nbakaev.cityguide.BaseActivity;
import com.nbakaev.cityguide.BaseFragment;
import com.nbakaev.cityguide.MainActivity;
import com.nbakaev.cityguide.R;
import com.nbakaev.cityguide.poi.Poi;
import com.nbakaev.cityguide.poi.PoiProvider;
import com.nbakaev.cityguide.util.FragmentsWalker;
import com.nbakaev.cityguide.util.StringUtils;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by user
 */

public class QrScanFragment extends BaseFragment {

    @Inject
    PoiProvider poiProvider;

    @Inject
    QrCodeParser qrCodeParser;

    private BaseActivity baseActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        baseActivity = (BaseActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject();
        View view = inflater.inflate(R.layout.qr_read_activity, container, false);
        getQrCode();
        return view;
    }

    private void getQrCode() {
        IntentIntegrator.forSupportFragment(this).setBeepEnabled(false).initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && !StringUtils.isEmpty(result.getContents())) {
            String lastScannedCode = result.getContents();
            if (qrCodeParser.isOurQrCode(lastScannedCode)) {
                String poiId = qrCodeParser.getPoiFromUrl(lastScannedCode);
                getPoi(poiId);
            }else{
                Toast.makeText(getContext(), "Not our QR code :( Retry, or go back", Toast.LENGTH_LONG).show();
                goToMainActivity();
            }
        } else {
            goToMainActivity();
        }
    }

    private void goToMainActivity() {
        Intent intent = new Intent(baseActivity.getApplicationContext(), MainActivity.class);
        baseActivity.finish();
        startActivity(intent);
    }

    private void getPoi(String poiId) {
        poiProvider.getById(poiId).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(new Observer<Poi>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Poi value) {
                        goToPoi(value);

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void goToPoi(Poi poi) {
        FragmentsWalker.startMapFragmentFromScratchWithPoiOpen(baseActivity, this, poi.getId());
    }

    @Override
    protected void inject() {
        App.getAppComponent().inject(this);
    }
}


