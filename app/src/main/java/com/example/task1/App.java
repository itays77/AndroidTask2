package com.example.task1;

import android.app.Application;

import com.example.task1.Utilities.SharePreferencesManager;
import com.example.task1.Utilities.SignalManager;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SharePreferencesManager.init(this);
        SignalManager.init(this);
    }
}