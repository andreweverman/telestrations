package com.example.telestrations.model;

import android.content.Context;
import android.util.Log;

import com.example.telestrations.utils.EmptyCallback;
import com.example.telestrations.utils.Constants;
import com.example.telestrations.utils.DatabaseUtil;
import com.example.telestrations.utils.LocalPlayer;
import com.example.telestrations.utils.ValueCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Game implements Serializable {

    private static Game game = null;

    private String roomCode;
    private List<Player> players;
    private boolean isHost;
    private EmptyCallback onGameStart;
    private List<String> playerOrderIDs;

    private EmptyCallback onPlayerListUpdate;
    private EmptyCallback onNewSubmitMessage;

    private String nextPlayerID;
    private String previousPlayerName;
    private String localPlayerID;

    private int turnCount;

    private Queue<SubmitMessage> localMessageQueue;

    //CTOR FOR HOST
    private Game(Context context, final EmptyCallback onGameStarted, ValueCallback<String> fragmentChangeCallback){
        this.players = new ArrayList<>();
        this.isHost = true;
        this.onGameStart = onGameStarted; //store for later in beingGame()
        this.localPlayerID = LocalPlayer.getLocalPlayer(context).getId();

        //Perform some 'Game' class setup after lobby creation
        ValueCallback<String> onLobbyCreated = new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String roomCode) {
                Game.this.roomCode = roomCode;

                //All synchronization is performed by each device having event listeners on the DB.
                DatabaseUtil.setUpGameEventListeners(Game.this);
            }
        };

        // Set into motion the creation of the game
        DatabaseUtil.instantiateGameInDBAndAddLocalPlayer(fragmentChangeCallback, onLobbyCreated, context);
    }

    //CTOR FOR NON-HOST
    private Game(Context context, EmptyCallback onGameStarted, String roomCode){
        this.players = new ArrayList<>();
        this.isHost = false;
        this.onGameStart = onGameStarted; //store for later in beingGame()
        this.roomCode = roomCode;
        this.localPlayerID = LocalPlayer.getLocalPlayer(context).getId();

        //Game is already created by another player (host), add yourself to the game
        DatabaseUtil.addLocalPlayerToGameInDB(roomCode, context);

        //All synchronization is performed by each device having event listeners on the DB.
        DatabaseUtil.setUpGameEventListeners(this);
    }

    public static Game instantiateGameAsHost(Context context, final EmptyCallback onGameStarted, ValueCallback<String> fragmentChangeCallback){
        game = new Game(context, onGameStarted, fragmentChangeCallback);
        return game;
    }

    public static Game instantiateGameAsNonHost(Context context, EmptyCallback onGameStarted, String roomCode){
        game = new Game(context, onGameStarted, roomCode);
        return game;
    }

    public static Game getGameInstance(){
        if(game == null){
            throw new RuntimeException("AHHHHHHHHH, called before game instantiated.");
        }

        return game;
    }

    public void setChangeListener(EmptyCallback playerListUpdate){
        this.onPlayerListUpdate = playerListUpdate;
    }

    public void setOnReceiveMessageCallback(EmptyCallback callback){
        this.onNewSubmitMessage = callback;
    }

    public void exitGame(Context context) {
        Log.d("game", "Exiting game: " + this.roomCode);

        //On game exit, either remove the local player from the game, or just delete game if the
        // local player is the only one left.
        DatabaseUtil.removeSelfOrDeleteGameIfLastPlayer(this.roomCode, context);

        //Remove event listeners, otherwise they won't go away til app is destroyed ?
        DatabaseUtil.takeDownGameEventListeners();
    }

    public void beginGame() {
        //Trigger the LobbyActivity to load the Game Activity
        this.onGameStart.onCallback();

        ValueCallback<List<String>> onPlayerOrderRead = new ValueCallback<List<String>>() {
            @Override
            public void onReceiveValue(List<String> playerID) {
                Game.this.playerOrderIDs=playerID;
                int count = Game.this.playerOrderIDs.size();
                int index = playerID.indexOf(Game.this.localPlayerID);
                Game.this.nextPlayerID = Game.this.playerOrderIDs.get((index + 1) % count);
                final String prevPlayerID = Game.this.playerOrderIDs.get(((index - 1) + count)  % count);

                for(Player player: Game.this.players){
                    if(player.getId().equals(prevPlayerID)){
                        Game.this.previousPlayerName = player.getDisplayName();

                        break;
                    }
                }
            }
        };

        if(this.isHost){
            Collections.shuffle(this.players);
            List<String> temp = new ArrayList<>();
            for (Player player: this.players){
                temp.add(player.getId());
            }

            DatabaseUtil.createPlayerOrdering(this.roomCode, temp);
            DatabaseUtil.updateGameStatus(this.roomCode, Constants.GAME_STATE.IN_PROGRESS);

            onPlayerOrderRead.onReceiveValue(temp);
        } else {
            DatabaseUtil.getPlayerOrdering(this.roomCode, onPlayerOrderRead, this.players.size());
        }

        ValueCallback<SubmitMessage> onNewArrival = new ValueCallback<SubmitMessage>() {
            @Override
            public void onReceiveValue(SubmitMessage message) {
                Game.this.localMessageQueue.add(message);
                DatabaseUtil.removeMessageFromInboundQueue(message, Game.this.localPlayerID, Game.this.roomCode);

                //TODO: Trigger something
                Game.this.onNewSubmitMessage.onCallback();
            }
        };

        this.localMessageQueue = new LinkedList<>();
        this.turnCount = 0;
        DatabaseUtil.createInboundQueue(this.roomCode, this.localPlayerID, onNewArrival);
    }

    public void removePlayer(Player player){
        this.players.remove(player);
        this.onPlayerListUpdate.onCallback();
    }

    public void addPlayer(Player player){
        this.players.add(player);
        this.onPlayerListUpdate.onCallback();
    }

    public boolean isHost(){
        return this.isHost;
    }

    public List<Player> getPlayers(){
        return this.players;
    }

    public String getRoomCode(){
        return this.roomCode;
    }

    public int getTurnCount() {
        return this.turnCount;
    }

    public void incrementTurnCount() {
        this.turnCount++;
    }

    public String getOriginalPlayerIDFromTurnCount(){
        int size = this.playerOrderIDs.size();

        int ourIndexInOrder = this.playerOrderIDs.indexOf(this.localPlayerID);

        return this.playerOrderIDs.get((ourIndexInOrder - this.turnCount + size) % size);
    }

    public boolean haveMessagesReady(){
        return !localMessageQueue.isEmpty();
    }

    public SubmitMessage getNextMessage(){
        return localMessageQueue.remove();
    }

    public String getNextPlayerID() {
        return game.nextPlayerID;
    }

    public String getPreviousPlayerName() {
        return previousPlayerName;
    }
}
