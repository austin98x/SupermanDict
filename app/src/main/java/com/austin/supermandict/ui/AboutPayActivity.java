package com.austin.supermandict.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.austin.supermandict.R;

public class AboutPayActivity extends FragmentActivity {
    public final static String TAG = "AboutPayActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.about_pay);
    }

}
