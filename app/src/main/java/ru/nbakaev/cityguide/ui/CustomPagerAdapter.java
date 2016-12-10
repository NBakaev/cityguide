package ru.nbakaev.cityguide.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import ru.nbakaev.cityguide.R;
import ru.nbakaev.cityguide.poi.Poi;
import ru.nbakaev.cityguide.poi.PoiProvider;
import ru.nbakaev.cityguide.utils.StringUtils;

/**
 * Created by ya on 12/10/2016.
 */

public class CustomPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater mLayoutInflater;
    private PoiProvider poiProvider;
    private Poi poi;
    private static final String TAG = "CustomPagerAdapter";
    private final BitmapFactory.Options options = new BitmapFactory.Options();

    private List<String> links = new ArrayList<>();

    public CustomPagerAdapter(Context context, PoiProvider poiProvider, Poi poi) {
        this.poiProvider = poiProvider;
        this.poi = poi;
        this.context = context;
        mLayoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        options.inSampleSize = 7;
    }

    @Override
    public int getCount() {
        int num = 0;
        if (!StringUtils.isEmpty(poi.getImageUrl())) {
            num++;
            links.add(poi.getImageUrl());
        }

        if (!StringUtils.isEmpty(poi.getVideoUrl())) {
            num++;
            links.add(poi.getVideoUrl());
        }

        if (poi.getImageUrls() != null && poi.getImageUrls().size() > 0) {
            num += poi.getImageUrls().size();
            links.addAll(poi.getImageUrls());
        }

        return num;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);

        final ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);

        if (position == 0 && !StringUtils.isEmpty(poi.getImageUrl())) {
            Observable<ResponseBody> icon = poiProvider.getIcon(poi);
            Observer<ResponseBody> iconResult = new Observer<ResponseBody>() {
                @Override
                public void onSubscribe(Disposable d) {
                    Log.d(TAG, d.toString());
                }

                @Override
                public void onNext(ResponseBody value) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(value.byteStream(), null, options);
                        imageView.setImageBitmap(bitmap);
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
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

        final String currentImageLink = links.get(position);

        if (isYoutubeLink(currentImageLink)){
            imageView.setImageResource(R.drawable.youtube);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isYoutubeLink(currentImageLink)) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(currentImageLink)));
                }
            }
        });

        container.addView(itemView);
        return itemView;
    }

    private boolean isYoutubeLink(String s){
        return (s.startsWith("https://youtube.com") || s.startsWith("http://youtube.com") ||
                s.startsWith("https://www.youtube.com") || s.startsWith("http://www.youtube.com"));
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}