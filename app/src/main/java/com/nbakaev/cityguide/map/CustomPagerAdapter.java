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

import com.nbakaev.cityguide.R;
import com.nbakaev.cityguide.poi.Poi;
import com.nbakaev.cityguide.poi.PoiProvider;
import com.nbakaev.cityguide.settings.SettingsService;
import com.nbakaev.cityguide.util.StringUtils;

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

    private final String youtubePattern = "https?:\\/\\/(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w]*";
    private final Pattern youtubeCompiledPattern = Pattern.compile(youtubePattern, Pattern.CASE_INSENSITIVE);

    private LinearLayout header;

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
        if (!StringUtils.isEmpty(poi.getContent().getImageUrl())) {
            num++;
            links.add(poi.getContent().getImageUrl());
        }

        if (!StringUtils.isEmpty(poi.getContent().getVideoUrl())) {
            num++;
            links.add(poi.getContent().getVideoUrl());
        }

        if (poi.getContent().getImageUrls() != null && poi.getContent().getImageUrls().size() > 0) {
            num += poi.getContent().getImageUrls().size();
            links.addAll(poi.getContent().getImageUrls());
        }

        return num;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);

        final ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        // show first main POI icon
        if (position == 0 && !StringUtils.isEmpty(poi.getContent().getImageUrl())) {
            loadImageForPoi(imageView);
        }

        final String currentImageLink = links.get(position);

        // show youtube video and youtube image preview
        if (isYoutubeLink(currentImageLink)) {
            processYoutubeThumbnail(imageView, currentImageLink);
        }

        container.addView(itemView);
        return itemView;
    }

    private void loadImageForPoi(final ImageView imageView) {
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
                    imageView.setAlpha(0.9f);
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

    private void processYoutubeThumbnail(final ImageView imageView, final String currentImageLink) {
        imageView.setImageResource(R.drawable.youtube);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setOnClickListener(v -> context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(currentImageLink))));

        String youtubeVideoId = getYoutubeVideoId(currentImageLink);
        if (youtubeVideoId != null) {
            String url = "https://img.youtube.com/vi/" + youtubeVideoId + "/0.jpg";
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
            poiProvider.downloadData(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(youtubePreviewResult);
        }
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