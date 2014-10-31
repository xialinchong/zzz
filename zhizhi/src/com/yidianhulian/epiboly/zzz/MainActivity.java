package com.yidianhulian.epiboly.zzz;

import java.util.ArrayList;
import java.util.HashMap;

import com.yidianhulian.epiboly.ZZZApplication;

import android.app.Activity;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MainActivity extends Activity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the
     * navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in
     * {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private ArrayAdapter<String> categories = null;
    private ArrayList<HashMap<String, String>> addresses = null;
    private ArrayList<HashMap<String, String>> adapterArrayList;

    private final static int GET_COUPONS = 2;
    private final static int GET_FAVORITES = 4;
    private final static int GET_SYSTEM_SETTING = 6;

    private Fragment mContent;

    private int current_tab = 0; // 当前打开的栏目

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
                .findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        mNavigationDrawerFragment.closeDrawer();

        Intent intent = getIntent();
        adapterArrayList = (ArrayList<HashMap<String, String>>) intent
                .getSerializableExtra("adapter");
        addresses = (ArrayList<HashMap<String, String>>) intent
                .getSerializableExtra("addresses");

        initActionBar(savedInstanceState);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Fragment newContent = null;
        if ((position == 0 || position == 1 || position == 2)
                && !ZZZApplication.isLogin()) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, Signin.class);
            MainActivity.this.startActivity(intent);
            return;
        }
        switch (position) {
        case 0:
            newContent = new CouponList(GET_COUPONS);
            break;
        case 1:
            newContent = new CouponList(GET_FAVORITES);
            break;
        case 2:
            newContent = new CouponList(GET_SYSTEM_SETTING);
            break;
        case 3:
            newContent = new About();
            break;
        case 4:
            ZZZApplication.getInstance().clearLoginUser();
            initMainView();

            mNavigationDrawerFragment.is_show_logout();
            // Intent intent = new Intent(this, Signin.class);
            // startActivity(intent);

            return;
        }
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, newContent).commit();

        mContent = newContent;
    }

    public void initActionBar(final Bundle savedInstanceState) {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        categories = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item);
        for (int i = 0; i < adapterArrayList.size(); i++) {
            categories.add(adapterArrayList.get(i).get("name"));
        }
        categories
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        actionBar.setListNavigationCallbacks(categories,
                new OnNavigationListener() {
                    @Override
                    public boolean onNavigationItemSelected(int itemPosition,
                            long itemId) {
                        if (savedInstanceState != null) {
                            mContent = getFragmentManager().getFragment(
                                    savedInstanceState, "mContent");
                        }
                        if (mContent == null) {
                            initMainView();
                        } else if (mContent instanceof GoodsListNav) {
                            ((GoodsListNav) mContent)
                                    .setCurrentCategory(adapterArrayList.get(
                                            itemPosition).get("id"));
                            ((GoodsListNav) mContent).updateCouponGoodsList();
                        } else {
                            mContent = new GoodsListNav(addresses,
                                    adapterArrayList.get(itemPosition)
                                            .get("id"), "p");
                            getFragmentManager().beginTransaction()
                                    .replace(R.id.content_frame, mContent)
                                    .commit();
                        }

                        return true;
                    }
                });
    }

    public void initMainView() {
        mContent = new GoodsListNav(addresses, "0", "p");
        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, mContent).commit();
    }

    public void restoreActionBar() {
        Fragment contentFragment = getFragmentManager().findFragmentById(
                R.id.content_frame);
        if (current_tab == 0) {
            changeActionBar(ActionBar.NAVIGATION_MODE_LIST);
        } else if (contentFragment != null
                && !(contentFragment instanceof GoodsListNav)) {
            changeActionBar(ActionBar.NAVIGATION_MODE_LIST);
        } else {
            changeActionBar(ActionBar.NAVIGATION_MODE_STANDARD);
        }
    }

    public void changeActionBar(int mode) {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(mode);
    }

    public void setCurrentTab(int tab) {
        current_tab = tab;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.btn_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
