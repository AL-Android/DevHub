package es.alejandrolora.devhub.activitys;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import es.alejandrolora.devhub.R;
import es.alejandrolora.devhub.Util.SlidingTabLayout;
import es.alejandrolora.devhub.Util.Util;
import es.alejandrolora.devhub.fragments.ViewPagerAdapter;

public class VideosCommentsActivity extends BaseActivity {

    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    final int Numboftabs = 2;

    private String idCourse;
    private String titleCurso;
    private String color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            idCourse = b.getString("idCourse");
            titleCurso = b.getString("title");
            color = b.getString("color");
            toolbar.setTitle(titleCurso);
            Util.changeColorToolBar(this, toolbar, color);
        }
        setUpTabs();
    }


    private void setUpTabs() {
        CharSequence Titles[] = {getString(R.string.videos_string), getString(R.string.comments_string)};
        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, Numboftabs, idCourse, color);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setBackgroundColor(Color.parseColor(color));
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(android.R.color.white);
            }
        });
        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_videos_comments;
    }

}