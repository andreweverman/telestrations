package com.example.telestrations.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;

import com.example.telestrations.ArrayAdapterWithIcon;
import com.example.telestrations.R;
import com.example.telestrations.model.Game;
import com.example.telestrations.model.Player;
import com.example.telestrations.utils.EmptyCallback;
import com.example.telestrations.utils.Constants;
import com.example.telestrations.utils.ValueCallback;

import java.io.Serializable;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class LobbyActivity extends FragmentActivity  implements Serializable {

    private Game activeGame;
    public boolean isHost;
    private Integer finishCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        finishCode=1;
        setContentView(R.layout.activity_lobby);

        // passing loading
        Bundle loadingBundle = new Bundle();
        loadingBundle.putString(Constants.LOADING_MESSAGE, "Creating Lobby...");

        LoadingFragment loadingFragment = new LoadingFragment();
        loadingFragment.setArguments(loadingBundle);
        //Set the loading fragment to display a loading symbol until lobby fragment is ready.
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.activeFragment, loadingFragment).commit();

        isHost = getIntent().getBooleanExtra(Constants.BUNDLE_IS_HOST, false);
        //Code block to perform AFTER lobby is created in DB (callback)
        // Callback to change from LoadingFragment to LobbyFragment, only after lobby has been created in the DB.
        ValueCallback<String> onLobbyCreated = new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String roomCode) {
                //Start up the lobby fragment, pass in room code so it can display.
                LobbyFragment lobbyFrag = new LobbyFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.BUNDLE_ROOM_CODE, roomCode);
                bundle.putBoolean(Constants.BUNDLE_IS_HOST, activeGame.isHost());
                lobbyFrag.setArguments(bundle);

                //End the loading symbol fragment, and show the lobby fragment.
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.activeFragment, lobbyFrag).commit();
            }
        };

        //Once the game has been started by the host, execute this callback to change to the GameActivity
        EmptyCallback onGameStarted = new EmptyCallback() {
            @Override
            public void onCallback() {

                Intent intent = new Intent(LobbyActivity.this, GameActivity.class);
                startActivityForResult(intent, finishCode);

            }
        };
        boolean isHost = getIntent().getBooleanExtra(Constants.BUNDLE_IS_HOST, false);

        //Diverge based on whether or not the local player is the one creating the game or not.
        if(isHost){
            //Below Game CTOR will perform setup to create game in DB
            activeGame = Game.instantiateGameAsHost(getApplicationContext(), onGameStarted, onLobbyCreated);
        } else{
            //We are not the host, game is already created
            String roomCode = getIntent().getStringExtra(Constants.BUNDLE_ROOM_CODE);

            activeGame = Game.instantiateGameAsNonHost(getApplicationContext(), onGameStarted, roomCode);
            onLobbyCreated.onReceiveValue(roomCode);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        activeGame.setChangeListener(new EmptyCallback() {
            @Override
            public void onCallback() {
                updatePlayerList();
            }
        });
    }

    //Executed on the host device, when they click the start button. Non-hosts call Game.beginGame
    // based on an event listener
    public void startGame(){
        Log.d("test", "started by host");
        activeGame.beginGame();
    }

    @Override
    protected void onDestroy(){
        Log.d("lobby", "ondestroy");
        activeGame.exitGame(getApplicationContext());
        super.onDestroy();
    }

    private void updatePlayerList(){
        FragmentManager fragmentManager = getSupportFragmentManager();
//        if we dont have this then the fragment we want to update can be null
        fragmentManager.executePendingTransactions();


//        creating the adapter that will get passes to the fragment
//        to put in its ListView element

        final Field[] fields =  R.drawable.class.getDeclaredFields();
        final android.R.drawable drawableResources = new android.R.drawable();
        List<Integer> iconList = new ArrayList<>();
        List<String> playerNameList = new ArrayList<>();
        List<Integer> playerColors = new ArrayList<>();

        List players = activeGame.getPlayers();

        for (int i = 0; i < players.size(); i++) {
            try {
                Player currentPlayer = (Player) players.get(i);

//                need the file path without the extension
                String filePath = currentPlayer.getImageFileName();


//                finds the icon drawable from the path
                int resID = getResources().getIdentifier(filePath,
                        "drawable", getPackageName());

//                addint the file icon and the name to the lists
                iconList.add(resID);
                playerNameList.add(currentPlayer.getDisplayName());
                playerColors.add(currentPlayer.getNameColor());
            } catch (Exception e) {

            }
        }


        ListAdapter adapter = new ArrayAdapterWithIcon(this, playerNameList , iconList,playerColors);

        LobbyFragment lobbyFragment =   (LobbyFragment)fragmentManager.findFragmentById(R.id.activeFragment);
        lobbyFragment.updatePlayerListUI(adapter);
    }


    //when the gameactivity comes back around, this will then put us back on the main activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==finishCode){
            finish();
        }
    }
}
