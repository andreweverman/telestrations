package com.example.telestrations.model;

import android.content.Context;

import androidx.appcompat.view.menu.ListMenuItemView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.telestrations.utils.EmptyCallback;
import com.example.telestrations.utils.ValueCallback;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class NotepadTest {


    @Test
    public void createNotebook() throws InterruptedException {
        String id = "1234";
        String payload = "test";
        int turnCount = 1;

        SubmitMessage message = new SubmitMessage(id, payload,turnCount);
        SubmitMessage submitMessage = new SubmitMessage(id, payload, turnCount);


        assertEquals(submitMessage.getOrigPlayerID().equals(id), true);
        assertEquals(submitMessage.getPayload().equals(payload), true);
        assertEquals(submitMessage.getTurnCount(), turnCount);

    }



}
