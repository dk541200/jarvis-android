package com.jarvis.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, JarvisService.class).setAction(JarvisService.ACTION_ON));
        finish();
    }
}
