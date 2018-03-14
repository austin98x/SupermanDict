package com.austin.supermandict.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.austin.supermandict.BuildConfig;
import com.austin.supermandict.R;
import com.xiaomi.mistatistic.sdk.MiStatInterface;

public class AboutFragment extends Fragment {
    public final static String TAG = "AboutActivity";

    private TextView mMark;
    private TextView mContact;
    private TextView mPay;
    private TextView mShare;
    private TextView mVersion;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(final View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        mMark = v.findViewById(R.id.mark);
        mPay = v.findViewById(R.id.pay);
        mContact = v.findViewById(R.id.suggestion);
        mShare = v.findViewById(R.id.share);
        mVersion = v.findViewById(R.id.version);
        mVersion.setText("版本: " + BuildConfig.VERSION_NAME);
        mMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAppMarket();
            }
        });
        mPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPay();
            }
        });
        mContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContact();
            }
        });
        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openShare();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        MiStatInterface.recordPageStart(getActivity(), "About Page");
    }

    @Override
    public void onPause() {
        super.onPause();
        MiStatInterface.recordPageEnd();
    }

    private void openAppMarket() {
        try {
            Uri uri = Uri.parse("market://details?id=com.austin.supermandict");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ActivityNotFoundException anf) {
            Toast.makeText(getContext(), R.string.no_share_app, Toast.LENGTH_SHORT).show();
        }
    }

    private void openPay() {
        Intent intent = new Intent(getActivity(), AboutPayActivity.class);
        startActivity(intent);
    }

    private void openContact() {
        try{
            Uri uri = Uri.parse(getString(R.string.mail_send_to));
            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_topic));
            intent.putExtra(Intent.EXTRA_TEXT,
                    getString(R.string.device_model) + Build.MODEL + " \n"
                            + getString(R.string.sdk_version) + Build.VERSION.RELEASE + " \n"
                            + "Version: " + BuildConfig.VERSION_NAME);
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex){
            Toast.makeText(getContext(), R.string.no_mail_app, Toast.LENGTH_SHORT).show();
        }
    }

    private void openShare() {
        try {
            String shareTittle = getString(R.string.share_title);
            String shareContent = getString(R.string.share_content);
            String shareUrl = getString(R.string.share_url);
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareTittle + "\n" + shareContent + "\n" + shareUrl);
            sendIntent.setType("text/plain");
            startActivityForResult(sendIntent, 0);
        } catch (ActivityNotFoundException anf) {
            Toast.makeText(getContext(), R.string.no_share_app, Toast.LENGTH_SHORT).show();
        }
    }
}
