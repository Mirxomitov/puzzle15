package com.example.mypuzzle15;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

public class MainActivity extends AppCompatActivity {

    LinearLayoutCompat continueGame;
    private MediaPlayer mediaPlayer;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mediaPlayer = MediaPlayer.create(this, R.raw.music_for_puzzle_game);        mediaPlayer.start();
//        mediaPlayer.setLooping(true);

        continueGame = findViewById(R.id.continueGame);

        continueGame.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PlayGameActivity.class);
            startActivity(intent);
        });

        LinearLayoutCompat statisticsButton = findViewById(R.id.statistics);
        statisticsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ScoreActivity.class);
            startActivity(intent);
        });

        LinearLayoutCompat newGameButton = findViewById(R.id.newGame);
        newGameButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PlayGameActivity.class);
            intent.putExtra("IS_NEW_GAME", true);
            startActivity(intent);
        });

        LinearLayoutCompat infoButton = findViewById(R.id.info);
        infoButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, InfoActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MyShared.getInstance().isActive()) {
            continueGame.setVisibility(View.GONE);
        } else {
            continueGame.setVisibility(View.VISIBLE);
        }

        //mediaPlayer.start();
    }

    private long lastPressedTime;
    private static final int PERIOD = 2000;


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (event.getDownTime() - lastPressedTime < PERIOD) {
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Press again to exit.",
                            Toast.LENGTH_LONG).show();
                    lastPressedTime = event.getEventTime();
                }
                return true;
            }
        }
        return false;
    }

}