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
import com.arunkr.postsharer.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = "SIGNUP";
    FirebaseAuth firebaseAuth;
    EditText txtUsername,txtPassword;
    Button btnSignUp;
    TextView txtvSignUp;
    ProgressDialog mProgressDialog;

    DatabaseReference mDatabaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

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
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        txtUsername = (EditText)findViewById(R.id.signup_txt_username);
        txtPassword = (EditText)findViewById(R.id.signup_txt_password);
        txtvSignUp = (TextView) findViewById(R.id.signup_txtv_sign_in);
        btnSignUp = (Button)findViewById(R.id.signup_btn_sign_up);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(R.string.signing_up);
        btnSignUp.setOnClickListener(this);
        txtvSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.signup_btn_sign_up:
                String username, password;

                username = txtUsername.getText().toString().trim();
                password = txtPassword.getText().toString().trim();

                txtUsername.setText("");
                txtPassword.setText("");

                if(validateEmailPassword(username,password))
                {
                    mProgressDialog.show();
                    firebaseAuth.createUserWithEmailAndPassword(username,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                            {

                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)
                                {
                                    mProgressDialog.dismiss();
                                    if(task.isSuccessful())
                                    {
                                        setDefaultProfileData();
                                        Utils.showToast(SignUp.this,getString(R.string.signup_sucess));
                                        loginIfSignedIn();
                                    }
                                    else
                                    {
                                        Utils.showToast(SignUp.this,getString(R.string.signup_failure));
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

            case R.id.signup_txtv_sign_in:
                finish();
                startActivity(new Intent(this,LoginActivity.class));
                break;
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mProgressDialog.dismiss();
    }



    private void setDefaultProfileData()
    {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        final String userId = currentUser.getUid();

        User userInfo = new User(getString(R.string.default_username));

        mDatabaseRef.child(Utils.USERS_TABLE).child(userId).setValue(userInfo);
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
