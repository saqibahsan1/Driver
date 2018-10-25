package com.akhdmny.driver.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akhdmny.driver.R;



/**
 * Created by ar-android on 15/10/2015.
 */
public class FragmentOrder extends Fragment{
    public FragmentOrder() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }
}
