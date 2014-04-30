package com.example.lookingGlassCommon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
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

        this.wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage cm)
            {
                Log.d("ConsoleMessage", String.format("%s @ %d: %s",
                        cm.message(), cm.lineNumber(), cm.sourceId()));
                return true;
            }
        });

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
        public String getDevice(){
            return (Build.MANUFACTURER + "_" + Build.PRODUCT).replaceAll("\\.", ",");
        }
    }

    public void sendImage(String base64image){
        wv.loadUrl("javascript:getImage('" + base64image + "')");
    }
}
