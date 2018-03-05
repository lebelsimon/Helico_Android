package com.example.simon.helico_flap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnnewGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //On cache le titre
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        btnnewGame = (Button)findViewById(R.id.button_newGame);
        newGame();
    }


    public void newGame() {
        btnnewGame.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Intent jeu = new Intent(MainActivity.this, Jeu.class);
                        startActivityForResult(jeu,1000);
                    }
                }
        );
    }
}
