package com.arunkr.postsharer.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.arunkr.postsharer.R;
import com.arunkr.postsharer.helpers.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = "LOGIN";
    FirebaseAuth firebaseAuth;
    EditText txtUsername,txtPassword;
    Button btnSignIn;
    TextView txtvSignUp;
    TextView txtvForgotPass;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();

        loginIfSignedIn();
        initControls();
    }

    private void loginIfSignedIn()
    {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!=null)
        {
            // User is signed in
            Intent intent = new Intent(this,ViewPostsActivity.class);
            Log.i(TAG, "Redirecting, User already Signed in"+user.getUid());

            finish();
            startActivity(intent);
        }
    }

    private void initControls()
    {
        txtUsername = (EditText)findViewById(R.id.login_txt_username);
        txtPassword = (EditText)findViewById(R.id.login_txt_password);
        txtvSignUp = (TextView) findViewById(R.id.login_txtv_sign_up);
        btnSignIn = (Button)findViewById(R.id.login_btn_sign_in);
        txtvForgotPass = (TextView)findViewById(R.id.login_txtv_forgot_pass);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(R.string.logging_in);

        btnSignIn.setOnClickListener(this);
        txtvSignUp.setOnClickListener(this);
        txtvForgotPass.setOnClickListener(this);
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.login_btn_sign_in:
                String username, password;

                username = txtUsername.getText().toString().trim();
                password = txtPassword.getText().toString().trim();

                txtUsername.setText("");
                txtPassword.setText("");


                if(validateEmailPassword(username,password))
                {
                    mProgressDialog.show();
                    firebaseAuth.signInWithEmailAndPassword(username,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)
                                {
                                    mProgressDialog.dismiss();
                                    if(!task.isSuccessful())
                                    {
                                        //show error
                                        Utils.showToast(LoginActivity.this,getString(R.string.login_failure));
                                    }
                                    else
                                    {
                                        loginIfSignedIn();
                                    }
                                }
                            });
                }
                else
                {
                    Snackbar.make(view, R.string.login_validation_error, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                break;

            case R.id.login_txtv_sign_up:
                finish();
                startActivity(new Intent(this,SignUp.class));
                break;
            case R.id.login_txtv_forgot_pass:
                Intent intent = new Intent(this,PasswordReset.class);
                startActivity(intent);
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mProgressDialog.dismiss();
    }

    private boolean validateEmailPassword(String email, String pass)
    {
        if(!email.isEmpty() && !pass.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            return true;
        }
        return false;
    }
}
