package com.austin.supermandict.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.austin.supermandict.R;
import com.xiaomi.mistatistic.sdk.MiStatInterface;

/**
 * Created by HoHoibin on 03/01/2018.
 * Email: imhhb1997@gmail.com
 */

public class SearchActivity extends AppCompatActivity {
    private SearchView searchView;

    private FragmentManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        manager = getSupportFragmentManager();

        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setIconified(false);
        searchView.setSubmitButtonEnabled(true);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                intent.putExtra("queryWord",query);
                intent.setAction("com.austin.supermandict.wordsearch");
                startActivity(intent);
                finish();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            onBackPressed();
        }

        if (id == R.id.action_search){
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        MiStatInterface.recordPageStart(this, "Search Page");
    }

    @Override
    public void onPause() {
        super.onPause();
        MiStatInterface.recordPageEnd();
    }

}
