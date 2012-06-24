/*
 * Copyright (C) 2011 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cyanogenmod.trebuchet.preference;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.cyanogenmod.trebuchet.LauncherApplication;
import com.cyanogenmod.trebuchet.R;

import java.util.ArrayList;

public class PreferenceSettings extends Activity {

    public static final String PHONE_SEARCH_BAR = "ui_homescreen_general_search";
    public static final String SEARCH_BAR = "ui_tablet_search";
    public static final String ALL_APPS_BAR = "ui_tablet_workspace_allapps";
    public static final String COMBINED_BAR = "ui_tablet_workspace_combined_bar";
    public static final String CENTER_ALLAPPS = "ui_tablet_workspace_allapps_center";
    public static final String HIDE_DRAWER_TAB = "ui_drawer_hide_topbar";
    public static final String JOIN_WIDGETS = "ui_drawer_widgets_join_apps";
    public static final String SMALLER_ICONS = "ui_tablet_smaller_icons";
    public static final String DOCK_LABELS = "ui_tablet_show_dock_icon_labels";
    public static final String HOTSEAT_POSITIONS = "ui_hotseat_apps";
    public static final String HOTSEAT_ALLAPPS_POSITION = "ui_hotseat_all_apps";
    public static final String ALLAPPS_POSITION = "ui_tablet_all_apps_corner";
    public static final String SEARCH_POSITION = "ui_tablet_search_corner";
    public static final String HOMESCREENS = "ui_homescreen_screens";
    public static final String DEFAULT_HOMESCREEN = "ui_homescreen_default_screen";
    public static final String HOMESCREEN_TRANSITION = "ui_homescreen_scrolling_transition_effect";
    public static final String VERTICAL_PADDING = "ui_homescreen_screen_padding_vertical";
    public static final String HORIZONTAL_PADDING = "ui_homescreen_screen_padding_horizontal";
    public static final String DRAWER_TRANSITION = "ui_drawer_scrolling_transition_effect";
    public static final String HOMESCREEN_GRID = "ui_homescreen_grid";
    public static final String HOMESCREEN_DOUBLETAP = "ui_homescreen_doubletap";
    public static final String HOMESCREEN_SWIPEUP = "ui_homescreen_swipe_up";
    public static final String HOMESCREEN_SWIPEDOWN = "ui_homescreen_swipe_down";
    public static final String DRAWER_SWIPEUP = "ui_drawer_swipe_up";
    public static final String DRAWER_SWIPEDOWN = "ui_drawer_swipe_down";
    public static final String APP_BAR_LONGCLICK = "ui_app_bar_long_click";
    public static final String HDT_APPLICATION = "hdt_application";
    public static final String CUSTOM_BUTTON_ONE = "ui_tablet_custom_button_one";
    public static final String CUSTOM_BUTTON_TWO = "ui_tablet_custom_button_two";
    public static final String CUSTOM_BUTTON_THREE = "ui_tablet_custom_button_three";
    public static final String CUSTOM_BUTTON_FOUR = "ui_tablet_custom_button_four";
    public static final String CUSTOM_BUTTON_FIVE = "ui_tablet_custom_button_five";
    public static final String CUSTOM_BUTTON_SIX = "ui_tablet_custom_button_six";
    public static final String CUSTOM_BUTTON_SEVEN = "ui_tablet_custom_button_seven";
    public static final String CUSTOM_BUTTON_EIGHT = "ui_tablet_custom_button_eight";
    public static final String SHOW_DOCK = "ui_homescreen_general_show_hotseat";
    public static final String SHOW_DOCK_BACKGROUND = "ui_hotseat_background";
    public static final String SHOW_DOCK_DIVIDER = "ui_homescreen_indicator_background";
    public static final String SHOW_DOCK_APPS_BUTTON = "ui_homescreen_general_show_hotseat_allapps";
    public static final String MAXIMIZE_WORKSPACE = "ui_homescreen_maximize";
    public static final String SEARCH_BACKGROUND = "ui_search_background";

    private SharedPreferences mPrefs;

    ViewPager mViewPager;
    TabsAdapter mTabsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefs = getSharedPreferences(PreferencesProvider.PREFERENCES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
                editor.putBoolean(PreferencesProvider.PREFERENCES_CHANGED, true);
                editor.commit();

        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);

        final ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayOptions(0);

        mTabsAdapter = new TabsAdapter(this, mViewPager);
        mTabsAdapter.addTab(bar.newTab().setText(R.string.preferences_interface_homescreen_title),
                HomescreenFragmentActivity.class, null);
        if (LauncherApplication.isScreenLarge()) {
            mTabsAdapter.addTab(bar.newTab().setText(R.string.preferences_interface_tablet_title),
                    TabletFragmentActivity.class, null);
        }
        mTabsAdapter.addTab(bar.newTab().setText(R.string.preferences_interface_dock_title),
                DockFragmentActivity.class, null);
        mTabsAdapter.addTab(bar.newTab().setText(R.string.preferences_interface_drawer_title),
                DrawerFragmentActivity.class, null);
        mTabsAdapter.addTab(bar.newTab().setText(R.string.preferences_interface_gestures_title),
                GestureFragmentActivity.class, null);
        mTabsAdapter.addTab(bar.newTab().setText(R.string.preferences_interface_icons_title),
                IconsFragmentActivity.class, null);

        if (savedInstanceState != null) {
            bar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
    }

    public static class TabsAdapter extends FragmentPagerAdapter
            implements ActionBar.TabListener, ViewPager.OnPageChangeListener {
        private final Context mContext;
        private final ActionBar mActionBar;
        private final ViewPager mViewPager;
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

        static final class TabInfo {
            private final Class<?> clss;
            private final Bundle args;

            TabInfo(Class<?> _class, Bundle _args) {
                clss = _class;
                args = _args;
            }
        }

        public TabsAdapter(Activity activity, ViewPager pager) {
            super(activity.getFragmentManager());
            mContext = activity;
            mActionBar = activity.getActionBar();
            mViewPager = pager;
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }

        public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
            TabInfo info = new TabInfo(clss, args);
            tab.setTag(info);
            tab.setTabListener(this);
            mTabs.add(info);
            mActionBar.addTab(tab);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            TabInfo info = mTabs.get(position);
            return Fragment.instantiate(mContext, info.clss.getName(), info.args);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            mActionBar.setSelectedNavigationItem(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            Object tag = tab.getTag();
            for (int i=0; i<mTabs.size(); i++) {
                if (mTabs.get(i) == tag) {
                    mViewPager.setCurrentItem(i);
                }
            }
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
        }
    }
}
