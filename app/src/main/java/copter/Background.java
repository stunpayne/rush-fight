package copter;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by sunny.s on 13/09/15.
 */
public class Background {
    private Bitmap image;
    private int x, y, dx;

    public Background(Bitmap bmp)   {
        this.image = bmp;
        this.dx = GameArena.MOVESPEED;
    }

    public void update()    {
        x+=dx;
        if(x<-GameArena.WIDTH) {
            x=0;
        }
    }

    public void draw(Canvas canvas)  {
        canvas.drawBitmap(image, x, y, null);
        if(x<0) {
            canvas.drawBitmap(image, x+GameArena.WIDTH, y, null);
        }
    }
}
