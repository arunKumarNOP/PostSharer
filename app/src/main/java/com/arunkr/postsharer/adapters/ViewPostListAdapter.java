package com.arunkr.postsharer.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.arunkr.postsharer.R;
import com.arunkr.postsharer.helpers.Utils;
import com.arunkr.postsharer.model.ViewPost;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by arunkr on 2/9/16.
 */
public class ViewPostListAdapter extends ArrayAdapter<ViewPost>
{
    private Context context;
    private int layoutResourceId;
    DatabaseReference mDatabaseRef;
    StorageReference mStorageRef;


    public ViewPostListAdapter(Context context, int layoutResourceId, List<ViewPost> data)
    {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public ViewPost getItem(int position)
    {
        return super.getItem(getCount() - position - 1);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View itemView = convertView;
        if(itemView == null)
        {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            itemView = inflater.inflate(layoutResourceId, parent, false);
        }

        CircleImageView dp = (CircleImageView)itemView.findViewById(R.id.msg_item_img_profile_pic);
        ImageView postImage = (ImageView)itemView.findViewById(R.id.msg_item_img_message);
        TextView postText = (TextView)itemView.findViewById(R.id.msg_item_txt_message);
        TextView sender = (TextView)itemView.findViewById(R.id.msg_item_txt_sender_name);


        ViewPost currentPost = getItem(position);

        //set profile image of sender
        setProfilePic(currentPost.getProfile_pic(),dp);

        //set name of the sender
        sender.setText(currentPost.getPostedBy());

        if(currentPost.getType()== Utils.IMAGE_POST)
        {
            postText.setVisibility(View.GONE);
            postImage.setVisibility(View.VISIBLE);

            setPostPic(currentPost.getData(),postImage);
        }
        else if(currentPost.getType() == Utils.MSG_POST)
        {
            postImage.setVisibility(View.GONE);
            postText.setVisibility(View.VISIBLE);

            postText.setText(currentPost.getData());
        }

        return itemView;
    }

    void setProfilePic(String uri, CircleImageView iv)
    {
        Picasso.with(context)
                .load(uri)
                .placeholder(R.drawable.default_dp)
                .error(R.drawable.default_dp).into(iv);
    }

    void setPostPic(String uri, ImageView iv)
    {
        Picasso.with(context)
                .load(uri)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error).into(iv);
    }
}
