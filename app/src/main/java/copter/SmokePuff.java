package copter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by sunny.s on 20/09/15.
 */
public class SmokePuff extends GameObject{

    public int radius;

    public SmokePuff(int x, int y) {
        super.x = x;
        super.y = y;
        radius = 5;
    }

    public void update() {
        x -= 10;
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(x-radius, y-radius, radius, paint);
        canvas.drawCircle(x-radius+2, y-radius-2, radius, paint);
        canvas.drawCircle(x-radius+4, y-radius+1, radius, paint);

    }

}
