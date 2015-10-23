package com.example.sunnys.rushfight;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by sunny.s on 22/10/15.
 */
public class GameArena extends SurfaceView implements SurfaceHolder.Callback{

    public GameArena(Context context)   {
        super(context);

        //  Add the callback to the surface holder to intercept events
        getHolder().addCallback(this);

        //  Make GamePane focusable so it can handle events
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
