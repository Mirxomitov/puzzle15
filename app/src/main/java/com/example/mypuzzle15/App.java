package com.example.mypuzzle15;

import android.app.Application;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MyShared.init(this);
    }
}
