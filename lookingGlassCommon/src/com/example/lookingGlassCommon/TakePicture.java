package com.example.lookingGlassCommon;

import android.content.Context;
import android.hardware.Camera;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

/**
 * Created by bkase on 4/29/14.
 */
public class TakePicture {
    public final static String TAG = TakePicture.class.getName();

    public static Camera openCamera() {
        try {
            return Camera.open();
        } catch (Exception e) {
            return null;
        }
    }

    public static ListenableFuture<String> getBase64(SurfaceView surfaceView) {
        final SettableFuture<String> base64future = SettableFuture.create();
        final Camera camera = openCamera();

        Log.d(TAG, "Starting getBase64");
        if (camera == null) {
            base64future.setException(new Exception("Couldn't open camera"));
            return base64future;
        }
        Log.d(TAG, "Locked cam");
        camera.lock();
        Log.d(TAG, "Done Locked cam");
        final SurfaceView dummy = surfaceView;
        SurfaceHolder holder = dummy.getHolder();
        Log.d(TAG, "Adding callback Locked cam");
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try {
                    Log.d(TAG, "Surface created, loading prev");
                    camera.setPreviewDisplay(dummy.getHolder());
                    camera.startPreview();
                    camera.takePicture(null, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] bytes, Camera camera) {
                            Log.d(TAG, "Pic taken, ");
                            base64future.set(Base64.encodeToString(bytes, Base64.DEFAULT));
                            camera.unlock();
                        }
                    });
                } catch (Exception e) {
                    Log.d(TAG, "Some exception" + e);
                    base64future.setException(e);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        return base64future;
    }
}
