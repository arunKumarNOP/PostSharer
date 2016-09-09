package com.arunkr.postsharer.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.arunkr.postsharer.R;
import com.arunkr.postsharer.adapters.ViewPostListAdapter;
import com.arunkr.postsharer.helpers.Utils;
import com.arunkr.postsharer.model.Post;
import com.arunkr.postsharer.model.ViewPost;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ViewPostsActivity extends AppCompatActivity
{
    FirebaseAuth firebaseAuth;
    DatabaseReference postsRef;
    ListView postsListview;
    List<ViewPost> data;
    ViewPostListAdapter adapter;

    DatabaseReference mDatabaseRef;
    StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_posts);
        getSupportActionBar().setTitle(R.string.view_posts);

        firebaseAuth = FirebaseAuth.getInstance();
        checkIfLogedIn();
        initControls();
        initDatabaseRef();
        populateListview();
    }

    void initDatabaseRef()
    {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user==null)
        {
            finish();
        }
        String email_domain = Utils.domainFromEmail(user.getEmail());
        postsRef = FirebaseDatabase.getInstance().getReference()
                .child(Utils.POSTS).child(email_domain);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    void populateListview()
    {
        Toast.makeText(this,"Loading posts.",Toast.LENGTH_LONG).show();
        postsRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                data.clear();
                for (DataSnapshot child: dataSnapshot.getChildren())
                {
                    Post post = child.getValue(Post.class);

                    String uid = post.getPostedBy();
                    int post_type = post.getType();

                    final ViewPost viewPost = new ViewPost(post_type);

                    //sender profile pic
                    getProfilePic(uid,viewPost);

                    //sender name
                    getPosterName(uid,viewPost);

                    //Image_post
                    if(post_type == Utils.IMAGE_POST)
                    {
                        getPostImageUrl(post.getData(),viewPost);
                    }
                    else if (post_type==Utils.MSG_POST)
                    {
                        viewPost.setData(post.getData());
                    }
                    data.add(viewPost);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    void getPostImageUrl(String hash, final ViewPost viewPost)
    {
        StorageReference photoRef = mStorageRef.child(Utils.POSTED_IMAGES).child(hash + ".png");
        photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri uri)
            {
                viewPost.setData(uri.toString());
                adapter.notifyDataSetChanged();
            }
        });
    }

    void getPosterName(String uid, final ViewPost viewPost)
    {
        mDatabaseRef.child(Utils.USERS_TABLE).child(uid).child(Utils.USERS_TABLE_DISPLAY_NAME)
                .addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        viewPost.setPostedBy(dataSnapshot.getValue().toString());
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {}
                });
    }

    void getProfilePic(String uid, final ViewPost viewPost)
    {
        StorageReference profilePhotoRef = mStorageRef.child(Utils.PROFILE_PIC_DIR).child(uid+".png");
        profilePhotoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri uri)
            {
                viewPost.setProfile_pic(uri.toString());
                adapter.notifyDataSetChanged();
            }
        });
    }

    void checkIfLogedIn()
    {
        if(firebaseAuth.getCurrentUser()==null)
        {
            goToLoginActivity();
        }
    }

    void goToLoginActivity()
    {
        Intent intent = new Intent(this,LoginActivity.class);
        finish();
        startActivity(intent);
    }

    private void initControls()
    {
        postsListview = (ListView)findViewById(R.id.messages_listview);
        data = new ArrayList<>();
        adapter = new ViewPostListAdapter(ViewPostsActivity.this,
                R.layout.messages_list_item, data);

        postsListview.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.view_post_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.action_view_profile:
                Intent intent = new Intent(this,ProfilePageActivity.class);
                startActivity(intent);
                break;
            case R.id.action_logout:
                logout();
                break;
            case R.id.make_new_post:
                Intent intent2 = new Intent(ViewPostsActivity.this,MakePostActivity.class);
                startActivity(intent2);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout()
    {
        firebaseAuth.signOut();
        checkIfLogedIn();
    }
}
