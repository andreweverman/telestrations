package com.example.telestrations.ui.drawing;

import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import com.example.telestrations.R;


public class DrawActivity extends FragmentActivity {
    private final String tag = "DrawActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        how to dynamically add fragment
//
//        FragmentManager manager = getSupportFragmentManager();
//        FragmentTransaction transaction = manager.beginTransaction();
//        transaction.replace(R.id.draw_fragment, new DrawFragment()).commit();

        setContentView(R.layout.activity_draw);
        Log.d(tag,"onCreate has been run");

    }

}
