package copter;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by sunny.s on 20/09/15.
 */
public class TopBorder extends GameObject{

    private Bitmap image;

    public TopBorder(Bitmap res, int x, int y, int h)   {
        height = h;
        width = 20;
        this.x = x;
        this.y = y;

        dx = GameArena.MOVESPEED;
        image = Bitmap.createBitmap(res, 0, 0, width, height);
    }

    public void update()    {
        x += dx;
    }

    public void draw(Canvas canvas) {
        try{
            canvas.drawBitmap(image, x, y, null);
        }   catch (Exception e) {
            e.printStackTrace();
        }
    }
}
