package com.austin.supermandict.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.stetho.Stetho;
import com.austin.supermandict.R;
import com.xiaomi.mistatistic.sdk.MiStatInterface;

/**
 * Created by HoHoibin on 03/01/2018.
 * Email: imhhb1997@gmail.com
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private NavigationView navView;
    private Menu mMenu;

    private IndexFragment indexFragment;
    private WordFragment wordFragment;
    private NoteBookFragment noteBookFragment;
    private AboutFragment aboutFragment;

    private String queryWord;

    private static final String ACTION_SEARCH_WORD = "com.austin.supermandict.wordsearch";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);
        initViews();

        if (savedInstanceState != null) {
            FragmentManager manager = getSupportFragmentManager();
            indexFragment = (IndexFragment) manager.getFragment(savedInstanceState,"indexFragment");
            wordFragment = (WordFragment) manager.getFragment(savedInstanceState, "wordFragment");
            noteBookFragment = (NoteBookFragment) manager.getFragment(savedInstanceState,"noteBookFragment");
            aboutFragment = (AboutFragment) manager.getFragment(savedInstanceState,"aboutFragment");
        } else {
            indexFragment = new IndexFragment();
            wordFragment = new WordFragment();
            noteBookFragment = new NoteBookFragment();
            aboutFragment = new AboutFragment();
        }

        FragmentManager manager = getSupportFragmentManager();

        if (manager.getFragments().isEmpty()) {
            manager.beginTransaction()
                    .add(R.id.container_main, indexFragment, "indexFragment")
                    .commit();
            manager.beginTransaction()
                    .add(R.id.container_main,wordFragment,"wordFragment")
                    .commit();
            manager.beginTransaction()
                    .add(R.id.container_main,noteBookFragment,"noteBookFragment")
                    .commit();
            manager.beginTransaction()
                    .add(R.id.container_main,aboutFragment,"aboutFragment")
                    .commit();
        }

        Intent intent = getIntent();
        //从Intent当中根据key取得value
        if (intent != null) {
            if (intent.getAction().equals(ACTION_SEARCH_WORD)){
                showHideFragment(1);
                queryWord = intent.getStringExtra("queryWord");
                Bundle bundle = new Bundle();
                bundle.putString("queryWord", queryWord);
                wordFragment.setArguments(bundle);
            } else {
                showHideFragment(0);
            }
        }

    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        updateMenu(true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search){
            startActivity(new Intent(MainActivity.this, SearchActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void showHideFragment(@IntRange(from = 0, to = 3) int position) {

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().hide(indexFragment).commit();
        manager.beginTransaction().hide(wordFragment).commit();
        manager.beginTransaction().hide(noteBookFragment).commit();
        manager.beginTransaction().hide(aboutFragment).commit();

        if (position == 0) {
            manager.beginTransaction().show(indexFragment).commit();
            toolbar.setTitle(R.string.app_name);
            navView.setCheckedItem(R.id.nav_translate);
        } else if (position == 1) {
            toolbar.setTitle(R.string.title_word_search);
            manager.beginTransaction().show(wordFragment).commit();
            navView.setCheckedItem(R.id.nav_translate);
        } else if (position == 2) {
            toolbar.setTitle(R.string.title_note_book);
            manager.beginTransaction().show(noteBookFragment).commit();
            navView.setCheckedItem(R.id.nav_notebook);
        } else if (position == 3) {
            toolbar.setTitle(R.string.title_about);
            manager.beginTransaction().show(aboutFragment).commit();
            navView.setCheckedItem(R.id.nav_about);
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_translate) {
            showHideFragment(0);
            updateMenu(true);
        } else if (id == R.id.nav_notebook) {
            showHideFragment(2);
            updateMenu(false);
        } else if (id == R.id.nav_about) {
            showHideFragment(3);
            updateMenu(false);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (indexFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, "indexFragment", indexFragment);
        }

        if (wordFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, "wordFragment", wordFragment);
        }

        if (noteBookFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, "noteBookFragment", noteBookFragment);
        }

        if (aboutFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, "aboutFragment", aboutFragment);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        MiStatInterface.recordPageStart(this, "Main Page");
    }

    @Override
    public void onPause() {
        super.onPause();
        MiStatInterface.recordPageEnd();
    }

    private void updateMenu(boolean showSearch) {
        if (showSearch) {
            mMenu.findItem(R.id.action_search).setVisible(true);
            mMenu.findItem(R.id.action_search).setEnabled(true);
        } else {
            mMenu.findItem(R.id.action_search).setVisible(false);
            mMenu.findItem(R.id.action_search).setEnabled(false);
        }
    }

}
