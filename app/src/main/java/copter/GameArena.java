package copter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.sunnys.rushfight.R;

import java.util.ArrayList;
import java.util.Random;

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
    private ArrayList<Missile> missiles;
    private ArrayList<TopBorder> topBorder;
    private ArrayList<BotBorder> botBorder;
    private long smokeStartTime;
    private long missileStartTime;
    private Random rand = new Random();
    private int maxBorderHeight;
    private int minBorderHeight;
    private boolean topDown = true;
    private boolean botDown = true;
    //  Increase to increase difficult progression, Decrease to decrease difficult progression,
    private int progressDenom = 20;

    private Explosion explosion;
    private long startReset;
    private boolean reset;
    private boolean disappear;
    private boolean started;
    private int best = 0;

    private boolean newGameCreated;


    public GameArena(Context context) {
        super(context);

        //  Add the callback to the surface holder to intercept events
        getHolder().addCallback(this);

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

        missiles = new ArrayList<Missile>();
        missileStartTime = System.nanoTime();

        topBorder = new ArrayList<TopBorder>();
        botBorder = new ArrayList<BotBorder>();

        //  Start the game loop
        thread = new MainThread(getHolder(), this);
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
            thread = null;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)  {

        if(event.getAction() == MotionEvent.ACTION_DOWN)    {
            if(!player.getPlaying() && newGameCreated && reset)    {
                player.setPlaying(true);
            }
            if(player.getPlaying()) {
                if(!started) started = true;
                reset = false;
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

            if(botBorder.isEmpty()) {
                player.setPlaying(false);
                return;
            }

            if(topBorder.isEmpty()) {
                player.setPlaying(false);
                return;
            }

            bg.update();
            player.update();

            //  calculate the threshold of height the border can have based on the score
            //  max and min border heart are updated, adn the border switched direction when either max or
            //  min is met

            maxBorderHeight = 30 + player.getScore()/progressDenom;
            //  Cap  max border height so that borders oly take up a max of 1/2 of the screen
            if(maxBorderHeight > HEIGHT/4)maxBorderHeight = HEIGHT/4;


            minBorderHeight = 5 + player.getScore()/progressDenom;

            //  update top border
            updateTopBorder();

            //  update bottom border
            updateBottomBorder();


            //  Add missiles on timer
            long missileElapsed = (System.nanoTime() - missileStartTime)/1000000;
            if(missileElapsed > (2000 - player.getScore()/4))   {

                //  First missile always goes down the middle
                if(missiles.size()==0)  {
                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(), R.drawable.missile),
                            WIDTH+10, HEIGHT/2, 45, 15, player.getScore(), 13));
                }
                else    {
                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(), R.drawable.missile),
                            WIDTH+10, (int)(rand.nextDouble()*(HEIGHT)), 45, 15, player.getScore(), 13));
                }

                //  reset timer
                missileStartTime = System.nanoTime();
            }

            //  check collision with every missile
            for (int i=0; i<missiles.size(); i++)   {
                missiles.get(i).update();;
                if(collision(missiles.get(i), player))  {
                    missiles.remove(i);
                    player.setPlaying(false);
                    break;
                }

                //  remove missile if it off the screen
                if(missiles.get(i).getX()<-100)  {
                    missiles.remove(i);
                    break;
                }
            }


            //  Add smoke puffs on timer
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
        else    {
            player.resetDY();

            if(!reset)  {
                newGameCreated = false;
                startReset = System.nanoTime();
                reset = true;
                disappear = true;
                explosion = new Explosion(BitmapFactory.decodeResource(getResources(), R.drawable.explosion),
                        player.getX(), player.getY()-30, 100, 100, 25);
            }

            explosion.update();
            long resetElapsed = (System.nanoTime()-startReset)/1000000;

            if(resetElapsed > 2500 && !newGameCreated)  {
                newGame();
            }
        }

    }


    public boolean collision(GameObject a, GameObject b)    {
        if (Rect.intersects(a.getRectangle(), b.getRectangle()))    {
            return true;
        }

        return false;
    }

    @Override
    public void draw(Canvas canvas)    {

        final float scaleFactorX = getWidth() / (WIDTH * 1.f);
        final float scaleFactorY = getHeight() / (HEIGHT * 1.f);
        if (canvas!=null) {
            final int savedState = canvas.save();
            canvas.scale(scaleFactorX, scaleFactorY);
            bg.draw(canvas);

            if(!disappear)
                player.draw(canvas);

            for (SmokePuff sp : smoke) {
                sp.draw(canvas);
            }
            for (Missile m: missiles)   {
                m.draw(canvas);
            }

            //  Draw top border
            for (TopBorder tb: topBorder)   {
                tb.draw(canvas);
            }

            //  Draw top border
            for (BotBorder bb: botBorder)   {
                bb.draw(canvas);
            }

            //  Draw explosion
            if(started) {
                explosion.draw(canvas);
            }

            drawText(canvas);
            canvas.restoreToCount(savedState);
        }
    }

    private void updateTopBorder()   {
        //  Every 50 points, insert randomly placed bottom blocks that break the pattern
        if(player.getScore()%50 == 0)   {
            topBorder.add(new TopBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
            topBorder.get(topBorder.size()-1).getX()+20, 0, (int)((rand.nextDouble()*(maxBorderHeight))+1)));
        }

        for(int i=0; i<topBorder.size(); i++)   {
            topBorder.get(i).update();
            if(topBorder.get(i).getX()<-20) {
                topBorder.remove(i);
                //  remove element of array list, replace by adding a new one

                //  Calculate topDown that determines the direction the border is moving (up or down)
                if(topBorder.get(topBorder.size()-1).getHeight()>=maxBorderHeight)  {
                    topDown = false;
                }
                if(topBorder.get(topBorder.size()-1).getHeight()<=minBorderHeight)  {
                    topDown = true;
                }

                //  new border will have larger height
                if(topDown) {
                    topBorder.add(new TopBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                            topBorder.get(topBorder.size()-1).getX()+20, 0, topBorder.get(topBorder.size()-1).getHeight()+1));
                }
                //  new border will have smaller height
                else    {
                    topBorder.add(new TopBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                            topBorder.get(topBorder.size()-1).getX()+20, 0, topBorder.get(topBorder.size()-1).getHeight()-1));
                }
            }
        }
    }

    private void updateBottomBorder()    {
        //  Every 50 points, insert randomly placed top blocks that break the pattern
        if(player.getScore()%40 == 0)   {
            botBorder.add(new BotBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                    botBorder.get(botBorder.size()-1).getX()+20, (int)(rand.nextDouble()*(maxBorderHeight)
                    +(HEIGHT - maxBorderHeight))));
        }


        for(int i=0; i<botBorder.size(); i++)   {
            botBorder.get(i).update();
            if(botBorder.get(i).getX()<-20) {
                botBorder.remove(i);
                //  remove element of array list, replace by adding a new one

                //  Calculate topDown that determines the direction the border is moving (up or down)
                if(botBorder.get(botBorder.size()-1).getHeight()>=maxBorderHeight)  {
                    botDown = false;
                }
                if(botBorder.get(botBorder.size()-1).getHeight()<=minBorderHeight)  {
                    botDown = true;
                }

                //  new border will have larger height
                if(botDown) {
                    botBorder.add(new BotBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                            botBorder.get(botBorder.size()-1).getX()+20, botBorder.get(botBorder.size()-1).getY()+1));
                }
                //  new border will have smaller height
                else    {
                    botBorder.add(new BotBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                            botBorder.get(botBorder.size()-1).getX()+20, botBorder.get(botBorder.size()-1).getY()-1));
                }
            }
        }
    }

    public void newGame()   {

        disappear = false;
        topBorder.clear();
        botBorder.clear();
        missiles.clear();
        smoke.clear();

        minBorderHeight = 5;
        maxBorderHeight = 30;

        player.setY(HEIGHT/2);

        if(player.getScore() > best)    {
            best = player.getScore();
        }

        player.resetScore();

        //  create initial border
        for(int i=0; i*20<WIDTH+40; i++)    {
            if(i==0)    {
                topBorder.add(new TopBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick)
                        , i*20, 0, 10));
            }
            else    {
                topBorder.add(new TopBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick)
                        , i*20, 0, topBorder.get(i-1).getHeight()+1));
            }
        }

//        System.out.println("Top");
//        for(int i=0; i<topBorder.size(); i++)
//            System.out.print(topBorder.get(i).getX() + "," + topBorder.get(i).getY() + " ");

        for(int i=0; i*20<WIDTH+40; i++)    {
            if(i==0)    {
                botBorder.add(new BotBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick)
                        , i*20, HEIGHT - minBorderHeight));
            }
            else    {
                botBorder.add(new BotBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick)
                        , i*20, botBorder.get(i-1).getY()-1));
            }
        }

//        System.out.println("Bottom");
//        for(int i=0; i<topBorder.size(); i++)
//            System.out.print(botBorder.get(i).getX() + "," + botBorder.get(i).getY() + " ");

        newGameCreated = true;
    }

    public void drawText(Canvas canvas)  {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(30);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("DISTANCE: " + (player.getScore() * 3), 10, HEIGHT - 10, paint);
        canvas.drawText("BEST: " + best, WIDTH - 215, HEIGHT - 10, paint);

        if(!player.getPlaying() && newGameCreated && reset) {
            Paint paint1 = new Paint();
            paint1.setTextSize(40);
            paint1.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("PRESS TO START", WIDTH/2 - 50, HEIGHT/2, paint);

            paint1.setTextSize(20);
            canvas.drawText("PRESS AND HOLD TO GO UP", WIDTH/2-50, HEIGHT/2 + 20, paint1);
            canvas.drawText("RELEASE TO GO DOWN", WIDTH/2-50, HEIGHT/2 + 40, paint1);
        }
    }
}
