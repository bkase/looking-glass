package com.example.lookingGlassCommon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.webkit.WebView;

import java.io.ByteArrayOutputStream;

/**
 * Created by evan on 4/29/14.
 */
public class WebBridge {
    public WebView wv;
    public ReceiveImageCallback receiveImageCallback;

    public WebBridge(Activity activity, WebView wv, ReceiveImageCallback receiveImageCallback){
        this.wv = wv;
        this.receiveImageCallback = receiveImageCallback;
        JavaScriptInterface jsInterface = new JavaScriptInterface(activity);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.addJavascriptInterface(jsInterface, "JSInterface");

    }

    public interface ReceiveImageCallback {
        public void onReceiveImage(String base64Image);
    }

    public class JavaScriptInterface {
        private Activity activity;

        public JavaScriptInterface(Activity activiy) {
            this.activity = activiy;
        }

        public void onReceiveImage(String base64Image){
            receiveImageCallback.onReceiveImage(base64Image);
        }
    }

    public void sendImage(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
        String encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        wv.loadUrl("javascript:getImage('" + encodedImage + "')");
    }
}
