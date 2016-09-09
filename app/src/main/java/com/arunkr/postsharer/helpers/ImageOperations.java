package com.arunkr.postsharer.helpers;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.Toast;

/**
 * Created by arunkr on 2/9/16.
 */
public class ImageOperations
{
    public static final float MAX_IMAGE_SIZE = 2.0f*1000*1000;

    public static void performCrop(Context context, Uri picUri, boolean forProfile)
    {
        try
        {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            if(forProfile)
            {
                // indicate aspect of desired crop
                cropIntent.putExtra("aspectX", 1);
                cropIntent.putExtra("aspectY", 1);
                // indicate output X and Y
                cropIntent.putExtra("outputX", 128);
                cropIntent.putExtra("outputY", 128);
            }
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            ((Activity)context).startActivityForResult(cropIntent, Utils.PIC_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe)
        {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public static Bitmap scaleDown(Bitmap realImage, float ratio, boolean filter)
    {
        int width = Math.round(ratio * realImage.getWidth());
        int height = Math.round(ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

}
