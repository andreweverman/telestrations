package com.example.telestrations.utils;

import android.content.Context;
import android.util.Log;

import com.example.telestrations.model.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public abstract class LocalPlayer {

    private static final String USERDATA_FILE_NAME = "userdata.txt";

    //Singleton variable
    private static Player localPlayer;

    public static Player getLocalPlayer(Context context){
        //Implement singleton design pattern for storing the local player.
        if(localPlayer == null){
            File f = new File(context.getFilesDir(), USERDATA_FILE_NAME);

            if(f.exists() && f.length() > 0){
                Log.d("player", "reading from memory.");
                localPlayer = readLocalPlayerFromMemory(f);
            } else {
                Log.d("player", "instantiating new.");
                //First time running app, need to create local user and write to file
                localPlayer = instantiateNewLocalPlayerAndWriteToMemory(context);
            }
        }

        if(localPlayer == null){
            Log.e("player", "ERROR: null player returned in getLocalPlayer");
        }

        return localPlayer;
    }

    private static Player readLocalPlayerFromMemory(File file) {
        try {

            BufferedReader br = new BufferedReader(new FileReader(file));
            String playerName =  br.readLine();
            String playerID = br.readLine();
            String playerAvatar = br.readLine();
            String playerNameColor = br.readLine();
            Log.d("Player:", "\n"+playerName+"\n"+playerID+"\n"+playerAvatar+"\n"+playerNameColor);


            br.close();

            //         Player(displayname,   id,     imagefilename,       color               )
            return new Player(playerName, playerID, playerAvatar, Integer.parseInt(playerNameColor));
        } catch (FileNotFoundException e) {
            Log.e("player", "filenotfound: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("player", "ioexcep: " + e.getMessage());
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e){
            Log.e("player", "IOB" + e.getMessage());
            e.printStackTrace();
        } catch (NullPointerException e){
            Log.e("player", "Null pointer" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static void storeLocalPlayerDataToMemory(Context context){
        File f = new File(context.getFilesDir(), USERDATA_FILE_NAME);

        //Write to the file
        try {
            if(!f.exists()){
                f.createNewFile();
            }

            String writeStr = localPlayer.toString();

            Log.d("Player", " Store Local Player Data, Player:\n" + writeStr);

            FileWriter out = new FileWriter(f,false); // false, so overwrite if exists
            out.write(writeStr);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Player instantiateNewLocalPlayerAndWriteToMemory(Context context){
        Player newPlayer = new Player("Player",
                UUID.randomUUID().toString(),
                "avatar_stitch",
                -999999999);

        Log.d("player", "Created user: " + newPlayer.getId());

        localPlayer = newPlayer;

        storeLocalPlayerDataToMemory(context);

        return newPlayer;
    }
}
