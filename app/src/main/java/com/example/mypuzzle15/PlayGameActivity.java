package com.example.mypuzzle15;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayGameActivity extends AppCompatActivity {

    private final Button[][] buttons = new Button[4][4];
    private final List<String> buttonValues = new ArrayList<>(16);

    private int x = 3;
    private int y = 3;

    private boolean isNewGame; // continue, or newGame

    private int movesCount;

    private Chronometer chronometer;

    private SharedPreferences sharedPref;

    private MyShared myShared;

    private MediaPlayer mediaPlayer;

    private ImageView onMusic;
    private ImageView offMusic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        // TODO shared
        sharedPref = getSharedPreferences("puzzle_15", MODE_PRIVATE);
        myShared = MyShared.getInstance();

        chronometer = findViewById(R.id.gameChronometer);
        chronometer.start();

        isNewGame = getIntent().getBooleanExtra("IS_NEW_GAME", false);

        ImageView backButton = findViewById(R.id.scoreBackButton);
        backButton.setOnClickListener(v -> {
            finish();
        });

        mediaPlayer = MediaPlayer.create(this, R.raw.music_for_puzzle_game);
        onMusic = findViewById(R.id.music_on);
        offMusic = findViewById(R.id.music_off);

        findViewById(R.id.restartGameButton).setOnClickListener(this::onClickRestart);

        addValuesToList();
        shuffleValues();
        initButtons();
        putValueAndBackgroundToButtons();
    }

    @Override
    protected void onResume() {
        // button states rewrote, moves count set
        int best = myShared.getBest();

        if (best != Integer.MAX_VALUE) {
            TextView bestMoves = findViewById(R.id.bestMoves);
            bestMoves.setText(String.valueOf(best));
        }

        if (sharedPref.getString("BUTTONS_STATE", "").equals("")) {
            super.onResume();
            return;
        }

        if (!isNewGame)
        // continue last game
        {
            // get values from shared Preferences
            String[] arrayOfNumbers = sharedPref.getString("BUTTONS_STATE", "").split("#");

            movesCount = sharedPref.getInt("MOVES_COUNT", 0);
            long elapsedTime = SystemClock.elapsedRealtime() - sharedPref.getLong("TIME", 0);

            chronometer.setBase(elapsedTime);
            chronometer.start();

            // set values to fields
            TextView movesCountButton = findViewById(R.id.movesCountText);
            movesCountButton.setText(String.valueOf(movesCount));

            // set texts to views
            Chronometer chronometerView = findViewById(R.id.gameChronometer);
            chronometerView.setBase(chronometer.getBase());

            for (int i = 0; i < 16; i++) {
                if (arrayOfNumbers[i].equals("0")) {
                    x = i / 4;
                    y = i % 4;
                    buttonValues.set(i, "");
                } else {
                    buttonValues.set(i, arrayOfNumbers[i]);
                }
            }

            putValueAndBackgroundToButtons();
        }

        mediaPlayer.setLooping(true);
        super.onResume();
    }

    @Override
    protected void onStop() {
        StringBuilder lastButtonsState = new StringBuilder();
        for (int i = 0; i < 16; i++) {

            int row = i / 4;
            int col = i % 4;

            if (buttons[row][col].getText().toString().equals("")) {
                lastButtonsState.append("0").append("#");
            } else {
                lastButtonsState.append(buttons[row][col].getText().toString()).append("#");
            }
        }

        mediaPlayer.pause();

        chronometer.stop();
        long elapsedTime = SystemClock.elapsedRealtime() - chronometer.getBase();
        SharedPreferences.Editor edit = sharedPref.edit();

        edit.putString("BUTTONS_STATE", lastButtonsState.toString());
        edit.putInt("MOVES_COUNT", movesCount);
        edit.putLong("TIME", elapsedTime);
        edit.apply();
        super.onStop();
    }

    private void addValuesToList() {
        for (int i = 1; i < 16; i++) {
            buttonValues.add(String.valueOf(i));
        }

        buttonValues.add("");
    }

    private void shuffleValues() {
        //int count = 0;

        Collections.shuffle(buttonValues);

        while (!isSolvable()) {
            //count++;
            Collections.shuffle(buttonValues);
        }

        //Toast.makeText(this, String.valueOf(count), Toast.LENGTH_SHORT).show();
    }

    private void initButtons() {
        onMusic.setOnClickListener(v -> {
            mediaPlayer.pause();
            onMusic.setVisibility(View.INVISIBLE);
            offMusic.setVisibility(View.VISIBLE);
        });

        offMusic.setOnClickListener(v -> {
            mediaPlayer.start();
            offMusic.setVisibility(View.INVISIBLE);
            onMusic.setVisibility(View.VISIBLE);
        });

        RelativeLayout relativeLayout = findViewById(R.id.puzzleButtons);
        for (int i = 0; i < relativeLayout.getChildCount(); i++) {
            Button currentButton = (Button) relativeLayout.getChildAt(i);

            int currentX = i / 4;
            int currentY = i % 4;

            buttons[currentX][currentY] = currentButton;
            currentButton.setOnClickListener(this::onClickButton);
            currentButton.setTag(new Point(currentX, currentY));
            currentButton.setBackgroundResource(R.drawable.puzzle_button_shape);
        }
    }

    private void putValueAndBackgroundToButtons() {
        x = buttonValues.indexOf("") / 4;
        y = buttonValues.indexOf("") % 4;
        for (int i = 0; i < 16; i++) {

            int currentX = i / 4;
            int currentY = i % 4;

            buttons[currentX][currentY].setText(buttonValues.get(i));
            buttons[currentX][currentY].setBackgroundResource(R.drawable.puzzle_button_shape);
        }

        buttons[x][y].setBackgroundResource(R.drawable.empty_puzzle_button);
    }

    private void onClickButton(View view) {

        Button button = (Button) view;
        Point currentPoint = (Point) button.getTag();
        boolean canMove = Math.abs(x - currentPoint.getX()) + Math.abs(y - currentPoint.getY()) == 1;

        if (canMove) {
            movesCount++;

            buttons[x][y].setText(button.getText());
            buttons[x][y].setBackgroundResource(R.drawable.puzzle_button_shape);

            x = currentPoint.getX();
            y = currentPoint.getY();

            button.setText("");

            button.setBackgroundResource(R.drawable.empty_puzzle_button);

            if (x == 3 && y == 3) {
                if (userWinGame())
                // TODO after user win game and skip dialog, restart game;
                {
                    myShared.save(movesCount);
                    myShared.saveMoveCount(movesCount);
                    showWinDialog();
                }
            }

            TextView moveText = findViewById(R.id.movesCountText);
            moveText.setText(String.valueOf(movesCount));

            TextView bestText = findViewById(R.id.bestMoves);
            if (myShared.getBest() != Integer.MAX_VALUE) {
                bestText.setText(String.valueOf(myShared.getBest()));
            }
        }
    }

    private void showWinDialog() {
        int currentOrientation = getResources().getConfiguration().orientation;

        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
        else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.win_dialog);

        dialog.setOnCancelListener(dialog1 -> {
            restartGame();
            chronometer.setBase(SystemClock.elapsedRealtime());
            dialog.cancel();
        });

        dialog.show();
        dialog.setCancelable(false);

        chronometer.stop();

        TextView winMovesCount = dialog.findViewById(R.id.dialogMovesCount);
        winMovesCount.setText(String.valueOf(movesCount));

        Chronometer dialogWinTime = dialog.findViewById(R.id.dialogChronometer);
        dialogWinTime.setBase(chronometer.getBase());

        AppCompatTextView homeButton = dialog.findViewById(R.id.dialogHomeButton);
        homeButton.setOnClickListener(v -> {
            restartGame();
            chronometer.setBase(SystemClock.elapsedRealtime());
            dialog.cancel();
            finish();
        });

        AppCompatTextView restartButton = dialog.findViewById(R.id.dialogRestartButton);
        restartButton.setOnClickListener(v -> {
            restartGame();
            chronometer.setBase(SystemClock.elapsedRealtime());
            dialog.cancel();
        });
    }

    private boolean userWinGame() {

        for (int i = 0; i < 15; i++) {
            int currentX = i / 4;
            int currentY = i % 4;

            String currentButton = buttons[currentX][currentY].getText().toString();
            if (!currentButton.equals(String.valueOf(i + 1))) {
                return false;
            }
        }

        return true;
    }

    public void onClickRestart(View view) {
        restartGame();
        chronometer.setBase(SystemClock.elapsedRealtime());
    }

    public void restartGame() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        movesCount = 0;

        chronometer.start();

        TextView movesText = findViewById(R.id.movesCountText);
        movesText.setText("0");

        shuffleValues();
        initButtons();
        putValueAndBackgroundToButtons();
    }

    private int getInvCount(Integer[] arr) {
        int N = 4;
        int inv_count = 0;
        for (int i = 0; i < N * N - 1; i++) {
            for (int j = i + 1; j < N * N; j++) {
                if (arr[j] != 0 && arr[i] != 0
                        && arr[i] > arr[j])
                    inv_count++;
            }
        }
        return inv_count;
    }


    private boolean isSolvable() {
        // geek for geeks
        Integer[] arr = new Integer[16];

        int pos = -1;
        for (int i = 0; i < arr.length; i++) {
            if (buttonValues.get(i).equals("")) {
                arr[i] = 0;
                pos = 4 - i / 4;
            } else {
                arr[i] = Integer.valueOf(buttonValues.get(i));
            }
        }
        int invCount = getInvCount(arr);


        Log.d("TTT", pos + " " + invCount);
        if (pos % 2 == 1) {
            return invCount % 2 == 0;
        } else {
            return invCount % 2 == 1;
        }
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("movesCount", movesCount);
        boolean isPlaying = onMusic.getVisibility() == View.VISIBLE;
        if (isPlaying) {
            outState.putInt("mediaCurrentPosition", mediaPlayer.getCurrentPosition());
        }
        outState.putBoolean("isPlaying", isPlaying);

        outState.putLong("time", chronometer.getBase());

        ArrayList<Integer> list = new ArrayList<>();

        RelativeLayout relativeLayout = findViewById(R.id.puzzleButtons);
        for (int i = 0; i < relativeLayout.getChildCount(); i++) {
            String s = String.valueOf(((TextView) relativeLayout.getChildAt(i)).getText());

            if (s.equals("")) {
                list.add(0);
            } else {
                list.add(Integer.valueOf(s));
            }
        }

        outState.putIntegerArrayList("buttonValues", list);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // set music
        boolean isPlaying = savedInstanceState.getBoolean("isPlaying");
        if (isPlaying) {
            int l = savedInstanceState.getInt("mediaCurrentPosition");
            mediaPlayer.seekTo(l);
            mediaPlayer.start();

            onMusic.setVisibility(View.VISIBLE);
            offMusic.setVisibility(View.INVISIBLE);
        }

        // set moves count and time
        int count = savedInstanceState.getInt("movesCount", 0);
        movesCount = count;
        long time = savedInstanceState.getLong("time");
        chronometer.setBase(time);

        TextView moveText = findViewById(R.id.movesCountText);
        moveText.setText(String.valueOf(count));

        // set button values
        ArrayList<Integer> list = savedInstanceState.getIntegerArrayList("buttonValues");
        if (list != null)
            for (int i = 0; i < 16; i++) {
                if (list.get(i) == 0) {
                    buttonValues.set(i, "");
                } else {
                    buttonValues.set(i, list.get(i).toString());
                }
            }
        putValueAndBackgroundToButtons();
    }
}

