package com.example.simon.helico_flap;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class Jeu extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //On cache le titre
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(new ControleJeu(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
