package com.example.LookingGlassGlass;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.lookingGlassCommon.PictureTaker;
import com.example.lookingGlassCommon.RenderPicture;
import com.google.common.util.concurrent.Futures;

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

        RoomType roomWanted = triggerRoomType();

        TextView roomName = (TextView)findViewById(R.id.roomname);
        roomName.setText(roomWanted.name());

        RenderPicture.render((ImageView)findViewById(R.id.imageview), "");
    }

    public abstract RoomType triggerRoomType();
}
