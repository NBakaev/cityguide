package com.nbakaev.cityguide.nearby;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nbakaev.cityguide.BaseActivity;
import com.nbakaev.cityguide.R;
import com.nbakaev.cityguide.location.LocationProvider;
import com.nbakaev.cityguide.poi.Poi;
import com.nbakaev.cityguide.poi.PoiProvider;
import com.nbakaev.cityguide.util.FragmentsWalker;
import com.nbakaev.cityguide.util.StringUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

import static com.nbakaev.cityguide.poi.PoiProvider.DISTANCE_POI_DOWNLOAD;
import static com.nbakaev.cityguide.util.MapUtils.printDistance;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    private List<Poi> mData = new ArrayList<>();
    private LayoutInflater inflater;
    private final LocationProvider locationProvider;
    private Location lastLocation;
    private PoiProvider poiProvider;
    private final BitmapFactory.Options options = new BitmapFactory.Options();

    private static final String TAG = "RecyclerAdapter";
    private FragmentManager fragmentManager;

    private WeakReference<BaseActivity> baseActivity;

    public RecyclerAdapter(BaseActivity baseActivityRef, LocationProvider locationProvider, PoiProvider poiProvider, FragmentManager fragmentManager) {
        inflater = LayoutInflater.from(baseActivityRef.getApplicationContext());
        baseActivity = new WeakReference<>(baseActivityRef);
        this.locationProvider = locationProvider;
        this.poiProvider = poiProvider;
        this.fragmentManager = fragmentManager;

        subscribeToLocationChanges(locationProvider);
    }

    private void subscribeToLocationChanges(LocationProvider locationProvider) {
        Observer<Location> locationObserver = new Observer<Location>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, d.toString());
            }

            @Override
            public void onNext(Location value) {
                handleNewLocation(value);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, e.toString());
            }

            @Override
            public void onComplete() {
            }
        };
        locationProvider.getCurrentUserLocation().subscribe(locationObserver);
    }

    private void handleNewLocation(@NonNull Location currentLocation) {
        final double x = currentLocation.getLatitude();
        final double y = currentLocation.getLongitude();

        poiProvider.getData(x, y, DISTANCE_POI_DOWNLOAD).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Poi>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Poi> value) {
                        mData = value;

                        // TODO: create DiffUtils and handle deletes/new object instead of this
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        lastLocation = currentLocation;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_list_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Poi current = mData.get(position);
        holder.setData(current, position);
//        holder.setListeners();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

//    public void removeItem(int position) {
//        mData.remove(position);
//        notifyItemRemoved(position);
//        notifyItemRangeChanged(position, mData.size());
//    }
//
//    public void addItem(int position, Poi poi) {
//        mData.add(position, poi);
//        notifyItemInserted(position);
//        notifyItemRangeChanged(position, mData.size());
//    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        static final int MAX_DESCRIPTION_LENGTH = 35;
        TextView title;
        TextView description;
        TextView distance;
        ImageView imgThumb;
        int position;
        Poi current;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tvTitle);
            description = (TextView) itemView.findViewById(R.id.tvDescription);
            distance = (TextView) itemView.findViewById(R.id.rvDistance);

            // default image
            imgThumb = (ImageView) itemView.findViewById(R.id.img_row);
            itemView.setOnClickListener(this);
        }

        private String stripHtmlFromHtmlString(String htmlWithTags) {
            if (htmlWithTags == null){
                return null;
            }
            return android.text.Html.fromHtml(htmlWithTags).toString().replace("\n", "");
        }

        public void setData(Poi current, int position) {
            this.title.setText(current.getName());
            this.position = position;
            this.current = current;

            String descriptionWithDeletedHtmlTags = stripHtmlFromHtmlString(current.getDescription());
            String visibleDescription = descriptionWithDeletedHtmlTags == null ? "" : descriptionWithDeletedHtmlTags.substring(0, Math.min(MAX_DESCRIPTION_LENGTH, descriptionWithDeletedHtmlTags.length())).concat("...");
            this.description.setText(visibleDescription);

            if (!StringUtils.isEmpty(current.getContent().getImageUrl())) {
                Observable<ResponseBody> icon = poiProvider.getIcon(current);
                Observer<ResponseBody> iconResult = new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, d.toString());
                    }

                    @Override
                    public void onNext(ResponseBody value) {
                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream(value.byteStream(), null, options);
                            imgThumb.setImageBitmap(bitmap);
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

            if (lastLocation != null) {
                float distanceTo = lastLocation.distanceTo(current.getLocation().toLocation());
                this.distance.setText(printDistance(distanceTo));
            } else {
                this.distance.setText("");
            }

        }

//        public void setListeners() {
//            imgDelete.setOnClickListener(MyViewHolder.this);
//            imgAdd.setOnClickListener(MyViewHolder.this);
//        }

        @Override
        public void onClick(View v) {
            RecyclerView recyclerView = (RecyclerView) v.getParent();

            int itemPosition = recyclerView.getChildLayoutPosition(v);
            Poi poi = mData.get(itemPosition);

            // hack to change active tab
            // 0 is index of MapsFragment
            if (baseActivity.get() != null) {
                baseActivity.get().getNavigationDrawerAdapter().setActiveItem(0);
            }
            FragmentsWalker.startMapFragmentWithPoiOpen(fragmentManager, poi.getId());
        }
    }
}
