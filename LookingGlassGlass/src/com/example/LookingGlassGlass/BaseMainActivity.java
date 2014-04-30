package com.example.LookingGlassGlass;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.lookingGlassCommon.PictureTaker;
import com.example.lookingGlassCommon.RenderPicture;
import com.example.lookingGlassCommon.WebBridge;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.Futures;

import java.util.Map;

/**
 * Created by bkase on 4/29/14.
 */
public abstract class BaseMainActivity extends Activity {

    public final static String TAG = BaseMainActivity.class.getName();

    private WebView mWebView;

    private WebBridge mWebBridge;

    private Handler mHandler;

    private PowerManager.WakeLock mWakeLock;

    private static final Map<RoomType, String> roomToDevice;
    static {
        roomToDevice = ImmutableMap.<RoomType, String>builder()
                .put(RoomType.FrontPorch, "Samsung")
                .put(RoomType.Kitchen, "Moto")
                .put(RoomType.Hand, "samsung")
                .build();
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mHandler = new Handler();
        final RoomType roomWanted = triggerRoomType();

        PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);
        mWakeLock.acquire();

        TextView roomName = (TextView)findViewById(R.id.roomname);
        roomName.setText(roomWanted.name());

        mWebView = (WebView)findViewById(R.id.webview);

        ImageView imageView = (ImageView)findViewById(R.id.imageview);

        mWebView.loadUrl("file:///android_asset/index.html");
        mWebBridge = new WebBridge(this, mWebView, null, imageView, new WebBridge.Callback() {
            @Override
            public void onPageLoad() {
                callWithDeviceName(roomToDevice.get(roomWanted));
            }
        });

    }

    public void callWithDeviceName(final String device) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mWebBridge.callWithDeviceName(device);
            }
        });
    }

    public abstract RoomType triggerRoomType();

    @Override
    public void onPause() {
        Log.d(TAG, "onPause starting");
        mWebView.clearHistory();
        mWebView.loadUrl("about:blank");
        mWebView.freeMemory();
        mWebView.clearCache(true);
        mWebView = null;
        mWakeLock.release();
        finish();
        Log.d(TAG, "onPause ending");
        super.onPause();
    }
}
