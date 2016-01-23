package es.alejandrolora.devhub.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.SnackBar;
import com.rengwuxian.materialedittext.MaterialEditText;

import es.alejandrolora.devhub.Api.API;
import es.alejandrolora.devhub.R;
import es.alejandrolora.devhub.Util.Util;

public class SignUpActivity extends Activity implements View.OnClickListener {

    public static Activity mContextSignUp;

    private MaterialEditText inputEmail;
    private MaterialEditText inputConfirmPass;
    private MaterialEditText inputPass;
    private Button btnSignUp;
    private Button btnHaveAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mContextSignUp = SignUpActivity.this;

        inputEmail = (MaterialEditText) findViewById(R.id.editTextEmailSignUp);
        inputPass = (MaterialEditText) findViewById(R.id.editTextPasswordSignUp);
        inputConfirmPass = (MaterialEditText) findViewById(R.id.editTextConfirmPasswordSignUp);
        btnSignUp = (Button) findViewById(R.id.buttonSignUpSignUp);
        btnHaveAccount = (Button) findViewById(R.id.buttonHaveAccountSignUp);

        btnSignUp.setOnClickListener(this);
        btnHaveAccount.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.buttonSignUpSignUp:
                CheckDataToSignUp();
                break;
            case R.id.buttonHaveAccountSignUp:
                Intent i = new Intent(this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                break;
        }
    }

    private void CheckDataToSignUp() {
        String email = inputEmail.getText().toString().trim().toLowerCase();
        String pass = inputPass.getText().toString().trim();
        String passConfirm = inputConfirmPass.getText().toString().trim().toLowerCase();


        if (email.isEmpty() || pass.isEmpty() || passConfirm.isEmpty()) {
            new SnackBar(this, getString(R.string.login_signup_error_inputs_empty), null, null).show();
        }else if(!Util.isValidEmail(email)) {
            new SnackBar(this, getString(R.string.login_signup_error_email_pattern), null, null).show();
        }else if(!pass.equals(passConfirm)) {
            new SnackBar(this, getString(R.string.signup_error_different_passwords), null, null).show();
            inputPass.setText("");
            inputConfirmPass.setText("");
        }else if (pass.length() < 4) {
            new SnackBar(this, getString(R.string.login_error_min_length), null, null).show();
        } else if (pass.length() > 10) {
            new SnackBar(this, getString(R.string.login_error_max_length), null, null).show();
        } else {
            API.signUpNewUser(email, pass);
        }
    }

    public static void SignUpOk(){
        Intent intent = new Intent(mContextSignUp, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("validate", false);
        mContextSignUp.startActivity(intent);
        // Data extra to save
        Util.registerNotification(true);
    }

    public static void EmailIsRegistered(){
        new SnackBar(mContextSignUp, mContextSignUp.getString(R.string.signup_error_email_registered_yet), null, null).show();
    }

    public static void SignUpError(){
        new SnackBar(mContextSignUp, mContextSignUp.getString(R.string.signup_error), null, null).show();

    }
}
