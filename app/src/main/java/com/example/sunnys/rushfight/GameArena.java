package com.example.sunnys.rushfight;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * Created by sunny.s on 09/09/15.
 */
public class GameArena extends SurfaceView implements SurfaceHolder.Callback{

    public static final int WIDTH = 856;
    public static final int HEIGHT = 480;
    public static final int MOVESPEED = -5;
    private MainThread thread;
    private Background bg;
    private Player player;
    private ArrayList<SmokePuff> smoke;
    private long smokeStartTime;

    public GameArena(Context context) {
        super(context);

        //  Add the callback to the surface holder to intercept events
        getHolder().addCallback(this);

        thread = new MainThread(getHolder(), this);

        //  Make GamePane focusable so it can handle events
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.grassbg1));
        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.helicopter), 65, 25, 3);
        player.resetDY();

        smoke = new ArrayList<SmokePuff>();
        smokeStartTime = System.nanoTime();

        //  Start the game loop
        thread.setRunning(true);
        thread.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        boolean retry = true;
        int counter = 0;
        while (retry && counter<1000)   {
            counter++;
            try {
                thread.setRunning(false);
                thread.join();
            }   catch (Exception e)    {
                e.printStackTrace();
            }
            retry = false;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)  {

        if(event.getAction() == MotionEvent.ACTION_DOWN)    {
            System.out.println("ACTION_DOWN");
            if(!player.getPlaying())    {
                player.setPlaying(true);
            }
            else    {
                player.setUp(true);
            }
            return true;
        }

        if(event.getAction() == MotionEvent.ACTION_UP)    {
            System.out.println("ACTION_UP");
            player.setUp(false);
            return true;
        }

        return super.onTouchEvent(event);
    }

    public void update() {

        if(player.getPlaying()) {
            bg.update();
            player.update();

            long elapsed = (System.nanoTime() - smokeStartTime)/1000000;
            if (elapsed > 120) {
                smoke.add(new SmokePuff(player.getX(), player.getY()+10));
                smokeStartTime = System.nanoTime();
            }

            for (int i=0; i<smoke.size(); i++) {
                smoke.get(i).update();

                if (smoke.get(i).getX()<-10) {
                    smoke.remove(i);
                }
            }
        }

    }

    @Override
    public void draw(Canvas canvas)    {

        final float scaleFactorX = getWidth() / (WIDTH * 1.f);
        final float scaleFactorY = getHeight() / (HEIGHT * 1.f);
        if (canvas!=null) {
            final int savedState = canvas.save();
            canvas.scale(scaleFactorX, scaleFactorY);
            bg.draw(canvas);
            player.draw(canvas);

            for (SmokePuff sp : smoke) {
                sp.draw(canvas);
            }

            canvas.restoreToCount(savedState);
        }
    }
}
