package com.nbakaev.cityguide;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.nbakaev.cityguide.eventbus.EventBus;
import com.nbakaev.cityguide.eventbus.events.ReInjectAllEvent;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Nikita Bakaev on 12/26/2016.
 */

public abstract class BaseFragment extends Fragment {

    protected Toolbar toolbar;

    @Inject
    protected EventBus eventBus;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
            ((BaseActivity) getActivity()).setToolbar(toolbar);
            toolbar.setTitle("City Guide");
        } catch (Exception e) {
            e.printStackTrace();
        }
        subscribeToReInjectEvent();
    }

    @Override
    public void onDestroyView() {
        ((BaseActivity) getActivity()).setToolbar(null);
        super.onDestroyView();
    }

    protected abstract void inject();

    protected void onReInjectEvent(){
        inject();
    }

    private void subscribeToReInjectEvent() {
        eventBus.observable(ReInjectAllEvent.class).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Observer<ReInjectAllEvent>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ReInjectAllEvent value) {
              onReInjectEvent();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

}
