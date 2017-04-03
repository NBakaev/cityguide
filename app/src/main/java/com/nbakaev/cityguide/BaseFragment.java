package com.nbakaev.cityguide;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

/**
 * Created by ya on 12/26/2016.
 */

public abstract class BaseFragment extends Fragment {

    protected Toolbar toolbar;

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
    }

    @Override
    public void onDestroyView() {
        ((BaseActivity) getActivity()).setToolbar(null);
        super.onDestroyView();
    }
}
