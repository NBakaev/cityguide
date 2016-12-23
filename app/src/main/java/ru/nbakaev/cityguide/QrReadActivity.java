package ru.nbakaev.cityguide;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import ru.nbakaev.cityguide.poi.Poi;
import ru.nbakaev.cityguide.poi.PoiProvider;
import ru.nbakaev.cityguide.poi.db.DBService;
import ru.nbakaev.cityguide.utils.CacheUtils;
import ru.nbakaev.cityguide.utils.StringUtils;

import static ru.nbakaev.cityguide.poi.PoiProvider.DISTANCE_POI_DOWNLOAD;

/**
 * Created by user on 21.12.2016.
 */


public class QrReadActivity extends BaseActivity {
    private String lastScannedCode;
    private String poiId;
    private TextView poiDescription;
    private ImageView poiImage;
    private LinearLayout btnMap;
    private Button btnRetry;
    private ScrollView scrollView;
    private LinearLayout empty;
    final BitmapFactory.Options options = new BitmapFactory.Options();
    @Inject
    PoiProvider poiProvider;

    @Inject
    CacheUtils cacheUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.getAppComponent().inject(this);

        setContentView(R.layout.qr_read_activity);

        setUpToolbar();
        setUpDrawer();
        toolbar.setTitle(R.string.drawer_qr);
        poiDescription = (TextView) findViewById(R.id.PoiDescription);
        poiImage = (ImageView) findViewById(R.id.PoiImage);
        ;
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        empty = (LinearLayout) findViewById(R.id.empty);
        btnMap = (LinearLayout) findViewById(R.id.btnMap);
        btnRetry = (Button) findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getQrCode();
            }
        });
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (poiId != null) {
                    Intent i = new Intent(QrReadActivity.this, MapsActivity.class);
                    i.putExtra("MOVE_TO_POI_ID", poiId);
                    QrReadActivity.this.startActivity(i);
                }
            }
        });
        Intent intent = getIntent();
        String sData = intent.getDataString();
        if (sData != null) {
            if (IsOurQrCode(sData)) {
                poiId = getPoiFromUrl(sData);
                getPoi(poiId);
            }
        } else {
            getQrCode();
        }
    }

    protected void getQrCode() {
        new IntentIntegrator(this).initiateScan();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            lastScannedCode = result.getContents();
            if (lastScannedCode != null) {
                boolean isPoi = IsOurQrCode(lastScannedCode);
                if (isPoi == true) {
                    poiId = getPoiFromUrl(lastScannedCode);
                    getPoi(poiId);
                }
            }
        }
    }

    protected boolean IsOurQrCode(String lScannedCode) {
        String teml = "(https://s2.nbakaev.ru/#/poi/).+";
        Pattern pattern = Pattern.compile(teml);
        Matcher matcher = pattern.matcher(lScannedCode);
        boolean res = matcher.matches();
        return res;
    }

    protected String getPoiFromUrl(String Url) {
        String templ = "(https://s2.nbakaev.ru/#/poi/)";
        String res = Url.split(templ)[1];
        return res;
    }

    protected void setImg(final Poi poi) {
        Observable<ResponseBody> icon = poiProvider.getIcon(poi);
        Observer<ResponseBody> iconResult = new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseBody value) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(value.byteStream(), null, options);
                    cacheUtils.cachePoiImage(bitmap, poi);
                    poiImage.setImageBitmap(bitmap);
                    poiImage.setVisibility(View.VISIBLE);
                } catch (Exception e) {

                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
            }
        };

        icon.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(iconResult);
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
        poiDescription.setText(poi.getDescription());
        poiDescription.setVisibility(View.VISIBLE);
        toolbar.setTitle(poi.getName());
        //poiName.setVisibility(View.VISIBLE);
        empty.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);

        if (!StringUtils.isEmpty(poi.getImageUrl())) {
            setImg(poi);
        }
    }

}


