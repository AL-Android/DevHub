package es.alejandrolora.devhub.activitys;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.widgets.SnackBar;
import com.melnykov.fab.FloatingActionButton;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import es.alejandrolora.devhub.R;
import es.alejandrolora.devhub.Util.CircleTransform;
import es.alejandrolora.devhub.Util.Util;
import es.alejandrolora.devhub.models.User;

public class AvatarActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;

    private ParseImageView avatar;
    private final int REQUEST_PICK_IMAGE_GALLERY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        avatar = (ParseImageView) findViewById(R.id.imageViewAvatar);

        Util.showProgressBar(this, null);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setShadow(true);
        fab.setOnClickListener(this);

        loadImageAvatar();
        avatar.setOnClickListener(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_avatar;
    }

    private void loadImageAvatar() {

        User currentUser = (User) ParseUser.getCurrentUser();
        ParseFile file = currentUser.getPhoto();

        if (file != null) {
            if (file.getUrl() != null && !file.getUrl().isEmpty()) {
                Picasso.with(this).load(file.getUrl()).transform(new CircleTransform()).resize(600, 600).centerInside().into(avatar, new Callback() {
                    @Override
                    public void onSuccess() {
                        Util.dismissProgressBar();
                    }

                    @Override
                    public void onError() {
                        Util.dismissProgressBar();
                        new SnackBar(AvatarActivity.this, getString(R.string.image_avatar_error_picasso), null, null).show();
                    }
                });
            } else {
                Picasso.with(this).load(R.drawable.no).transform(new CircleTransform()).into(avatar);
                new SnackBar(this, getString(R.string.no_image_avatar), null, null).show();
                Util.dismissProgressBar();
            }
        } else {
            Picasso.with(this).load(R.drawable.no).transform(new CircleTransform()).into(avatar);
            new SnackBar(this, getString(R.string.no_image_avatar), null, null).show();
            Util.dismissProgressBar();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_PICK_IMAGE_GALLERY && resultCode == RESULT_OK) {

            Util.showProgressBar(this, null);

            // Capture photo like URI
            Uri photoUri = data.getData();
            // Load Photo to Screen
            Picasso.with(this).load(photoUri).transform(new CircleTransform()).into(avatar, new Callback() {
                @Override
                public void onSuccess() {
                    Util.dismissProgressBar();
                }

                @Override
                public void onError() {
                    Util.dismissProgressBar();
                    new SnackBar(AvatarActivity.this, getString(R.string.image_avatar_error_picasso), null, null).show();
                }
            });

            // Process to low quality photo and save the avatar in Parse.com in AsyncTask
            Util.lowQualityPhotoFromUriToParseFileBackground(this, photoUri, 25);
        }
    }

    @Override
    public void onClick(View v) {

        final Activity act = this;

        if(v.getId() == R.id.imageViewAvatar){
            Util.pickPhotoGallery(this, getString(R.string.pick_image_chooser_avatar), REQUEST_PICK_IMAGE_GALLERY);
        }else if(v.getId() == R.id.fab){
            new MaterialDialog.Builder(this)
                    .content(R.string.fab_delete_avatar_content)
                    .backgroundColor(Color.WHITE)
                    .contentColorRes(R.color.colorPrimaryDialogText)
                    .positiveColorRes(R.color.colorWhite)
                    .negativeColorRes(R.color.colorButtonCancelLight)
                    .positiveText(R.string.i_am_sure)
                    .btnSelector(R.drawable.md_btn_selector_custom, DialogAction.POSITIVE)
                    .negativeText(R.string.cancel)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            Picasso.with(act).load(R.drawable.no).transform(new CircleTransform()).into(avatar);
                            User user = (User) ParseUser.getCurrentUser();
                            user.remove("photo");
                            user.saveInBackground();
                        }
                    }).show();
        }
    }
}