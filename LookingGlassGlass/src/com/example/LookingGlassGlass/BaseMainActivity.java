package com.example.LookingGlassGlass;

import android.app.Activity;
import android.os.Bundle;
import com.example.lookingGlassCommon.TestLibProject;

/**
 * Created by bkase on 4/29/14.
 */
public abstract class BaseMainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TestLibProject.test();
    }
}
