package com.example.LookingGlassPhone;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import com.example.lookingGlassCommon.TestLibProject;
import com.example.lookingGlassCommon.WebBridge;

public class MainActivity extends Activity {
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
    }
}
