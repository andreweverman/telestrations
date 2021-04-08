package com.example.telestrations.model;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SubmitMessageTest {

    @Test
    public void createSubmitMessage() throws InterruptedException {
        String id = "1234";
        String payload = "test";
        int turnCount = 1;


        SubmitMessage submitMessage = new SubmitMessage(id, payload, turnCount);


        assertEquals(submitMessage.getOrigPlayerID().equals(id), true);
        assertEquals(submitMessage.getPayload().equals(payload), true);
        assertEquals(submitMessage.getTurnCount(), turnCount);



    }



}
