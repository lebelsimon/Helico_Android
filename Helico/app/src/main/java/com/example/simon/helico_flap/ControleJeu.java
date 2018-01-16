package com.example.simon.helico_flap;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;


public class ControleJeu extends SurfaceView implements SurfaceHolder.Callback
{
    public static final int WIDTH = 856;
    public static final int HEIGHT = 480;
    public static final int MOVESPEED = -5;
    private long smokeStartTime;
    private long missileStartTime;
    private MainThread thread;
    private Decor decor;
    private Joueur joueur;
    private ArrayList<Fumee> fumees;
    private ArrayList<Missile> missiles;
    private Random rand = new Random();


    public ControleJeu(Context context)
    {
        super(context);


        //add the callback to the surfaceholder to intercept events
        getHolder().addCallback(this);

        thread = new MainThread(getHolder(), this);

        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        boolean retry = true;
        int counter = 0;
        while(retry && counter<1000)
        {
            counter++;
            try{thread.setRunning(false);
                thread.join();
                retry = false;

            }catch(InterruptedException e){e.printStackTrace();}

        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){

        decor = new Decor(BitmapFactory.decodeResource(getResources(), R.drawable.grassbg1));
        joueur = new Joueur(BitmapFactory.decodeResource(getResources(), R.drawable.helicopter), 65, 25, 3);
        fumees = new ArrayList<Fumee>();
        missiles = new ArrayList<Missile>();
        smokeStartTime=  System.nanoTime();
        missileStartTime = System.nanoTime();

        thread.setRunning(true);
        thread.start();

    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            if(!joueur.getPlaying())
            {
                joueur.setPlaying(true);
            }
            else
            {
                joueur.setUp(true);
            }
            return true;
        }
        if(event.getAction()==MotionEvent.ACTION_UP)
        {
            joueur.setUp(false);
            return true;
        }

        return super.onTouchEvent(event);
    }

    public void update()

    {
        if(joueur.getPlaying()) {

            decor.update();
            joueur.update();

            //add missiles on timer
            long missileElapsed = (System.nanoTime()-missileStartTime)/1000000;
            if(missileElapsed >(1000 - joueur.getScore()/4)){

                System.out.println("making missile");

                if(missiles.size()==0)
                {
                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(),R.drawable.
                            missile),WIDTH + 10, HEIGHT/2, 45, 15, joueur.getScore(), 13));
                }
                else
                {

                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(),R.drawable.missile),
                            WIDTH+10, (int)(rand.nextDouble()*(HEIGHT)),45,15, joueur.getScore(),13));
                }

                missileStartTime = System.nanoTime();
            }

            for(int i = 0; i<missiles.size();i++)
            {

                missiles.get(i).update();

                if(collision(missiles.get(i),joueur))
                {
                    missiles.remove(i);
                    joueur.setPlaying(false);
                    break;
                }

                if(missiles.get(i).getX()<-100)
                {
                    missiles.remove(i);
                    break;
                }
            }

            long elapsed = (System.nanoTime() - smokeStartTime)/1000000;
            if(elapsed > 120){
                fumees.add(new Fumee(joueur.getX(), joueur.getY()+10));
                smokeStartTime = System.nanoTime();
            }

            for(int i = 0; i<fumees.size();i++)
            {
                fumees.get(i).update();
                if(fumees.get(i).getX()<-10)
                {
                    fumees.remove(i);
                }
            }
        }
    }
    public boolean collision(ObjetJeu a, ObjetJeu b)
    {
        if(Rect.intersects(a.getRectangle(),b.getRectangle()))
        {
            return true;
        }
        return false;
    }
    @Override
    public void draw(Canvas canvas)
    {
        final float scaleFactorX = getWidth()/(WIDTH*1.f);
        final float scaleFactorY = getHeight()/(HEIGHT*1.f);

        if(canvas!=null) {
            final int savedState = canvas.save();



            canvas.scale(scaleFactorX, scaleFactorY);
            decor.draw(canvas);
            joueur.draw(canvas);

            for(Fumee sp: fumees)
            {
                sp.draw(canvas);
            }

            for(Missile m: missiles)
            {
                m.draw(canvas);
            }
            canvas.restoreToCount(savedState);
        }
    }


}