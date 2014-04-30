package com.example.lookingGlassCommon;

import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

/**
 * Created by evan on 4/29/14.
 */
public class WebBridge {
    public WebView wv;
    private PictureTaker picTaker;
    private ImageView imageView;

    private final Callback callback;

    private final Handler handler;

    private final UIExecutor uiExecutor;

    public static final String TAG = WebBridge.class.getName();

    public WebBridge(Activity activity, WebView wv, PictureTaker picTaker, ImageView imgView, Callback callback){
        this.wv = wv;

        handler = new Handler();
        uiExecutor = new UIExecutor(handler);

        this.wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage cm)
            {
                Log.d("ConsoleMessage1", String.format("%s @ %d: %s",
                        cm.message(), cm.lineNumber(), cm.sourceId()));
                return true;
            }
        });

        this.picTaker = picTaker;
        this.imageView = imgView;
        this.callback = callback;
        JavaScriptInterface jsInterface = new JavaScriptInterface(activity);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.addJavascriptInterface(jsInterface, "JSInterface");

    }

    public interface TakeImageCallback {
        public void takeImage(String base64Image);
    }

    public interface Callback {
        public void onPageLoad();
    }

    public class JavaScriptInterface {
        private Activity activity;

        public JavaScriptInterface(Activity activiy) {
            this.activity = activiy;
        }

        public void renderResponse(final String base64, long timestamp) {
            Log.d(TAG, "2Got timestamp: " + timestamp);

            handler.post(new Runnable() {
                @Override
                public void run() {
                    RenderPicture.render(imageView, base64);
                }
            });
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
            }, uiExecutor);
        }

        public String getDevice(){
            return (Build.MANUFACTURER + "_" + Build.PRODUCT).replaceAll("\\.", ",");
        }

        public void loaded() {
            callback.onPageLoad();
        }
    }

    public void sendImage(int callbackId, String base64image){
        wv.loadUrl("javascript:onGotImage(" + callbackId + ", '" + base64image + "')");
    }

    public void callWithDeviceName(String name){
        wv.loadUrl("javascript:callWithDeviceName('" + name + "')");
    }
}
