package com.example.simon.helico_flap;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class MurBas extends ObjetJeu{

    private Bitmap image;
    public MurBas(Bitmap res, int x, int y)
    {
        height = 200;
        width = 20;

        this.x = x;
        this.y = y;
        dx = ControleJeu.MOVESPEED;

        image = Bitmap.createBitmap(res, 0, 0, width, height);

    }
    public void update()
    {
        x +=dx;

    }
    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(image, x, y, null);

    }
}
