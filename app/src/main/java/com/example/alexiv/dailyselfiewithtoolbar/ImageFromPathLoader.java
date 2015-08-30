package com.example.alexiv.dailyselfiewithtoolbar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Alex on 8/24/2015.
 */

/**
 * Task for loading images by path from storage to memory
 */
public class ImageFromPathLoader extends AsyncTask<MyListAdapter.MyBundleParams, Void, Bitmap> {
    ImageView imageView; // ImageView for changing
    ProgressBar prog; // ProgressBar that shown while image loads
    ArrayList<Bitmap> refToCache; // Reference to list of cached small images
    Integer pos; // Current image index
    String path; // Storage path to image

    protected Bitmap doInBackground(MyListAdapter.MyBundleParams... params) {
        // Unpack variables
        this.imageView = params[0].getImageView();
        this.prog = params[0].getProg();
        this.refToCache = params[0].getRefToCache();
        this.pos = params[0].getPos();
        this.path = params[0].getPath();

        // Setting decoding options
        BitmapFactory.Options myOpt = new BitmapFactory.Options();
        myOpt.inSampleSize = 16;

        // Decoding image
        return ImageRotationClass.rotateBitmap(BitmapFactory.decodeFile(path, myOpt), path);
    }


    protected void onPostExecute(Bitmap result) {
        refToCache.set(pos, result); // Save image to cache
        prog.setVisibility(View.GONE);
        imageView.setImageBitmap(result);
        imageView.setVisibility(View.VISIBLE);
    }
}
