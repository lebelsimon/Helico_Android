package com.example.simon.helico_flap;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.Random;



public class ControleJeu extends SurfaceView implements SurfaceHolder.Callback
{
    public static final int WIDTH = 856;
    public static final int HEIGHT = 480;
    public static final int MOVESPEED = -10;
    public static int BEST = 0;
    private long smokeStartTime;
    private long missileStartTime;
    private MainThread thread;
    private Decor decor;
    private Joueur joueur;
    private ArrayList<Fumee> fumees;
    private ArrayList<Missile> missiles;
    private ArrayList<MurHaut> murHaut;
    private ArrayList<MurBas> murBas;
    private Random rand = new Random();
    private int maxBorderHeight;
    private int minBorderHeight;
    private boolean topDown = true;
    private boolean botDown = true;
    private boolean newGameCreated;

    private int progressDenom = 20;

    private Explosion explosion;
    private long startReset;
    private boolean reset;
    private boolean dissapear;
    private boolean started;




    public ControleJeu(Context context)
    {
        super(context);
        getHolder().addCallback(this);
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
                thread = null;

            }catch(InterruptedException e){e.printStackTrace();}

        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){

        decor = new Decor(BitmapFactory.decodeResource(getResources(), R.drawable.grassbg1));
        joueur = new Joueur(BitmapFactory.decodeResource(getResources(), R.drawable.helicopter), 65, 25, 3);
        fumees = new ArrayList<Fumee>();
        missiles = new ArrayList<Missile>();
        murHaut = new ArrayList<MurHaut>();
        murBas = new ArrayList<MurBas>();
        smokeStartTime=  System.nanoTime();
        missileStartTime = System.nanoTime();

        thread = new MainThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();

    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            if(!joueur.getPlaying() && newGameCreated && reset)
            {
                joueur.setPlaying(true);
                joueur.setUp(true);
            }
            if(joueur.getPlaying())
            {

                if(!started)started = true;
                reset = false;
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

            if(murBas.isEmpty())
            {
                joueur.setPlaying(false);
                return;
            }
            if(murHaut.isEmpty())
            {
                joueur.setPlaying(false);
                return;
            }

            decor.update();
            joueur.update();

            maxBorderHeight = 30+joueur.getScore()/progressDenom;
            if(maxBorderHeight > HEIGHT/4)maxBorderHeight = HEIGHT/4;
            minBorderHeight = 5+joueur.getScore()/progressDenom;

            for(int i = 0; i<murBas.size(); i++)
            {
                if(collision(murBas.get(i), joueur))
                    joueur.setPlaying(false);
            }

            for(int i = 0; i <murHaut.size(); i++)
            {
                if(collision(murHaut.get(i), joueur))
                    joueur.setPlaying(false);
            }

            this.updateTopBorder();

            this.updateBottomBorder();

            long missileElapsed = (System.nanoTime()-missileStartTime)/1000000;
            if(missileElapsed >(1000 - joueur.getScore()/4)){


                if(missiles.size()==0)
                {
                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(),R.drawable.
                            missile),WIDTH + 10, HEIGHT/2, 45, 15, joueur.getScore(), 13));
                }
                else
                {

                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(),R.drawable.missile),
                            WIDTH+10, (int)(rand.nextDouble()*(HEIGHT - (maxBorderHeight * 2))+maxBorderHeight),45,15, joueur.getScore(),13));
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
        else{
            joueur.resetDY();
            if(!reset)
            {
                newGameCreated = false;
                startReset = System.nanoTime();
                reset = true;
                dissapear = true;
                explosion = new Explosion(BitmapFactory.decodeResource(getResources(),R.drawable.explosion),joueur.getX(),
                        joueur.getY()-30, 100, 100, 25);
            }

            explosion.update();
            long resetElapsed = (System.nanoTime()-startReset)/1000000;

            if(resetElapsed > 2500 && !newGameCreated)
            {
                newGame();
            }


        }

    }
    public boolean collision(ObjetJeu a, ObjetJeu b)
    {
        if(Rect.intersects(a.getRectangle(), b.getRectangle()))
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
            if(!dissapear) {
                joueur.draw(canvas);
            }

            for(Fumee sp: fumees)
            {
                sp.draw(canvas);
            }

            for(Missile m: missiles)
            {
                m.draw(canvas);
            }

            for(MurHaut mh: murHaut)
            {
                mh.draw(canvas);
            }

            for(MurBas mb: murBas)
            {
                mb.draw(canvas);
            }

            if(started)
            {
                explosion.draw(canvas);
            }
            drawText(canvas);
            canvas.restoreToCount(savedState);

        }
    }

    public void updateTopBorder()
    {
        if(joueur.getScore()%50 ==0)
        {
            murHaut.add(new MurHaut(BitmapFactory.decodeResource(getResources(),R.drawable.brick
            ),murHaut.get(murHaut.size()-1).getX()+20,0,(int)((rand.nextDouble()*(maxBorderHeight
            ))+1)));
        }
        for(int i = 0; i<murHaut.size(); i++)
        {
            murHaut.get(i).update();
            if(murHaut.get(i).getX()<-20)
            {
                murHaut.remove(i);
                if(murHaut.get(murHaut.size()-1).getHeight()>=maxBorderHeight)
                {
                    topDown = false;
                }
                if(murHaut.get(murHaut.size()-1).getHeight()<=minBorderHeight)
                {
                    topDown = true;
                }
                if(topDown)
                {
                    murHaut.add(new MurHaut(BitmapFactory.decodeResource(getResources(),
                            R.drawable.brick),murHaut.get(murHaut.size()-1).getX()+20,
                            0, murHaut.get(murHaut.size()-1).getHeight()+1));
                }
                else
                {
                    murHaut.add(new MurHaut(BitmapFactory.decodeResource(getResources(),
                            R.drawable.brick),murHaut.get(murHaut.size()-1).getX()+20,
                            0, murHaut.get(murHaut.size()-1).getHeight()-1));
                }

            }
        }

    }
    public void updateBottomBorder()
    {
        if(joueur.getScore()%40 == 0)
        {
            murBas.add(new MurBas(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                    murBas.get(murBas.size()-1).getX()+20,(int)((rand.nextDouble()
                    *maxBorderHeight)+(HEIGHT-maxBorderHeight))));
        }

        for(int i = 0; i<murBas.size(); i++)
        {
            murBas.get(i).update();

            if(murBas.get(i).getX()<-20) {
                murBas.remove(i);


                if (murBas.get(murBas.size() - 1).getY() <= HEIGHT-maxBorderHeight) {
                    botDown = true;
                }
                if (murBas.get(murBas.size() - 1).getY() >= HEIGHT - minBorderHeight) {
                    botDown = false;
                }

                if (botDown) {
                    murBas.add(new MurBas(BitmapFactory.decodeResource(getResources(), R.drawable.brick
                    ), murBas.get(murBas.size() - 1).getX() + 20, murBas.get(murBas.size() - 1
                    ).getY() + 1));
                } else {
                    murBas.add(new MurBas(BitmapFactory.decodeResource(getResources(), R.drawable.brick
                    ), murBas.get(murBas.size() - 1).getX() + 20, murBas.get(murBas.size() - 1
                    ).getY() - 1));
                }
            }
        }
    }
    public void newGame()
    {
        dissapear = false;

        murBas.clear();
        murHaut.clear();

        missiles.clear();
        fumees.clear();

        minBorderHeight = 5;
        maxBorderHeight = 30;

        joueur.resetDY();
        joueur.resetScore();
        joueur.setY(HEIGHT/2);

        if(joueur.getScore()>BEST)
        {
            BEST = joueur.getScore();

        }

        for(int i = 0; i*20<WIDTH+40;i++)
        {
            //first top border create
            if(i==0)
            {
                murHaut.add(new MurHaut(BitmapFactory.decodeResource(getResources(),R.drawable.brick
                ),i*20,0, 10));
            }
            else
            {
                murHaut.add(new MurHaut(BitmapFactory.decodeResource(getResources(),R.drawable.brick
                ),i*20,0, murHaut.get(i-1).getHeight()+1));
            }
        }
        for(int i = 0; i*20<WIDTH+40; i++)
        {
            if(i==0)
            {
                murBas.add(new MurBas(BitmapFactory.decodeResource(getResources(),R.drawable.brick)
                        ,i*20,HEIGHT - minBorderHeight));
            }

            else
            {
                murBas.add(new MurBas(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                        i * 20, murBas.get(i - 1).getY() - 1));
            }
        }

        newGameCreated = true;


    }
    public void drawText(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(30);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("DISTANCE: " + joueur.getScore(), 10, HEIGHT - 10, paint);
        canvas.drawText("BEST: " + BEST, WIDTH - 215, HEIGHT - 10, paint);

        if(!joueur.getPlaying()&&newGameCreated&&reset)
        {
            Paint paint1 = new Paint();
            paint1.setTextSize(50);
            paint1.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("PRESS TO START", WIDTH/2-50, HEIGHT/2, paint1);

            paint1.setTextSize(20);
            canvas.drawText("PRESS AND HOLD TO GO UP", WIDTH/2-50, HEIGHT/2 + 20, paint1);
            canvas.drawText("RELEASE TO GO DOWN", WIDTH/2-50, HEIGHT/2 + 40, paint1);
        }
    }


}