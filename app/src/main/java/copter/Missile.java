package copter;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;
import java.lang.Math;

/**
 * Created by sunny.s on 20/09/15.
 */
public class Missile extends GameObject {

    private int score;
    private int speed;
    private Random rand = new Random();
    private Animation animation = new Animation();
    private Bitmap spritesheet;

    public Missile(Bitmap res, int x, int y, int w, int h, int s, int numFrames)    {

        spritesheet = res;
        super.x = x;
        super.y = y;
        width = w;
        height = h;
        score = s;

        speed = 7 + (int) (rand.nextDouble()*score/30);

        //  cap missile speed
        speed = Math.min(speed, 40);

        Bitmap[] image = new Bitmap[numFrames];

        spritesheet = res;

        for(int i = 0; i<image.length; i++) {
            image[i] = Bitmap.createBitmap(spritesheet, 0, i*height, width, height);
        }

        animation.setFrames(image);
        animation.setDelay(100 - speed);

    }


    public void update()    {
        x -= speed;
        animation.udpate();
    }

    public void draw(Canvas canvas) {
        try {
            canvas.drawBitmap(animation.getImage(), x, y, null);
        }   catch(Exception e)  {
            e.printStackTrace();
        }
    }

    @Override
    public int getWidth()   {
        //  offset slightly for more realistic collision detection
        return width-10;
    }
}
