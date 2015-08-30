package com.example.sunnys.rushfight;

import android.graphics.Bitmap;

/**
 * Created by sunny.s on 27/08/15.
 */
public class Player extends GameEntity  {

    public Speed speed;

    public Position getPosition()   {
        return this.pos;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bmp = bitmap;
    }

    public Speed getSpeed() {
        return this.speed;
    }
}
