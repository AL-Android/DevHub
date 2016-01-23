package es.alejandrolora.devhub.activitys;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.ParseAnalytics;
import com.parse.ParseUser;
import org.json.JSONException;
import java.util.List;
import es.alejandrolora.devhub.Api.API;
import es.alejandrolora.devhub.R;
import es.alejandrolora.devhub.Util.Util;
import es.alejandrolora.devhub.adapters.CustomAdapterCategories;
import es.alejandrolora.devhub.models.Category;


public class CategoriesActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private List<Category> listCategories;
    private ListView listView;
    private CustomAdapterCategories adapter;
    private Toolbar toolbar;

    private boolean network;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        // Analytics
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        // If it comes from parse notification push
        if (getIntent().hasExtra("com.parse.Data")) {
            try {
                Util.getActivityFromPushNotification(this, getIntent());
            } catch (JSONException e) {
                // Notification Empty!
            }
        }

        Util.showProgressBar(this, null);

        if (checkCurrentUser()) {
            Util.registerEmailToInstalation(ParseUser.getCurrentUser().getEmail());

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            toolbar.setLogo(R.mipmap.ic_launcher);

            listView = (ListView) findViewById(R.id.listViewCategories);
            listView.setOnItemClickListener(this);

            network = Util.isInternetAvailable(this);


            if (!network) {
                Util.dismissProgressBar();
                showInternetAlert();

            } else {
                listCategories = API.getAllCategoriesFromAPI();
                adapter = new CustomAdapterCategories(this);
                listView.setAdapter(adapter);
            }
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_categories;
    }

    private void showInternetAlert(){
        new MaterialDialog.Builder(this)
                .title(R.string.network_error_title)
                .content(R.string.network_error_msg)
                .backgroundColor(Color.WHITE)
                .titleColorRes(R.color.colorPrimaryDialog)
                .contentColorRes(R.color.colorPrimaryDialogText)
                .positiveColorRes(R.color.colorWhite)
                .negativeColorRes(R.color.colorPrimaryDialog)
                .positiveText(R.string.wifi_settings)
                .btnSelector(R.drawable.md_btn_selector_custom, DialogAction.POSITIVE)
                .negativeText(R.string.exit)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        finish();
                    }
                }).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!Util.isTablet(this)){
            getMenuInflater().inflate(R.menu.menu_categories, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent i = new Intent(this, CoursesActivity.class);
        i.putExtra("idCategory", listCategories.get(position).getId());
        i.putExtra("categoryName", listCategories.get(position).getName());
        i.putExtra("color", listCategories.get(position).getColor());
        startActivity(i);
    }


    private boolean checkCurrentUser() {
        if (ParseUser.getCurrentUser() == null) {
            Util.dismissProgressBar();
            Intent i = new Intent(this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            return false;
        }
        return true;
    }
}