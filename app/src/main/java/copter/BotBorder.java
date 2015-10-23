package copter;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by sunny.s on 20/09/15.
 */
public class BotBorder extends GameObject {

    private Bitmap image;

    public BotBorder(Bitmap res, int x, int y)  {
        height = 200;
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
        canvas.drawBitmap(image, x, y, null);
    }
}
