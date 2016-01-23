package es.alejandrolora.devhub.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import es.alejandrolora.devhub.Api.API;
import es.alejandrolora.devhub.R;
import es.alejandrolora.devhub.Util.Util;
import es.alejandrolora.devhub.adapters.CustomAdapterCourses;
import es.alejandrolora.devhub.models.Course;

public class CoursesActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private Toolbar toolbar;
    private ListView listView;
    private List<Course> listCourses;
    private CustomAdapterCourses adapter;

    private String categoryNameReceived;
    private String idCategory;
    private String color;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.listViewCourses);
        listView.setOnItemClickListener(this);

        Bundle b = getIntent().getExtras();
        if (b != null){
            idCategory = b.getString("idCategory");
            categoryNameReceived = b.getString("categoryName");
            color = b.getString("color");
            toolbar.setTitle(categoryNameReceived);
            Util.changeColorToolBar(this, toolbar, color);
            Util.showProgressBar(this, color);
        }

        showHideIfCommentIsEmpty();

        listCourses = API.getAllCoursesByCategoryFromAPI(idCategory);
        adapter = new CustomAdapterCourses(this, idCategory);
        listView.setAdapter(adapter);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_courses;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                // Get back all data and not have to do callback again
                NavUtils.navigateUpTo(this, getIntent());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent i = new Intent(this, VideosCommentsActivity.class);
        i.putExtra("idCourse", listCourses.get(position).getId());
        i.putExtra("title", listCourses.get(position).getTitle());
        i.putExtra("color", color);
        startActivity(i);
    }

    private void showHideIfCommentIsEmpty(){
        TextView text = (TextView) findViewById(R.id.textViewNoCourses);
        if (API.isCourseEmpty(idCategory))
            text.setVisibility(View.VISIBLE);
        else
            text.setVisibility(View.INVISIBLE);
    }
}
