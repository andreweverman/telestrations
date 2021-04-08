package com.example.telestrations.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.telestrations.R;
import com.example.telestrations.utils.Constants;
import com.example.telestrations.utils.DatabaseUtil;
import com.example.telestrations.utils.LocalPlayer;
import com.example.telestrations.utils.ValueCallback;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class JoinGameActivity extends AppCompatActivity {
    private Integer finishCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        finishCode=1;
        setContentView(R.layout.activity_join_game);

        //Set the max edit text length, and force uppercase
        EditText roomCode = (EditText) findViewById(R.id.roomCode);
        roomCode.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(Constants.ROOM_CODE_LENGTH),
                new InputFilter.AllCaps()});
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void attemptJoinGame(View view) {
        //Check if they have an internet connection:
        //FROM: https://developer.android.com/training/monitoring-device-state/connectivity-status-type
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(isConnected){
            final String roomCode = ((EditText) findViewById(R.id.roomCode)).getText().toString();

            //Code block to perform once we know if we are joining a valid game. Passed in to DB call below.
            ValueCallback<Constants.JOIN_ATTEMPT_RESPONSE> onLobbyVerified = new ValueCallback<Constants.JOIN_ATTEMPT_RESPONSE>() {
                @Override
                public void onReceiveValue(Constants.JOIN_ATTEMPT_RESPONSE response) {

                    Log.d("joinGame", "response: " + response);

                    //TODO: Display Toasts/dialogues/something if can't join lobby.
                    // Create and Retrieve them from strings.xml
                    switch (response) {
                        case VALID_LOBBY:
                            Intent intent = new Intent(JoinGameActivity.this, LobbyActivity.class);
                            intent.putExtra(Constants.BUNDLE_IS_HOST, false);
                            intent.putExtra(Constants.BUNDLE_ROOM_CODE, roomCode);

                            startActivityForResult(intent, finishCode);
                            break;
                        case LOBBY_FULL:
                            //Toast.makeText(getApplicationContext(),"Game is full!")
                            break;
                        case GAME_NOT_CREATED:

                            break;
                        case GAME_IN_PROGRESS:

                            break;

                        default:
                            Log.e("joinGame", "Invalid response received from DBUtil.");
                    }
                }
            };

            String localPlayerID = LocalPlayer.getLocalPlayer(getApplicationContext()).getId();

            //Make call to database
            DatabaseUtil.checkIfAbleToJoinGame(roomCode, localPlayerID, onLobbyVerified);
        } else {
            Toast.makeText(this, "Internet connection is required to join a game.", Toast.LENGTH_SHORT).show();
        }
    }

    //when the gameactivity comes back around, this will then put us back on the main activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == finishCode) {
            finish();
        }
    }
}
