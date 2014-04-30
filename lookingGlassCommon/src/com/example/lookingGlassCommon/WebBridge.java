package com.example.lookingGlassCommon;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

/**
 * Created by evan on 4/29/14.
 */
public class WebBridge {
    public WebView wv;
    private PictureTaker picTaker;

    public WebBridge(Activity activity, WebView wv, PictureTaker picTaker){
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

        this.picTaker = picTaker;
        JavaScriptInterface jsInterface = new JavaScriptInterface(activity);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.addJavascriptInterface(jsInterface, "JSInterface");

    }

    public interface TakeImageCallback {
        public void takeImage(String base64Image);
    }

    public class JavaScriptInterface {
        private Activity activity;

        public JavaScriptInterface(Activity activiy) {
            this.activity = activiy;
        }

        public void takePhoto(final int callbackId){
            Futures.addCallback(picTaker.snapBase64(), new FutureCallback<String>() {
                @Override
                public void onSuccess(String s) {
                    sendImage(callbackId, s);
                }

                @Override
                public void onFailure(Throwable throwable) {
                    System.out.println(throwable.getMessage());
                }
            });
        }

        public String getDevice(){
            return (Build.MANUFACTURER + "_" + Build.PRODUCT).replaceAll("\\.", ",");
        }
    }

    public void sendImage(int callbackId, String base64image){
        wv.loadUrl("javascript:onGotImage(" + callbackId + ", '" + base64image + "')");
    }
}
