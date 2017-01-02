package com.nbakaev.cityguide.map;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import com.nbakaev.cityguide.R;
import com.nbakaev.cityguide.poi.Poi;
import com.nbakaev.cityguide.poi.PoiProvider;
import com.nbakaev.cityguide.settings.SettingsService;
import com.nbakaev.cityguide.util.StringUtils;

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

    private String youtubePattern = "https?:\\/\\/(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w]*";
    private Pattern youtubeCompiledPattern = Pattern.compile(youtubePattern, Pattern.CASE_INSENSITIVE);

    public CustomPagerAdapter(Context context, PoiProvider poiProvider, Poi poi, SettingsService settingsService) {
        this.poiProvider = poiProvider;
        this.poi = poi;
        this.context = context;
        mLayoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // in offline cache if already have image with inSampleSize = 7
        if (!settingsService.isOffline()) {
            options.inSampleSize = 2;
        }
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
        // show first main POI icon
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

        // show youtube video and youtube image preview
        if (isYoutubeLink(currentImageLink)) {
            imageView.setImageResource(R.drawable.youtube);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

            String youtubeVideoId = getYoutubeVideoId(currentImageLink);
            if (youtubeVideoId != null) {
                String url = "https://img.youtube.com/vi/" + youtubeVideoId + "/0.jpg";

                Observable<ResponseBody> youtubePreview = poiProvider.downloadData(url);
                Observer<ResponseBody> youtubePreviewResult = new Observer<ResponseBody>() {
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
                youtubePreview.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(youtubePreviewResult);
            }

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

    private boolean isYoutubeLink(String s) {
        return (s.startsWith("https://youtube.com") || s.startsWith("http://youtube.com") ||
                s.startsWith("https://www.youtube.com") || s.startsWith("http://www.youtube.com"));
    }

    private String getYoutubeVideoId(String url) {
        try {
            Matcher matcher = youtubeCompiledPattern.matcher(url);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}