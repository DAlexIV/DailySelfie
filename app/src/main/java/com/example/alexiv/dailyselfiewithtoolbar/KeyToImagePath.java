package com.example.alexiv.dailyselfiewithtoolbar;

import java.io.Serializable;

/**
 * Created by Alex on 8/24/2015.
 */

/**
 * Structure for storing and maintaining images
 */
public class KeyToImagePath implements Serializable {
    String key; // Date and timestamp of image
    String path; // Path to image

    public KeyToImagePath(String mCurrentPhotoKey, String mCurrentPhotoPath) {
        this.key = mCurrentPhotoKey;
        this.path = mCurrentPhotoPath;
    }
}
