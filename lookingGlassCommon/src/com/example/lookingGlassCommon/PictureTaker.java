package com.example.lookingGlassCommon;

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
public class PictureTaker {
    public final static String TAG = PictureTaker.class.getName();
    public Camera mCamera;

    public void openCamera() {
        try {
            mCamera = Camera.open();

            Camera.Parameters params = mCamera.getParameters();
            params.setPictureSize(640, 480);
            mCamera.setParameters(params);
        } catch (Exception e) {
            mCamera = null;
        }
    }

    public PictureTaker() {
        openCamera();
    }

    public void release() {
        mCamera.release();
    }

    private void snap(final SettableFuture<String> base64future, SurfaceHolder holder) {
        try {
            Log.d(TAG, "Surface created, loading prev");
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            mCamera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] bytes, Camera camera) {
                    Log.d(TAG, "Pic taken, ");
                    camera.unlock();
                    base64future.set(Base64.encodeToString(bytes, Base64.DEFAULT));
                }
            });
        } catch (Exception e) {
            mCamera.unlock();
            Log.d(TAG, "Some exception" + e);
            base64future.setException(e);
        }
    }

    public ListenableFuture<String> snapBase64(SurfaceView surfaceView) {
        final SettableFuture<String> base64future = SettableFuture.create();

        Log.d(TAG, "Starting snapBase64");
        if (mCamera == null) {
            base64future.setException(new Exception("Couldn't open camera"));
            return base64future;
        }

        Log.d(TAG, "Locked cam");
        mCamera.lock();
        Log.d(TAG, "Done Locked cam");
        final SurfaceView dummy = surfaceView;
        SurfaceHolder holder = dummy.getHolder();
        Log.d(TAG, "Adding callback Locked cam");
        if (holder == null) {
            base64future.setException(new Exception("Surface holder is null"));
            return base64future;
        }
        if (holder.getSurface().isValid()) {
            snap(base64future, holder);
        } else {
            holder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    snap(base64future, surfaceHolder);
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

                }
            });
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        return base64future;
    }
}
