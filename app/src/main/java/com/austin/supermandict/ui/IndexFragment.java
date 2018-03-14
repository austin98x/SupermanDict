package com.austin.supermandict.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.austin.supermandict.R;
import com.austin.supermandict.constant.Constants;
import com.austin.supermandict.db.NoteBookDatabaseHelper;
import com.austin.supermandict.model.NoteBookItem;
import com.austin.supermandict.util.DBUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by HoHoibin on 08/01/2018.
 * Email: imhhb1997@gmail.com
 */

public class IndexFragment extends Fragment {
    private TextView textViewEng;
    private TextView textViewChi;
    private TextView textViewCount;
    private ImageView imageViewMain;
    private ImageView ivShare;
    private ImageView ivCopy;
    private CardView cvStoreHint;

    private String imageUrl = null;

    private Boolean isRendered = false;

    private NoteBookDatabaseHelper dbHelper;
    private ArrayList<NoteBookItem> list = new ArrayList<NoteBookItem>();

    private Activity activity;

    private String message;

    public IndexFragment(){

    }

    public static IndexFragment newInstance()
    {
        return new IndexFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new NoteBookDatabaseHelper(getActivity(),"MyNote.db",null,1);

    }

    private void sendRequest() {
        OkHttpClient okHttpClient = new OkHttpClient();
        final Handler mainHandler = new Handler(Looper.getMainLooper());
        Request request = new Request.Builder()
                .url(Constants.DAILY_SENTENCE)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println(e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    parseJSONWithJSONObject(response.body().string());
                }

            }
        });
    }

    private void parseJSONWithJSONObject(String responseData) {
//        Gson gson = new Gson();
//        final DailyOneItem dailyOneItem = gson.fromJson(responseData,DailyOneItem.class);

        try {
            final JSONObject dailyOneItem = new JSONObject(responseData);
            Log.e("IndexFragment",dailyOneItem.getString("content") );
            Log.e("IndexFragment",dailyOneItem.getString("note") );
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        textViewEng.setText(dailyOneItem.getString("content"));
                        textViewChi.setText(dailyOneItem.getString("note"));
                        imageUrl = dailyOneItem.getString("picture2");
                        Glide.with(getActivity())
                                .load(imageUrl)
                                .asBitmap()
                                .centerCrop()
                                .into(imageViewMain);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        initViews(view);

        getDataFromDB();

        textViewCount.setText(list.size()+"");

        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String shareTittle = getString(R.string.title_daily_one);
                    String shareContent = textViewEng.getText().toString() + "\n" + textViewChi.getText().toString();
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, shareTittle + "\n" + shareContent);
                    sendIntent.setType("text/plain");
                    startActivityForResult(sendIntent, 0);
                } catch (ActivityNotFoundException anf) {
                    Snackbar.make(ivShare, R.string.no_share_app, Snackbar.LENGTH_SHORT).show();
                }

            }
        });

        ivCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager manager = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", String.valueOf(textViewEng.getText() + "\n" + textViewChi.getText()));
                manager.setPrimaryClip(clipData);

                Snackbar.make(ivCopy, R.string.copy_done, Snackbar.LENGTH_SHORT).show();
            }
        });


        if(!isRendered)
        {
            sendRequest();
            isRendered = true;
        }

        return view;
    }

    private void initViews(View view) {
        textViewEng = view.findViewById(R.id.text_view_eng);
        textViewChi =  view.findViewById(R.id.text_view_chi);
        textViewCount = view.findViewById(R.id.tv_ItemCount);
        imageViewMain = view.findViewById(R.id.image_view_daily);
        cvStoreHint = view.findViewById(R.id.cardView_StoreHint);

        ivShare = view.findViewById(R.id.image_view_share);
        ivCopy = view.findViewById(R.id.image_view_copy);
    }


    private void getDataFromDB() {
        if (list != null) {
            list.clear();
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("notebook",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                String in = cursor.getString(cursor.getColumnIndex("input"));
                String out = cursor.getString(cursor.getColumnIndex("output"));
                NoteBookItem item = new NoteBookItem(in,out);
                list.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
    }


    @Override
    public void onResume() {
        super.onResume();
        sendRequest();
        getDataFromDB();
        textViewCount.setText(list.size()+"");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        getDataFromDB();
        if (list != null) {
            textViewCount.setText(list.size()+"");
        }
    }
}
