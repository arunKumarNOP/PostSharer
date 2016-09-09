package com.arunkr.postsharer.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.arunkr.postsharer.R;
import com.arunkr.postsharer.helpers.ImageOperations;
import com.arunkr.postsharer.helpers.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilePageActivity extends AppCompatActivity
{
    FirebaseAuth firebaseAuth;

    CircleImageView profile_pic;
    EditText username;
    TextView email_id;

    private StorageReference mStorageRef;
    DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        firebaseAuth = FirebaseAuth.getInstance();

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.view_profile);

        initControls();
        setControlValues();
    }

    private void initControls()
    {
        username = (EditText)findViewById(R.id.profile_txt_username);
        profile_pic = (CircleImageView)findViewById(R.id.profile_img_dp);
        email_id = (TextView)findViewById(R.id.profile_txt_email_id);

        profile_pic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select picture"),Utils.GET_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode ==Utils.GET_IMAGE && resultCode==RESULT_OK)
        {
            ImageOperations.performCrop(ProfilePageActivity.this,data.getData(),true);
        }
        else if(requestCode==Utils.PIC_CROP && resultCode == RESULT_OK)
        {
            if (data != null)
            {

                Bundle extras = data.getExtras();
                Bitmap selectedBitmap = extras.getParcelable("data");

                profile_pic.setImageBitmap(selectedBitmap);

                final StorageReference photoRef = mStorageRef.child(Utils.PROFILE_PIC_DIR)
                        .child(firebaseAuth.getCurrentUser().getUid()+".png");


                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                selectedBitmap.compress(Bitmap.CompressFormat.PNG, 75, bos);
                photoRef.putBytes(bos.toByteArray());
            }
        }
    }

    private void setControlValues()
    {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user!=null)
        {
            Utils.setDisplayName(mDatabaseRef,user.getUid(),username);
            email_id.setText(user.getEmail());
            Utils.loadProfileImage(this,mStorageRef,user.getUid(),profile_pic);
        }
        else
        {
            finish();
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed()
    {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        final String userId = currentUser.getUid();

        String displayName = username.getText().toString().trim();

        mDatabaseRef.child(Utils.USERS_TABLE).child(userId)
                .child(Utils.USERS_TABLE_DISPLAY_NAME).setValue(displayName);

        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
