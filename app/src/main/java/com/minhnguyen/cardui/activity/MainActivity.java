package com.minhnguyen.cardui.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.minhnguyen.cardui.IDialogWriteTagsListener;
import com.minhnguyen.cardui.R;
import com.minhnguyen.cardui.constant.StringValue;
import com.minhnguyen.cardui.fragment.AccessControlFragment;
import com.minhnguyen.cardui.fragment.DailyStatisticsFragment;
import com.minhnguyen.cardui.fragment.MonthlyStatisticsFragment;
import com.minhnguyen.cardui.fragment.WriteTagsFragment;
import com.nxp.nfclib.NxpNfcLib;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements IDialogWriteTagsListener {

    private String MODE = StringValue.MODE_READING;
    private boolean isDialogWriteDisplayed = false;

    // TapLinx library instance
    private NxpNfcLib nxpNfcLib = null;

    @BindView(R.id.dlMain)
    DrawerLayout dlMain;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nvView)
    NavigationView nvView;
    ActionBarDrawerToggle drawerToggle;

    private AccessControlFragment accessControlFragment;
    private WriteTagsFragment writeTagsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        initializeLibrary();

        setupDrawerContent(nvView);
        // Select item_information on startup
        nvView.getMenu().performIdentifierAction(R.id.navAccessControl, 0);

        drawerToggle = setupDrawerToggle();

        // Tie DrawerLayout events to the ActionBarToggle
        dlMain.addDrawerListener(drawerToggle);
    }

    /**
     * Method for TapLinx library instance
     * Register activity with parameter Activity and String packgage key
     */
    @TargetApi(19)
    private void initializeLibrary() {
        nxpNfcLib = NxpNfcLib.getInstance();
        nxpNfcLib.registerActivity(this, StringValue.PACKAGE_KEY);
    }

    /**
     * If app has become the user focus and is visible in the foreground
     * -> Resume the nfc. Therefore dispatching NFC intents are activated
     * in onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        nxpNfcLib.startForeGroundDispatch();
    }

    @Override
    protected void onPause() {
        nxpNfcLib.stopForeGroundDispatch();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                dlMain.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Method for called if the app receives an intent from the system or other sources
     * In this case, NFC intent (signal)
     *
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(StringValue.TAG, "Thấy thẻ rồi nè!!!");

        switch (MODE) {
            case StringValue.MODE_READING:
                if (null != accessControlFragment && accessControlFragment.isVisible()) {
                    accessControlFragment.onNFCDetected(intent, nxpNfcLib);
                }
                break;
            case StringValue.MODE_WRITING:
                if (!isDialogWriteDisplayed) {
//                    Utilities.getInstance().toastMessage(this, getString(R.string.message_waiting));
                } else {
                    if (null != writeTagsFragment && writeTagsFragment.isVisible() && isDialogWriteDisplayed) {
                        writeTagsFragment.onNFCDetected(intent);
                    }
                }
                break;
            default:
                break;
        }

        super.onNewIntent(intent);
    }

    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE 1: Make sure to override the method with only a single `Bundle` argument
    // Note 2: Make sure you implement the correct `onPostCreate(Bundle savedInstanceState)` method.
    // There are 2 signatures and only `onPostCreate(Bundle state)` shows the hamburger icon.
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onNFCDialogDisplayed() {
        isDialogWriteDisplayed = true;
    }

    @Override
    public void onNFCDialogDismissed() {
        isDialogWriteDisplayed = false;
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, dlMain, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    public void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });

    }

    private void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment;
        accessControlFragment = new AccessControlFragment();
        writeTagsFragment = new WriteTagsFragment();
        MODE = "";
        switch (menuItem.getItemId()) {
            case R.id.navAccessControl:
                fragment = AccessControlFragment.newInstance();
                MODE = StringValue.MODE_READING;
                accessControlFragment = (AccessControlFragment) fragment;
                break;
            case R.id.navDayStatistical:
                fragment = DailyStatisticsFragment.newInstance();
                break;
            case R.id.navMonthStatistical:
                fragment = MonthlyStatisticsFragment.newInstance();
                break;
//            case R.id.navWriteTags:
//                fragment = WriteTagsFragment.newInstance();
//                MODE = StringValue.MODE_WRITING;
//                writeTagsFragment = (WriteTagsFragment) fragment;
//                break;
            default:
                fragment = new Fragment();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        // Highlight the selected item_information has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        dlMain.closeDrawers();
    }

    public void onSettingsClick(MenuItem item) {
        // Close the navigation drawer
        dlMain.closeDrawers();
    }

    public void onAboutUsClick(MenuItem item) {
        // Close the navigation drawer
        dlMain.closeDrawers();
        Intent intent = new Intent(this, AboutUsActivity.class);
        startActivity(intent);
    }
}