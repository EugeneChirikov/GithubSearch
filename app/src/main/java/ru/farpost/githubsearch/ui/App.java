package ru.farpost.githubsearch.ui;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by eugene on 12/12/16.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }
}
