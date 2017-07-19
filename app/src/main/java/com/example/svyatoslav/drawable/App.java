package com.example.svyatoslav.drawable;

import android.app.Application;
import com.facebook.FacebookSdk;
/**
 * Created by Svyatoslav on 19.07.2017.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
