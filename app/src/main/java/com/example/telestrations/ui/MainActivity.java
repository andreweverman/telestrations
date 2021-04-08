package com.example.telestrations.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.telestrations.R;
import com.example.telestrations.model.Player;
import com.example.telestrations.ui.drawing.DrawActivity;
import com.example.telestrations.utils.Constants;
import com.example.telestrations.utils.LocalPlayer;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MAIN";
    ImageView profileImg;
    TextView playerName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MAIN","onCreate has been run");

        Context c = getApplicationContext();
        final Player player = LocalPlayer.getLocalPlayer(getApplicationContext());

        //FOR AVATAR
        profileImg = (ImageView) findViewById(R.id.playerIcon);
        String avatarName = player.getImageFileName();
        int id = c.getResources().getIdentifier("drawable/" + avatarName, null, c.getPackageName());
        profileImg.setImageResource(id);

        //FOR PLAYER NAME & COLOR
        playerName = (TextView) findViewById(R.id.playerName);
        String playerNameFromFile = player.getDisplayName();
        int playerNameColorFromFile = player.getNameColor();
        playerName.setTextColor(playerNameColorFromFile);
        playerName.setText(playerNameFromFile);
    }

    public static int getImageId(Context context, String imageName){
        return context.getResources().getIdentifier("drawable/" + imageName,null, context.getPackageName());
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart has been run");

        Context c = getApplicationContext();
        final Player player = LocalPlayer.getLocalPlayer(getApplicationContext());
        //Toast.makeText(this, player.toString(), Toast.LENGTH_LONG).show();

        //FOR AVATAR
        profileImg = (ImageView) findViewById(R.id.playerIcon);
        String avatarName = player.getImageFileName();
        int id = c.getResources().getIdentifier("drawable/" + avatarName, null, c.getPackageName());
        profileImg.setImageResource(id);

        //FOR PLAYER NAME & COLOR
        playerName = (TextView) findViewById(R.id.playerName);
        String playerNameFromFile = player.getDisplayName();
        int playerNameColorFromFile = player.getNameColor();
        playerName.setText(playerNameFromFile);
        playerName.setTextColor(playerNameColorFromFile);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MAIN","onPause has been run");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume has been run");

        Context c = getApplicationContext();
        final Player player = LocalPlayer.getLocalPlayer(getApplicationContext());
        //Toast.makeText(this, player.toString(), Toast.LENGTH_LONG).show();

        //FOR AVATAR
        profileImg = (ImageView) findViewById(R.id.playerIcon);
        String avatarName = player.getImageFileName();
        int id = c.getResources().getIdentifier("drawable/" + avatarName, null, c.getPackageName());
        profileImg.setImageResource(id);

        //FOR PLAYER NAME & COLOR
        playerName = (TextView) findViewById(R.id.playerName);
        String playerNameFromFile = player.getDisplayName();
        int playerNameColorFromFile = player.getNameColor();
        playerName.setText(playerNameFromFile);
        playerName.setTextColor(playerNameColorFromFile);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop has been run");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy has been run");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG,"onRestart has been run");
    }

    public void redirectToEditProfile(View view){
        //sets the view to the edit profile

        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
    }

    public void hostBtnOnClick(View view){
        Log.d(TAG,"Redirecting to host activity");

        //Check if they have an internet connection:
        //FROM: https://developer.android.com/training/monitoring-device-state/connectivity-status-type
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(isConnected){
            //Load the LobbyActivity, indicate in Bundle that we are hosting
            Intent intent = new Intent(this, LobbyActivity.class);
            intent.putExtra(Constants.BUNDLE_IS_HOST, true);

            startActivity(intent);
        } else {
            Toast.makeText(this, "Internet connection is required to host a game.", Toast.LENGTH_SHORT).show();
        }
    }

    public void joinBtnOnClick(View view) {
        Intent intent = new Intent(this, JoinGameActivity.class);
        startActivity(intent);
    }
}
