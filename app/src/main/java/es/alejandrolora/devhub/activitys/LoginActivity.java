package es.alejandrolora.devhub.activitys;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.SnackBar;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.rengwuxian.materialedittext.MaterialEditText;

import es.alejandrolora.devhub.Api.API;
import es.alejandrolora.devhub.R;
import es.alejandrolora.devhub.Util.Util;

public class LoginActivity extends Activity implements View.OnClickListener {
    public static Activity mContextLogin;

    private Button btnLogin;
    private MaterialEditText inputEmail;
    private MaterialEditText inputPass;
    private Button btnSignUp;
    private Button btnForget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContextLogin = LoginActivity.this;

        inputEmail = (MaterialEditText) findViewById(R.id.editTextEmaiLogin);
        inputPass = (MaterialEditText) findViewById(R.id.editTextPasswordLogin);
        btnLogin = (Button) findViewById(R.id.buttonLogin);
        btnSignUp = (Button) findViewById(R.id.buttonSignUpLogin);
        btnForget = (Button) findViewById(R.id.buttonForget);

        btnLogin.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        btnForget.setOnClickListener(this);


        if (getIntent() != null) {
            if (getIntent().hasExtra("validate")) {
                new SnackBar(this, getString(R.string.login_no_validate_email_yet), null, null).show();
            }
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.buttonLogin:
                CheckDataToLogin();
                break;
            case R.id.buttonSignUpLogin:
                Intent i = new Intent(this, SignUpActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                break;
            case R.id.buttonForget:
                new MaterialDialog.Builder(this)
                        .title(R.string.login_forget_password_dialog_title)
                        .content(R.string.login_forget_password_dialog_msg)
                        .backgroundColor(Color.WHITE)
                        .titleColorRes(R.color.colorPrimaryDialog)
                        .contentColorRes(R.color.colorPrimaryDialogText)
                        .positiveColorRes(R.color.colorPrimaryDialog)
                        .widgetColorRes(android.R.color.transparent)
                        .inputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                        .positiveText(R.string.submit)
                        .input(R.string.login_forget_password_dialog_hint, 0, false, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence email) {
                                if (Util.isValidEmail(email)) {
                                    sendEmailToResetPassword(email + "");
                                } else {
                                    new SnackBar(mContextLogin, getString(R.string.login_signup_error_email_pattern), null, null).show();
                                }
                                dialog.dismiss();
                            }
                        }).show();
                break;
        }
    }


    private void CheckDataToLogin() {
        String email = inputEmail.getText().toString().trim().toLowerCase();
        String pass = inputPass.getText().toString();
        if (email.isEmpty() || pass.isEmpty()) {
            new SnackBar(this, getString(R.string.login_signup_error_inputs_empty), null, null).show();
        } else if (!Util.isValidEmail(email)) {
            new SnackBar(this, getString(R.string.login_signup_error_email_pattern), null, null).show();
        } else if (pass.length() < 4) {
            new SnackBar(this, getString(R.string.login_error_min_length), null, null).show();
        } else if (pass.length() > 10) {
            new SnackBar(this, getString(R.string.login_error_max_length), null, null).show();
        } else {
            API.checkLogin(email, pass);
        }
    }

    private void sendEmailToResetPassword(String email) {
        ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    new SnackBar(mContextLogin, mContextLogin.getString(R.string.login_reset_password_send), null, null).show();
                } else if (e.getCode() == 205) {
                    new SnackBar(mContextLogin, mContextLogin.getString(R.string.login_forget_password_email_not_exist), null, null).show();
                } else {
                    new SnackBar(mContextLogin, mContextLogin.getString(R.string.unexpected_error), null, null).show();
                }
            }
        });
    }

    public static void LoginOk() {
        Intent i = new Intent(mContextLogin, CategoriesActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContextLogin.startActivity(i);
    }

    public static void checkLoginCancel() {
        new SnackBar(mContextLogin, mContextLogin.getString(R.string.login_error_incorrect), null, null).show();
    }

    public static void notNetworkAvailable() {
        new SnackBar(mContextLogin, mContextLogin.getString(R.string.login_error_network_not_available), null, null).show();
    }

    public static void checkLoginEmailNoVerified() {
        new SnackBar(mContextLogin, mContextLogin.getString(R.string.login_error_email_no_verified), null, null).show();
    }
}