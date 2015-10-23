package com.example.sunnys.rushfight;

import android.graphics.Bitmap;
import android.graphics.Rect;

/**
 * Created by sunny.s on 27/08/15.
 */
public class GameEntity {

    protected Bitmap bmp;
    protected Position pos;
    protected Speed speed;
    protected int height;
    protected int width;

    public Position getPosition()   {
        return pos;
    }

    public void setPosition(Position position)   {
        this.pos.x = position.x;
        this.pos.y = position.y;
    }

    public Speed getSpeed()   {
        return speed;
    }

    public int getHeight()  {
        return height;
    }

    public int getWidth()  {
        return width;
    }

    public void setSpeed(Speed speed)   {
        this.speed.dx = speed.dx;
        this.speed.dy = speed.dy;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bmp = bitmap;
    }

    public Rect getRectangle()  {
        return new Rect(pos.x, pos.y, pos.x+width, pos.y+height);
    }
}
