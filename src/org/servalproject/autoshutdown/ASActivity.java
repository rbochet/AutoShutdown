package org.servalproject.autoshutdown;

import android.app.Activity;
import android.os.Bundle;

public class ASActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}