package com.example.simon.helico_flap;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class MurHaut extends ObjetJeu{
    private Bitmap image;

    public MurHaut(Bitmap res, int x, int y, int h)
    {
        height = h;
        width = 20;

        this.x = x;
        this.y = y;

        dx = ControleJeu.MOVESPEED;
        image = Bitmap.createBitmap(res, 0, 0, width, height);
    }
    public void update()
    {
        x+=dx;
    }
    public void draw(Canvas canvas)
    {
        try{canvas.drawBitmap(image,x,y,null);}catch(Exception e){};
    }

}
