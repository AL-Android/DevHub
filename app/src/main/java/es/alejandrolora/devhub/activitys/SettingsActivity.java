package es.alejandrolora.devhub.activitys;

import android.os.Bundle;
import es.alejandrolora.devhub.R;
import es.alejandrolora.devhub.fragments.SettingsFragment;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(R.id.contentFrame, new SettingsFragment()).commit();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_settings;
    }
}
