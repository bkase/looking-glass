package com.example.lookingGlassCommon;

import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by bkase on 4/29/14.
 */
public class RenderPicture {
    public static final String TAG = RenderPicture.class.getName();

    public static void render(ImageView view, String base64) {
        Log.d(TAG, "Base64 image got");
        byte[] imageAsBytes = Base64.decode(base64, Base64.NO_WRAP);
        Log.d(TAG, "got bytes");
        view.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        Log.d(TAG, "setImage");
    }
}
