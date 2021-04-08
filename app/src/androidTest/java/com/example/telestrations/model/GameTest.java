package com.example.telestrations.model;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.telestrations.utils.EmptyCallback;
import com.example.telestrations.utils.ValueCallback;

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
public class GameTest {


    @Test
    public void createGameHost() throws InterruptedException {
        final Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        final CountDownLatch latch = new CountDownLatch(2);

        EmptyCallback emptyCallback = new EmptyCallback() {
            @Override
            public void onCallback() {
                assertEquals(true, true);
                latch.countDown();
            }
        };

        ValueCallback<String> valueCallback = new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String o) {
//                checking we got a valid room code
                assertEquals(o.length(), 3);
                latch.countDown();
            }
        };

        Game game = Game.instantiateGameAsHost(appContext, emptyCallback, valueCallback);
        game.setChangeListener(emptyCallback);

        latch.await();
    }



}
