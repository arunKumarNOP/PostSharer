package com.arunkr.postsharer.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.arunkr.postsharer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordReset extends AppCompatActivity implements View.OnClickListener
{
    FirebaseAuth firebaseAuth;
    EditText txtUsername;
    Button btnReset;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.view_reset_pass);

        firebaseAuth = FirebaseAuth.getInstance();
        initControls();
    }

    private void initControls()
    {
        txtUsername = (EditText)findViewById(R.id.pass_reset_txt_email);
        btnReset = (Button)findViewById(R.id.pass_reset_btn_reset);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(R.string.reseting_pass);
        btnReset.setOnClickListener(this);
    }

    @Override
    public void onClick(final View view)
    {
        switch (view.getId())
        {
            case R.id.pass_reset_btn_reset:
                String email = txtUsername.getText().toString().trim();
                if(email!="" && Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    mProgressDialog.show();
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            mProgressDialog.dismiss();
                            if(task.isSuccessful())
                            {
                                Snackbar.make(view,R.string.reset_email_send,Snackbar.LENGTH_LONG)
                                        .setAction("Action",null).show();
                            }
                            else
                            {
                                Snackbar.make(view,R.string.invalid_email,Snackbar.LENGTH_LONG)
                                        .setAction("Action",null).show();
                            }
                        }
                    });
                }
                else
                {
                    Snackbar.make(view, R.string.invalid_email, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
        }
    }
}
