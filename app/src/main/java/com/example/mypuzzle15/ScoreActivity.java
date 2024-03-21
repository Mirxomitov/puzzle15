package com.example.mypuzzle15;

import android.annotation.SuppressLint;
import android.media.Image;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ScoreActivity extends AppCompatActivity {

    MyShared myShared;

    TextView score1;
    TextView score2;
    TextView score3;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        myShared = MyShared.getInstance();

        Button backButton = findViewById(R.id.scoreBackButton);
        backButton.setOnClickListener(v -> {
            finish();
        });

        score1 = findViewById(R.id.movesStatistic1);
        score2 = findViewById(R.id.movesStatistic2);
        score3 = findViewById(R.id.movesStatistic3);

        int[] bestScores = myShared.getResults();

        if (bestScores != null) {
            score1.setText(bestScores[0] == Integer.MAX_VALUE ? "No plays!" : "Moves: " + bestScores[0]);
            score2.setText(bestScores[1] == Integer.MAX_VALUE ? "No plays!" : "Moves: " + bestScores[1]);
            score3.setText(bestScores[2] == Integer.MAX_VALUE ? "No plays!" : "Moves: " + bestScores[2]);
        }

//        ImageView clearButton = findViewById(R.id.tv_restart);
//        clearButton.setOnClickListener(v -> {
//            myShared.clearPref();
//
//            score1.setText("No plays!");
//            score2.setText("No plays!");
//            score3.setText("No plays!");
//        });
    }

    @Override
    protected void onResume() {
        int[] bestScores = myShared.getResults();

        if (bestScores != null) {
            score1.setText(bestScores[0] == Integer.MAX_VALUE ? "No plays!" : "Moves: " + bestScores[0]);
            score2.setText(bestScores[1] == Integer.MAX_VALUE ? "No plays!" : "Moves: " + bestScores[1]);
            score3.setText(bestScores[2] == Integer.MAX_VALUE ? "No plays!" : "Moves: " + bestScores[2]);
        }

        super.onResume();
    }
}