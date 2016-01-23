package es.alejandrolora.devhub.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.widgets.SnackBar;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;

import es.alejandrolora.devhub.Api.API;
import es.alejandrolora.devhub.R;
import es.alejandrolora.devhub.Util.Util;
import es.alejandrolora.devhub.activitys.AvatarActivity;
import es.alejandrolora.devhub.activitys.LoginActivity;
import es.alejandrolora.devhub.models.User;
import android.view.View;

public class SettingsFragment extends PreferenceFragment {

    private static final boolean ALWAYS_SIMPLE_PREFS = false;

    private Preference buttonUserName;
    private Preference buttonAvatar;
    private Preference buttonLogOut;
    private Preference buttonDeleteUser;

    private CheckBoxPreference buttonNotifications;

    private Preference buttonAbout;
    private Preference buttonContact;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupSimplePreferencesScreen();
        updateNotification();

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);


        /*** My Account  ***/
        changeUserName();
        changeAvatar();
        logOut();
        deleteAccount();
        /*** Notifications  ***/
        changeNotifications();
        /*** Help  ***/
        showContactMe();
        showAboutMe();

    }



    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(getActivity())) {
            return;
        }

        addPreferencesFromResource(R.xml.pref_general);

        PreferenceCategory fakeHeaderAccount = new PreferenceCategory(getActivity());
        fakeHeaderAccount.setTitle(R.string.pref_header_my_account);
        getPreferenceScreen().addPreference(fakeHeaderAccount);
        addPreferencesFromResource(R.xml.pref_account);

        PreferenceCategory fakeHeaderNotifications = new PreferenceCategory(getActivity());
        fakeHeaderNotifications.setTitle(R.string.pref_header_notifications);
        getPreferenceScreen().addPreference(fakeHeaderNotifications);
        addPreferencesFromResource(R.xml.pref_notifications);

        PreferenceCategory fakeHeaderHelp = new PreferenceCategory(getActivity());
        fakeHeaderHelp.setTitle(R.string.pref_header_help);
        getPreferenceScreen().addPreference(fakeHeaderHelp);
        addPreferencesFromResource(R.xml.pref_help);
    }

    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    private static boolean isSimplePreferences(Context context) {
        return ALWAYS_SIMPLE_PREFS
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    private void changeUserName(){

        buttonUserName  = (Preference)findPreference("name");
        buttonUserName.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                String name = ParseUser.getCurrentUser().getString("name");
                String placeholder = name;
                if (name == null){
                    name = "";
                    placeholder = getActivity().getString(R.string.preferences_user_name_placeholder);
                }
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.preferences_user_name_title_dialog)
                        .content(R.string.preferences_user_name_msg_dialog)
                        .backgroundColor(Color.WHITE)
                        .titleColorRes(R.color.colorPrimaryDialog)
                        .contentColorRes(R.color.windowBackgroundColor)
                        .positiveColorRes(R.color.colorPrimaryDialog)
                        .widgetColorRes(android.R.color.transparent)
                        .inputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                        .positiveText(R.string.submit)
                        .negativeText(R.string.cancel)
                        .inputMaxLength(15)
                        .negativeColorRes(R.color.colorButtonCancelLight)
                        .input(placeholder, name, false, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence newUserName) {
                                User u = (User) ParseUser.getCurrentUser();
                                u.setUserName(newUserName + "");
                                try {
                                    u.save();
                                    new SnackBar(getActivity(), getActivity().getString(R.string.preferences_user_name_changed), null, null).show();
                                } catch (ParseException e) {
                                    new SnackBar(getActivity(), getActivity().getString(R.string.unexpected_error), null, null).show();
                                }
                            }
                        }).show();
                return true;
            }
        });

    }

    private void changeAvatar(){

        buttonAvatar = (Preference)findPreference("avatar");
        buttonAvatar.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(getActivity(), AvatarActivity.class);
                startActivity(i);
                return false;
            }
        });

    }

    private void logOut(){
        buttonLogOut = (Preference)findPreference("logout");
        buttonLogOut.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new MaterialDialog.Builder(getActivity())
                        .content(R.string.logout_dialog_msg)
                        .backgroundColor(Color.WHITE)
                        .titleColorRes(R.color.colorPrimaryDialog)
                        .contentColorRes(R.color.colorPrimaryDialogText)
                        .positiveColorRes(R.color.colorWhite)
                        .negativeColorRes(R.color.colorButtonCancelLight)
                        .positiveText(R.string.logout)
                        .btnSelector(R.drawable.md_btn_selector_custom, DialogAction.POSITIVE)
                        .negativeText(R.string.cancel)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                ParseUser.getCurrentUser().logOut();
                                Intent i = new Intent(getActivity(), LoginActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }
                        }).show();
                return true;
            }
        });
    }

    private void deleteAccount(){
        buttonDeleteUser = (Preference)findPreference("delete_user");
        buttonDeleteUser.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.preferences_delete_user_title)
                        .content(R.string.preferences_delete_user_msg)
                        .backgroundColor(Color.WHITE)
                        .titleColorRes(R.color.colorPrimaryDialog)
                        .contentColorRes(R.color.colorPrimaryDialogText)
                        .positiveColorRes(R.color.colorWhite)
                        .negativeColorRes(R.color.colorButtonCancelLight)
                        .positiveText(R.string.i_am_sure)
                        .btnSelector(R.drawable.md_btn_selector_custom, DialogAction.POSITIVE)
                        .negativeText(R.string.cancel)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {

                                API.deleteCurrentUser();

                                Intent i = new Intent(getActivity(), LoginActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }
                        }).show();
                return true;
            }
        });
    }

    private void changeNotifications() {

        buttonNotifications = (CheckBoxPreference)findPreference("notifications");
        buttonNotifications.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                boolean check = ((CheckBoxPreference) preference).isChecked();
                Util.registerNotification(check);
                return false;
            }
        });
    }

    private void updateNotification(){
        buttonNotifications = (CheckBoxPreference)findPreference("notifications");
        ParseInstallation parse = ParseInstallation.getCurrentInstallation();
        parse.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    boolean isNotificationActived = parseObject.getBoolean("push");
                    buttonNotifications.setChecked(isNotificationActived);
                }
            }
        });

    }

    private void showContactMe(){

        buttonContact  = (Preference)findPreference("contact_me");
        buttonContact.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                new MaterialDialog.Builder(getActivity())
                        .title(R.string.preferences_contact_title)
                        .titleColorRes(R.color.colorPrimaryDark)
                        .content(R.string.preferences_contact_content)
                        .contentColorRes(R.color.windowBackgroundColor)
                        .backgroundColor(Color.WHITE)
                        .iconRes(R.mipmap.ic_launcher_red)
                        .limitIconToDefaultSize()
                        .items(R.array.social_networks)
                        .btnSelector(R.drawable.md_btn_selector_custom, DialogAction.POSITIVE)
                        .positiveText(R.string.ok)
                        .positiveColor(Color.WHITE)
                        .negativeText(R.string.exit)
                        .negativeColorRes(R.color.colorButtonCancelLight)
                        .itemsCallbackSingleChoice(3, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int position, CharSequence text) {

                                Intent intent = new Intent(Intent.ACTION_VIEW);

                                switch (position) {
                                    case 0:
                                        //Twitter
                                        try {
                                            // get the Twitter app if possible
                                            getActivity().getPackageManager().getPackageInfo("com.twitter.android", 0);
                                            intent.setData(Uri.parse("twitter://user?user_id=1056509035"));
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        } catch (Exception e) {
                                            // no Twitter app, revert to browser
                                            intent.setData(Uri.parse("https://twitter.com/alexpc_"));
                                        }
                                        startActivity(intent);
                                        break;
                                    case 1:
                                        //GitHub
                                        intent.setData(Uri.parse("https://github.com/ialex90"));
                                        startActivity(intent);
                                        break;
                                    case 2:
                                        //Linkedin
                                        try {
                                            // get the Linkedin app if possible
                                            getActivity().getPackageManager().getPackageInfo("com.linkedin.android", 0);
                                            intent.setData(Uri.parse("linkedin://profile/237647795"));
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        } catch (Exception e) {
                                            // no Linkedin app, revert to browser
                                            intent.setData(Uri.parse("http://www.linkedin.com/profile/view?id=237647795"));
                                        }
                                        startActivity(intent);
                                        break;
                                    case 3:
                                        //Email
                                        String uriText = "mailto:alejandrofpo@gmail.com" + "?subject=" +
                                                Uri.encode("From an User of DevHub App") + "&body=" + Uri.encode("I love your app but I would like to say you...");
                                        Uri uri = Uri.parse(uriText);
                                        Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                                        sendIntent.setData(uri);
                                        startActivity(Intent.createChooser(sendIntent, "Send me an email"));
                                        break;
                                    case 4:
                                        //Web
                                        intent.setData(Uri.parse("http://www.alejandrolora.es"));
                                        startActivity(intent);
                                        break;
                                }
                                return true;
                            }
                        })
                        .show();
                return true;
            }
        });
    }

    private void showAboutMe(){

        buttonAbout  = (Preference)findPreference("about_me");
        buttonAbout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                PackageInfo pInfo = null;
                try {
                    pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                String version = "Version - "+ pInfo.versionName;

                new MaterialDialog.Builder(getActivity())
                        .title(R.string.preferences_about_title)
                        .titleColorRes(R.color.colorPrimaryDark)
                        .content(version + getString(R.string.preferences_about_content))
                        .contentColorRes(R.color.windowBackgroundColor)
                        .backgroundColor(Color.WHITE)
                        .iconRes(R.mipmap.ic_launcher_red)
                        .limitIconToDefaultSize()
                        .show();
                return true;
            }
        });
    }

}
