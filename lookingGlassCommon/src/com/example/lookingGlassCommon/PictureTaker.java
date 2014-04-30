package com.example.lookingGlassCommon;

import android.hardware.Camera;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import java.io.IOException;

/**
 * Created by bkase on 4/29/14.
 */
public class PictureTaker {
    public final static String TAG = PictureTaker.class.getName();
    public Camera mCamera;
    private final SurfaceView surfaceView;

    private boolean isTakingPicture;
    private boolean isLocked;

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

    public PictureTaker(SurfaceView surfaceView) {
        this.surfaceView = surfaceView;
        openCamera();
        isTakingPicture = false;
        isLocked = false;

        Log.d(TAG, "Locked cam");
        mCamera.lock();
        isLocked = true;
        Log.d(TAG, "Done Locked cam");
        final SurfaceView dummy = surfaceView;
        SurfaceHolder holder = dummy.getHolder();
        Log.d(TAG, "Adding callback Locked cam");
        if (holder == null) {
            Log.e(TAG, "Holder is null");
            return;
        }
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try {
                    mCamera.setPreviewDisplay(surfaceHolder);
                    mCamera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "setPreview bad");
                }
                Log.d(TAG, "Surface loaded");
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

    public void release() {
        if (isLocked) {
            mCamera.unlock();
        }
        mCamera.release();
    }

    private void snap(final SettableFuture<String> base64future, SurfaceHolder holder) {
        try {
            Log.d(TAG, "Surface created, loading prev");
            if (!isTakingPicture) {
                isTakingPicture = true;
                Log.d(TAG, "Starting takePicture fo realz");
                mCamera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] bytes, Camera camera) {
                        isTakingPicture = false;
                        Log.d(TAG, "Pic taken, ");
                        mCamera.startPreview();
                        base64future.set(Base64.encodeToString(bytes, Base64.NO_WRAP));
                    }
                });
            } else {
                Log.d(TAG, "DIdn't take picture because already isTakingPicture");
            }
        } catch (Exception e) {
            Log.d(TAG, "Some exception" + e);
            mCamera.unlock();
            isLocked = false;
            base64future.setException(e);
        }
    }

    public ListenableFuture<String> snapBase64() {
        final SettableFuture<String> base64future = SettableFuture.create();

        if (mCamera == null) {
            base64future.setException(new Exception("Couldn't open camera"));
            return base64future;
        }

        SurfaceHolder holder = surfaceView.getHolder();
        // if (holder.getSurface().isValid()) {
            snap(base64future, holder);
        /*} else {
            Log.e(TAG,"Surface is bad");
        }*/

        return base64future;
    }
}
