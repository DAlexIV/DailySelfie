package com.example.alexiv.dailyselfiewithtoolbar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.alexiv.dailyselfiewithtoolbar.util.FireNotification;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Ivanov Alex. From Russia with love.
 */

public class MainActivity extends AppCompatActivity {


    static final int REQUEST_TAKE_PHOTO = 1;
    static final int ALARM_TIME = 24 * 60 * 60 * 1000;
    String mCurrentPhotoPath; // Path to new image
    String mCurrentPhotoKey; // New image key
    ArrayList<KeyToImagePath> myKeys = new ArrayList<>(); // Image info stores there
    MyListAdapter mListAdapter; // Adapter for listview

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        restorePreferences();

        //Initialize listview
        mListAdapter = new MyListAdapter(this, myKeys);
        final ListView lvMain = (ListView) findViewById(R.id.main_list_view);
        lvMain.setAdapter(mListAdapter);
        lvMain.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mListAdapter.onItemClick(parent, view, position, id);
                    }
                });

        setupAlarm();
        lvMain.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        String keyForFind =((KeyToImagePath) lvMain.getItemAtPosition(position)).key;
                        for (int i = 0; i < myKeys.size(); ++i)
                            if (myKeys.get(i).key.equals(keyForFind))
                                myKeys.remove(i);

                        ((MyListAdapter) lvMain.getAdapter()).deleteItemAt(position);
                        return true;
                    }
                }
        );
    }

    @Override
    public void onPause() {
        super.onPause();
        savePreferences();
    }

    @Override
    public void onResume() {
        super.onResume();
        restorePreferences();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Launch camera
        if (id == R.id.action_cam) {
            dispatchTakePictureIntent();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Create unique key for image and path to store it
     *
     * @return path
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("dd.MM.yyyy_HH:mm:ss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        mCurrentPhotoKey = timeStamp;
        return image;
    }

    /**
     * Launches camera event
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d("MYSUPERTAG", "IOEXCEPTION happened in building filename");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));

                // Save prefs before exiting activity
                savePreferences();
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * Retrieving image from cam
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            restorePreferences();

            // Adding it to adapter and sharedprefs
            KeyToImagePath curItem = new KeyToImagePath(mCurrentPhotoKey, mCurrentPhotoPath);
            myKeys.add(curItem);
            mListAdapter.addItem(curItem);

            // Save them
            savePreferences();
        }
    }

    private void savePreferences() {
        // We need an Editor object to make preference changes.
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putString("mCurrentPhotoPath", mCurrentPhotoPath);
        editor.putString("mCurrentPhotoKey", mCurrentPhotoKey);
        //Set the values
        Gson mGson = new Gson();
        editor.putString("myKeys", mGson.toJson(myKeys));
        // Commit the edits!
        editor.commit();
    }

    private void restorePreferences() {
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        mCurrentPhotoPath = settings.getString("mCurrentPhotoPath", "");
        mCurrentPhotoKey = settings.getString("mCurrentPhotoKey", "");
        Type collectionType = new TypeToken<ArrayList<KeyToImagePath>>() {
        }.getType();
        myKeys = (new Gson()).fromJson(settings.getString("myKeys", ""), collectionType);
        if (myKeys == null)
            myKeys = new ArrayList<>();
    }

    /**
     * Fires alarm
     */
    public void setupAlarm() {
        registerReceiver(new FireNotification(), new IntentFilter("ALARM_ACTION"));
        Intent intent = new Intent(MainActivity.this, FireNotification.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        mCalendar.set(Calendar.HOUR_OF_DAY, 21);
        mCalendar.set(Calendar.MINUTE, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                mCalendar.getTimeInMillis(),
                ALARM_TIME,
                pendingIntent);

    }
}
