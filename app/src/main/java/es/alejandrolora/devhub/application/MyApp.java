package es.alejandrolora.devhub.application;

import android.app.Application;
import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import es.alejandrolora.devhub.models.Category;
import es.alejandrolora.devhub.models.Comment;
import es.alejandrolora.devhub.models.Course;
import es.alejandrolora.devhub.models.Score;
import es.alejandrolora.devhub.models.User;
import es.alejandrolora.devhub.models.Video;

/**
 * Created by Alejandro on 11/4/15.
 */
public class MyApp extends Application{

    private final String APP_ID = "7fY9rjjDSr5LNFTxO2qLqeVTVEZWgGmxogB5JhY8";
    private final String KEY_ID = "5YhDkfZpRD9IswYtsjO5lBDCUvTXfhGCxFMytd3F";


    @Override
    public void onCreate() {
        super.onCreate();

        // Register our POJOs like subclass from Parse
        ParseObject.registerSubclass(Category.class);
        ParseObject.registerSubclass(Course.class);
        ParseObject.registerSubclass(Video.class);
        ParseObject.registerSubclass(Comment.class);
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(Score.class);

        ParseCrashReporting.enable(this);
        Parse.initialize(this, APP_ID, KEY_ID);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

}
