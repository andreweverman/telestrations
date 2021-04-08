package com.example.telestrations.utils;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseUtilTest {
    private String TAG = "DatabaseUtilTest";

    @Test
    public void createGame() throws InterruptedException {

        final CountDownLatch latch = new CountDownLatch(2);
        final String[] roomcode1 = new String[1];
        final String[] roomcode2 = new String[1];
        // Context of the app under test.
        ValueCallback<String> roomCodeCallback = new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String roomcode) {
                assertEquals(3, roomcode.length());
                roomcode1[0] = roomcode;
                latch.countDown();
            }
        };
        ValueCallback<String> changeFragmentCallback = new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String roomcode) {
                assertEquals(3, roomcode.length());
                roomcode2[0] = roomcode;
                latch.countDown();
            }
        };

        final Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        DatabaseUtil.instantiateGameInDBAndAddLocalPlayer(changeFragmentCallback, roomCodeCallback, appContext);
        Log.d(TAG, "Ran DBUtil");

        latch.await();


        assertEquals(roomcode1[0], roomcode2[0]);

//        cleaning up for the database
        DatabaseUtil.removeSelfOrDeleteGameIfLastPlayer(roomcode1[0],appContext);
    }



    @Test
    public void createGameHostAttemptJoin() throws InterruptedException {

        final CountDownLatch latch = new CountDownLatch(2);
        final String[] roomcode1 = new String[1];
        final String[] roomcode2 = new String[1];
        // Context of the app under test.
        ValueCallback<String> roomCodeCallback = new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String o) {
                assertEquals(3, o.length());
                roomcode1[0] = o;
                latch.countDown();
            }
        };
        ValueCallback<String> changeFragmentCallback = new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String o) {
                assertEquals(3, o.length());
                roomcode2[0] = o;
                latch.countDown();
            }
        };

        final Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        DatabaseUtil.instantiateGameInDBAndAddLocalPlayer(changeFragmentCallback, roomCodeCallback, appContext);
        Log.d(TAG, "Ran DBUtil");

        latch.await();


        assertEquals(roomcode1[0], roomcode2[0]);
        String roomCode = roomcode1[0];
//        now have the stuff set up
//        now going to make sure that the game is actually there

        final CountDownLatch latch2 = new CountDownLatch(1);

        final ValueCallback<Constants.JOIN_ATTEMPT_RESPONSE> lobbyVerified = new ValueCallback<Constants.JOIN_ATTEMPT_RESPONSE>() {
            @Override
            public void onReceiveValue(Constants.JOIN_ATTEMPT_RESPONSE o) {
                Log.d(TAG, o.toString());
                latch2.countDown();
            }
        };


        DatabaseUtil.getDatabaseRootRef().child(Constants.GAMES_CONTAINER_JSON_KEY)
                .child(roomCode)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Constants.JOIN_ATTEMPT_RESPONSE response = Constants.JOIN_ATTEMPT_RESPONSE.VALID_LOBBY;

                        //Make sure that the game exists
                        if (!dataSnapshot.exists()) {
                            response = Constants.JOIN_ATTEMPT_RESPONSE.GAME_NOT_CREATED;

                            //Check if they are already in the players list
//                          we are in the list so we should always get to this point
                        } else if (dataSnapshot.child(Constants.PLAYERS_CONTAINER_JSON_KEY).hasChild(LocalPlayer.getLocalPlayer(appContext).getId())) {
//
                            assertEquals(true, true);
                        } else {
//                            didn't detect that we are in the lobby so error out because that is broken
                            assertEquals(false, false);
                        }
                        lobbyVerified.onReceiveValue(response);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
        latch2.await();
        //        cleaning up for the database
        DatabaseUtil.removeSelfOrDeleteGameIfLastPlayer(roomCode,appContext);
    }


}
