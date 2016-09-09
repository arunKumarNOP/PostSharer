package com.arunkr.postsharer.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.arunkr.postsharer.R;
import com.arunkr.postsharer.helpers.ImageOperations;
import com.arunkr.postsharer.helpers.Utils;
import com.arunkr.postsharer.model.Post;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class MakePostActivity extends AppCompatActivity implements View.OnClickListener
{
    FirebaseAuth firebaseAuth;
    DatabaseReference mPostsRef;
    StorageReference mPostImgRef;

    Button btnPostImage;
    ImageView ivPostImage;
    EditText txtPostText;
    Button btnPostText;

    Bitmap selectedBitmap;
    Bitmap scaledBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_post);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.make_post);

        initFirebaseObjects();
        initControls();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.make_post_btn_img:
                if(selectedBitmap!=null)
                {
                    btnPostImage.setEnabled(false);
                    Toast.makeText(this,"Uploading Image..",Toast.LENGTH_LONG).show();
                    uploadImage();
                }
                break;

            case R.id.make_post_btn_msg:
                String msg = txtPostText.getText().toString().trim();

                if(msg.length()>0)
                {
                    addPost(Utils.MSG_POST,msg);
                }
                break;

            case R.id.make_post_img:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select picture"),Utils.GET_IMAGE);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode ==Utils.GET_IMAGE && resultCode==RESULT_OK)
        {

            ImageOperations.performCrop(MakePostActivity.this,data.getData(),false);
        }
        else if(requestCode==Utils.PIC_CROP && resultCode == RESULT_OK)
        {
            if (data != null)
            {
                Bundle extras = data.getExtras();
                selectedBitmap = extras.getParcelable("data");
                int len = selectedBitmap.getByteCount();
                float ratio = ImageOperations.MAX_IMAGE_SIZE / len;
                if (ratio < 1.0)
                {
                    scaledBitmap = ImageOperations.scaleDown(selectedBitmap, ratio, true);
                }
                else
                {
                    scaledBitmap = selectedBitmap;
                }

                if (scaledBitmap != selectedBitmap)
                {
                    selectedBitmap.recycle();
                }

                ivPostImage.setImageBitmap(scaledBitmap);
            }
        }
    }

    void initControls()
    {
        btnPostImage = (Button)findViewById(R.id.make_post_btn_img);
        btnPostText = (Button)findViewById(R.id.make_post_btn_msg);
        ivPostImage = (ImageView)findViewById(R.id.make_post_img);
        txtPostText = (EditText)findViewById(R.id.make_post_txt_msg);

        btnPostImage.setOnClickListener(this);
        btnPostText.setOnClickListener(this);
        ivPostImage.setOnClickListener(this);
    }

    void initFirebaseObjects()
    {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user==null)
        {
            //redirect to login activity
        }
        mPostsRef = FirebaseDatabase.getInstance().getReference().
                child(Utils.POSTS).child(Utils.domainFromEmail(user.getEmail()));
        mPostImgRef = FirebaseStorage.getInstance().getReference().child(Utils.POSTED_IMAGES);
    }

    void uploadImage()
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.PNG, 75, bos);

        byte[] bytes = bos.toByteArray();

        final String hash = Utils.hash(bytes);
        final StorageReference photoRef = mPostImgRef.child(hash+".png");
        photoRef.putBytes(bytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                addPost(Utils.IMAGE_POST,hash);
            }
        });
    }

    void addPost(int type, String data)
    {
        String uid = firebaseAuth.getCurrentUser().getUid();
        Post post = new Post(uid,type,data);
        mPostsRef.push().setValue(post);
        finish();
    }
}
