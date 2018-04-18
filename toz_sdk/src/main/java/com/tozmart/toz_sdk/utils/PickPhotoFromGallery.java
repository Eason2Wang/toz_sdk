package com.tozmart.toz_sdk.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.tozmart.toz_sdk.R;

/**
 * Created by wys on 17/5/17.
 */

public class PickPhotoFromGallery {
    public static final int REQUEST_PICK = 9162;
    /**
     * Pick image from an Activity
     *
     * @param activity Activity to receive result
     */
    public static void pickImage(Activity activity) {
        pickImage(activity, REQUEST_PICK);
    }

    /**
     * Pick image from an Activity with a custom request code
     *
     * @param activity    Activity to receive result
     * @param requestCode requestCode for result
     */
    public static void pickImage(Activity activity, int requestCode) {
        try {
            activity.startActivityForResult(getImagePicker(), requestCode);
        } catch (ActivityNotFoundException e) {
            showImagePickerError(activity);
        }
    }

    private static Intent getImagePicker() {
        return new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
    }

    private static void showImagePickerError(Context context) {
        Toast.makeText(context.getApplicationContext(), R.string.pick_error, Toast.LENGTH_SHORT).show();
    }
}
