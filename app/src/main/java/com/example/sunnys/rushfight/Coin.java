package com.example.sunnys.rushfight;

import android.graphics.Bitmap;

/**
 * Created by sunny.s on 27/08/15.
 */
public class Coin extends GameEntity {

    public Coin(Bitmap bitmap, Position position)   {
        this.bmp = bitmap;
        this.pos = position;
    }

    public Position getPosition()   {
        return pos;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bmp = bitmap;
    }
}
