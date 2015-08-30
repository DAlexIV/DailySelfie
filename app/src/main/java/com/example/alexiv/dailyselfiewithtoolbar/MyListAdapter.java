package com.example.alexiv.dailyselfiewithtoolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.datatype.Duration;

/**
 * Created by Alex on 8/24/2015.
 */

/**
 * Custom adapter for displaying images
 */
public class MyListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

    /**
     * Bundle to transmit data to AsyncTask thread
     */
    public class MyBundleParams {
        ImageView imageView; // ImageView for changing
        ProgressBar prog; // ProgressBar that shown while image loads
        ArrayList<Bitmap> refToCache; // Reference to list of cached small images
        Integer pos; // Current image index
        String path; // Storage path to image

        public MyBundleParams(ImageView imageView, ProgressBar prog, ArrayList<Bitmap> refToCache,
                              Integer pos, String path) {
            this.imageView = imageView;
            this.prog = prog;
            this.refToCache = refToCache;
            this.pos = pos;
            this.path = path;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public String getPath() {
            return path;
        }

        public ProgressBar getProg() {
            return prog;
        }

        public ArrayList<Bitmap> getRefToCache() {
            return refToCache;
        }

        public Integer getPos() {
            return pos;
        }

    }

    private Activity mActivity; // Parent activity
    private LayoutInflater mLayoutInflater;
    private List<KeyToImagePath> items; // Images
    private ArrayList<Bitmap> cachedImages; // Cached bitmaps

    public MyListAdapter(Activity activity, List<KeyToImagePath> items) {
        this.mActivity = activity;
        this.items = items;

        // Reverse array to display it in more usual way
        Collections.reverse(this.items);

        // Fill cached images with nulls
        cachedImages = new ArrayList<>();
        for (int i = 0; i < items.size(); ++i)
            cachedImages.add(null);

        mLayoutInflater =
                (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int location) {
        return items.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Adds new item to the beginning of listAdapter
     *
     * @param newElem newArrayAdapterElement
     */
    public void addItem(KeyToImagePath newElem) {
        items.add(0, newElem);
        cachedImages.add(0, null);
        notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // Getting view variables from layout
        convertView = mLayoutInflater.inflate(R.layout.list_row, null);
        ImageView pic = (ImageView) convertView.findViewById(R.id.row_image);
        ProgressBar pb = (ProgressBar) convertView.findViewById(R.id.prog);
        pic.setVisibility(View.INVISIBLE);
        TextView text = (TextView) convertView.findViewById(R.id.row_text);

        // If we haven't cached image yet
        if (cachedImages.get(position) == null) {
            // Packing params
            MyBundleParams params = new MyBundleParams(pic, pb,
                    cachedImages, position, items.get(position).path);
            ImageFromPathLoader myLoader = new ImageFromPathLoader();

            // Execute loading from storage task
            myLoader.execute(params);

        }
        // If we already cached image
        else {
            // Make it visible
            pb.setVisibility(View.GONE);
            pic.setVisibility(View.VISIBLE);

            // Restore from cache
            pic.setImageBitmap(cachedImages.get(position));
        }
        text.setText(items.get(position).key);
        return convertView;
    }

    /**
     * Starting new activity with fullsized image
     *
     * @param arg0
     * @param v
     * @param position
     * @param arg3
     */
    @Override
    public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
        Intent myInt = new Intent(mActivity, FullscreenActivity.class);
        myInt.putExtra("path", items.get(position).path);
        mActivity.startActivity(myInt);
    }
    protected void deleteItemAt(int pos)
    {
        if (pos < 0 && pos >= items.size())
            throw new IndexOutOfBoundsException("There is no element for deleting operation");
        cachedImages.remove(pos);
        deleteImageFromSD(pos);
        items.remove(pos);
        notifyDataSetChanged();
    }
    private void deleteImageFromSD(int pos)
    {
        File fileForDel;
        //try {
            fileForDel = new File(items.get(pos).path);
        //}
        /*
        catch (FileNotFoundException e)
        {
            Toast.makeText(mActivity, "Image not found", Toast.LENGTH_SHORT);
        }*/
        fileForDel.delete();
    }
}
