package com.example.telestrations.ui;

import android.content.Intent;
import android.os.Bundle;

import com.example.telestrations.R;
import com.example.telestrations.model.Game;
import com.example.telestrations.ui.drawing.DrawFragment;
import com.example.telestrations.utils.Constants;
import com.example.telestrations.utils.DatabaseUtil;
import com.example.telestrations.model.SubmitMessage;
import com.example.telestrations.utils.EmptyCallback;
import com.example.telestrations.utils.LocalPlayer;
import com.example.telestrations.utils.ValueCallback;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

public class GameActivity extends FragmentActivity {

    private Game activeGame;
    private boolean inLoadingFragment = false;

    private ValueCallback<String> onSubmit;
    private EmptyCallback onResultsNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        activeGame = Game.getGameInstance();

        //set the Game  callback to perform when a new submit message is received.
        activeGame.setOnReceiveMessageCallback(new EmptyCallback() {
            @Override
            public void onCallback() {
                if(inLoadingFragment){
                    inLoadingFragment = false;
                    changeFragment();
                }
            }
        });

        //What to perform on the very first submit button click in PhraseSelectFragment
        onSubmit = new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String payload) {
                String origPlayerID = GameActivity.this.activeGame.getOriginalPlayerIDFromTurnCount();
                SubmitMessage message = new SubmitMessage(origPlayerID, payload, activeGame.getTurnCount());

                DatabaseUtil.submitDrawingOrPhrase(activeGame.getRoomCode(), message, activeGame.getNextPlayerID());

                activeGame.incrementTurnCount();
                changeFragment();
            }
        };

        //when the player hits next on the results screen, go back to main
        onResultsNext = new EmptyCallback() {
            @Override
            public void onCallback() {
               finish();
            }
        };

        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_ROOM_CODE, activeGame.getRoomCode());
        bundle.putSerializable(Constants.BUNDLE_ON_SUBMITTED, onSubmit);

        PhraseSelectFragment phraseSelectFragment = new PhraseSelectFragment();
        phraseSelectFragment.setArguments(bundle);

        //Replace the fragment placeholder with the starting fragment.
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.gameActivityFragmentHolder, phraseSelectFragment).commit();
    }


    private void changeFragment(){
        if(activeGame.haveMessagesReady()){
            SubmitMessage msg = activeGame.getNextMessage();

            Fragment fragToLoad;
            Boolean goToResultFragment = false;
            Bundle bundle = new Bundle();

            if (activeGame.getTurnCount() == activeGame.getPlayers().size()){
                //player's notebook is done. lets have a look at it
                fragToLoad = new ResultsFragment();
                goToResultFragment=true;
            }
            else if(activeGame.getTurnCount() % 2 == 0){
                //received drawing, guess phrase
                fragToLoad = new GuessImageFragment();

            } else {
                //received phrase, draw a picture
                fragToLoad = new DrawFragment();
            }

            if (!goToResultFragment){
                bundle.putSerializable(Constants.BUNDLE_PAYLOAD, msg.getPayload());
                bundle.putSerializable(Constants.BUNDLE_ON_SUBMITTED, onSubmit);
            }
            else{
                bundle.putSerializable(Constants.BUNDLE_ON_SUBMITTED,onResultsNext);
//                TODO: bundle the lists that will be output in the results fragment
            }

            fragToLoad.setArguments(bundle);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.gameActivityFragmentHolder, fragToLoad).commit();
        } else{
            inLoadingFragment = true;


            String waitString = "Waiting on player: " + activeGame.getPreviousPlayerName();

            Bundle bundle = new Bundle();
            bundle.putString(Constants.LOADING_MESSAGE, waitString);

            //Create the loading Fragment
            LoadingFragment loadingFragment = new LoadingFragment();
            loadingFragment.setArguments(bundle);

            //Set the loading fragment to display a loading symbol until lobby fragment is ready.
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.gameActivityFragmentHolder, loadingFragment).commit();

        }
    }
}
