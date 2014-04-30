package com.example.LookingGlassPhone;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.SurfaceView;
import android.webkit.WebView;
import com.example.lookingGlassCommon.PictureTaker;
import com.example.lookingGlassCommon.TestLibProject;
import com.example.lookingGlassCommon.WebBridge;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

public class MainActivity extends Activity {
    public final String TAG = getClass().getName();

    private PictureTaker mPictureTaker;

    private PowerManager.WakeLock mWakeLock;

    private WebView mWebView;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TestLibProject.test();

        PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);
        mWakeLock.acquire();

        mWebView = (WebView)this.findViewById(R.id.webview);
        mWebView.loadUrl("file:///android_asset/index.html");
        WebBridge webBridge = new WebBridge(this, mWebView, new WebBridge.ReceiveImageCallback() {
            @Override
            public void onReceiveImage(String base64Image) {
                System.out.println("got image "  + base64Image);
            }
        });

        mPictureTaker = new PictureTaker();

        final Handler handler = new Handler();

        Futures.addCallback(mPictureTaker.snapBase64((SurfaceView) findViewById(R.id.surface)), new FutureCallback<String>() {
            @Override
            public void onSuccess(String base64) {
                Log.d(TAG, "Base64 image got, " + base64.length());
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d(TAG, "Failed with " + throwable.toString());
            }
        });
    }

    @Override
    public void onPause() {
        mWebView.clearHistory();
        mWebView.loadUrl("about:blank");
        mWebView.freeMemory();
        mWebView.pauseTimers();
        mWebView = null;
        mPictureTaker.release();
        finish();
        mWakeLock.release();
        super.onPause();
    }
}
