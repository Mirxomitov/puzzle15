package com.example.mypuzzle15;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        ImageView backButton = findViewById(R.id.info_back);
        backButton.setOnClickListener(v -> {
            finish();
        });
    }
}