package com.example.simon.helico_flap;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Decor {

    private Bitmap image;
    private int x, y, dx;

    public Decor(Bitmap res)
    {
        image = res;
        dx = ControleJeu.MOVESPEED;
    }
    public void update()
    {
        x+=dx;
        if(x<-ControleJeu.WIDTH){
            x=0;
        }
    }
    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(image, x, y,null);
        if(x<0)
        {
            canvas.drawBitmap(image, x+ControleJeu.WIDTH, y, null);
        }
    }
}
