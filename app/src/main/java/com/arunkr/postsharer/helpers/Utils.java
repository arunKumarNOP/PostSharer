package com.arunkr.postsharer.helpers;

import android.content.Context;
import android.net.Uri;
import android.widget.EditText;
import android.widget.Toast;

import com.arunkr.postsharer.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by arunkr on 1/9/16.
 */
public class Utils
{
    public static final int MSG_POST =1;
    public static final int IMAGE_POST =2;


    public static final int GET_IMAGE = 1;
    public static final int PIC_CROP = 2;
    public static final String PROFILE_PIC_DIR = "profilePic";
    public static final String USERS_TABLE = "users";
    public static final String POSTED_IMAGES = "postImages";
    public static final String POSTS = "posts";

    public static final String USERS_TABLE_DISPLAY_NAME = "displayName";

    public static void showToast(Context context, String message)
    {
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }

    public static String domainFromEmail(String email)
    {
        String domain = email.substring(email.indexOf('@')+1);
        return domain.substring(0,domain.lastIndexOf('.'));
    }

    public static void loadProfileImage(final Context context, StorageReference mStorageRef,
                                           String uid, final CircleImageView iv)
    {
        StorageReference photoRef = mStorageRef.child(PROFILE_PIC_DIR).child(uid+".png");
        photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri uri)
            {
                Picasso.with(context)
                        .load(uri)
                        .placeholder(R.drawable.default_dp)
                        .error(R.drawable.default_dp).into(iv);
            }
        });
    }

    public static void setDisplayName(DatabaseReference mRef, String uid, final EditText displayName)
    {
        mRef.child(USERS_TABLE).child(uid).child(USERS_TABLE_DISPLAY_NAME)
                .addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                displayName.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    public static String hash(byte[] hashThis)
    {
        try
        {
            byte[] hash;
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            hash = md.digest(hashThis);
            return convertToHex(hash);
        }
        catch (NoSuchAlgorithmException nsae)
        {
        }
        return null;
    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data)
        {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do
            {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }
}
