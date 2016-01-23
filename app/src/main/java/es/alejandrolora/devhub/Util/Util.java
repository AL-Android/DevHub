package es.alejandrolora.devhub.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.widgets.SnackBar;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import es.alejandrolora.devhub.R;
import es.alejandrolora.devhub.models.User;

/**
 * Created by Alejandro on 25/4/15.
 */
public class Util {

    public static MaterialDialog progress;



    public static void changeColorToolBar(Activity act, Toolbar toolbar, String color) {
        toolbar.setBackgroundColor(Color.parseColor(color));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = act.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(DarkerColor(color));
        }
    }

    public static int DarkerColor(String color) {
        int c = getIntColorFromHex(color);

        int r = Color.red(c);
        int b = Color.blue(c);
        int g = Color.green(c);

        return Color.rgb((int) (r * .8), (int) (g * .8), (int) (b * .8));
    }

    public static int LighterColor(String color) {
        int c = getIntColorFromHex(color);

        int r = Color.red(c);
        int b = Color.blue(c);
        int g = Color.green(c);

        return Color.rgb((int) (r * 1.35), (int) (g * 1.35), (int) (b * 1.35));
    }

    public static int getIntColorFromHex(String color){
        return Integer.parseInt(color.replaceFirst("^#", ""), 16);
    }

    public static String getHexFromIntColor(int color){
        return String.format("#%06X", 0xFFFFFF & color);
    }

    public static String getDurationFromSeconds(int seconds){
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;
        return twoDigitString(minutes) + " : " + twoDigitString(seconds);
    }

    public static String twoDigitString(int number) {
        if (number == 0) {
            return "00";
        }
        if (number / 10 == 0) {
            return "0" + number;
        }
        return String.valueOf(number);
    }

    public static String getDateFormatted(Date date, String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        return sdf.format(date);
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        if (i == null || !i.isConnected() || !i.isAvailable())
            return false;
        else
            return true;
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static void showProgressBar(Activity act, String colorWidget){

        if(colorWidget != null){
            progress = new MaterialDialog.Builder(act)
                    .content(R.string.please_wait)
                    .backgroundColorRes(R.color.windowBackgroundColor)
                    .cancelable(false)
                    .progress(true, 0)
                    .widgetColor(Color.parseColor(colorWidget))
                    .build();
        }else{
            progress = new MaterialDialog.Builder(act)
                    .content(R.string.please_wait)
                    .backgroundColorRes(R.color.windowBackgroundColor)
                    .cancelable(false)
                    .progress(true, 0)
                    .build();
        }
        progress.show();
    }

    public static void dismissProgressBar(){
        if (progress != null){
            progress.dismiss();
        }
    }

    public static void pickPhotoGallery(Activity act, String msgChooser, int request){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        act.startActivityForResult(Intent.createChooser(intent, msgChooser), request);
    }

    public static void lowQualityPhotoFromUriToParseFileBackground(final Activity act, final Uri photoUri, final int quality){

        class LowQualityPhotoAndSaveAsyncTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {

                try{
                    // Get Stream of gallery picture and then, its Bitmap
                    InputStream stream = act.getContentResolver().openInputStream(photoUri);
                    Bitmap bmp = BitmapFactory.decodeStream(stream);
                    stream.close();

                    // Low quality of Bitmap and convert to byte array
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, quality, baos);
                    byte[] byteArray = baos.toByteArray();

                    // Create parseFile to be uploaded to Parse.com
                    ParseFile newAvatar = new ParseFile("avatar", byteArray);

                    User currentUser = (User) ParseUser.getCurrentUser();
                    currentUser.setPhoto(newAvatar);
                    currentUser.saveInBackground();

                } catch (FileNotFoundException e) {
                    new SnackBar(act, act.getString(R.string.image_avatar_error), null, null).show();
                } catch (IOException e) {
                    new SnackBar(act, act.getString(R.string.image_avatar_error), null, null).show();
                }

                return null;
            }
        }

        new LowQualityPhotoAndSaveAsyncTask().execute();
    }

    public static int getReferenceStarIconByAvegare(float average){
        if (average == 0){
            return R.drawable.stars_empty;
        }else if(average <= 0.75){
            return R.drawable.stars_0_5;
        }else if(average <= 1.25){
            return R.drawable.stars_1;
        }else if(average <= 1.75){
            return R.drawable.stars_1_5;
        }else if(average <= 2.25){
            return R.drawable.stars_2;
        }else if(average <= 2.75){
            return R.drawable.stars_2_5;
        }else if(average <= 3.25){
            return R.drawable.stars_3;
        }else if(average <= 3.75){
            return R.drawable.stars_3_5;
        }else if(average <= 4.25){
            return R.drawable.stars_4;
        }else if(average <= 4.75){
            return R.drawable.stars_4_5;
        }else{
            return R.drawable.stars_fill;
        }
    }

    public static void getActivityFromPushNotification(Context context, Intent i) throws JSONException {
        String jsonData = i.getStringExtra("com.parse.Data");
        JSONObject json = new JSONObject(jsonData);
        Intent pushIntent = new Intent();
        pushIntent.setClassName(context.getPackageName(), context.getPackageName() + ".activitys.VideosCommentsActivity");
        pushIntent.putExtra("idCourse", json.getString("idCourse"));
        pushIntent.putExtra("title", json.getString("titleCourse"));
        pushIntent.putExtra("color", json.getString("colorCourse"));
        pushIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pushIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(pushIntent);
    }

    public static boolean isTablet(Context context){
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    public static void registerNotification(boolean check){
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("push", check);
        installation.saveInBackground();
    }

    public static void registerEmailToInstalation(String email){
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("currentEmail", email);
        installation.saveInBackground();
    }

}