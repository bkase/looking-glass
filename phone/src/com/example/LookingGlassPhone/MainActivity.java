package com.example.LookingGlassPhone;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.webkit.WebView;
import com.example.lookingGlassCommon.TakePicture;
import com.example.lookingGlassCommon.TestLibProject;
import com.example.lookingGlassCommon.WebBridge;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

public class MainActivity extends Activity {
    public final String TAG = getClass().getName();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TestLibProject.test();
        WebView wv = (WebView)this.findViewById(R.id.webview);
        wv.loadUrl("file:///android_asset/index.html");
        WebBridge webBridge = new WebBridge(this, wv, new WebBridge.ReceiveImageCallback() {
            @Override
            public void onReceiveImage(String base64Image) {
                System.out.println("got image "  + base64Image);
            }
        });

        Futures.addCallback(TakePicture.getBase64((SurfaceView)findViewById(R.id.surface)), new FutureCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Log.d(TAG, "len:" + s.length());
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d(TAG, "Failed with " + throwable.toString());
            }
        });
    }
}
