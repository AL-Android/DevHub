package es.alejandrolora.devhub.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import es.alejandrolora.devhub.Util.Util;

/**
 * Created by Alejandro on 20/4/15.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private CharSequence Titles[];
    private int NumbOfTabs;
    private String idCourse;
    private String color;
    // Build a Constructor and assign the passed Values to appropriate values in the class

    public ViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb, String idCourse, String color) {
        super(fm);
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
        this.idCourse = idCourse;
        this.color = color;
    }
    //This method return the fragment for the every position in the View Pager

    @Override
    public Fragment getItem(int position) {

        if (position == 0) // if the position is 0 we are returning the First tab
        {
            VideosTab videosTab = new VideosTab();
            Bundle b = new Bundle();
            b.putString("idCourse", idCourse);
            b.putString("color", color);
            videosTab.setArguments(b);
            return videosTab;
        } else// As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        {
            CommentsTab commentsTab = new CommentsTab();
            Bundle b = new Bundle();
            b.putString("idCourse", idCourse);
            b.putString("color", color);
            commentsTab.setArguments(b);
            return commentsTab;
        }
    }
    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }


    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}