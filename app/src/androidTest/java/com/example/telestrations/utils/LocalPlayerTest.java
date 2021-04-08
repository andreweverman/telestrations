package com.example.telestrations.utils;

import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.telestrations.model.Player;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class LocalPlayerTest {
    private String TAG = "DatabaseUtilTest";

    @Test
    public void getLocalPlayer() throws InterruptedException {
//        bad thing to test but doing it anyways
        final Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Player player = LocalPlayer.getLocalPlayer(appContext);


        assertEquals(player.getDisplayName().getClass(), String.class);
        assertEquals(player.getId().getClass(), String.class);
        assertEquals(player.getImageFileName().getClass(), String.class);


    }


}
