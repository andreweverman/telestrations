package com.example.telestrations.utils;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.example.telestrations.model.Game;
import com.example.telestrations.model.Notepad;
import com.example.telestrations.model.Player;
import com.example.telestrations.model.SubmitMessage;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DatabaseUtil {
    public static DatabaseReference getDatabaseRootRef() {
        return databaseRootRef;
    }

    private static DatabaseReference databaseRootRef;

    private static List<Pair<DatabaseReference, ChildEventListener>> childEventListenerPairs;
    private static List<Pair<DatabaseReference, ValueEventListener>> valueEventListenerPairs;

    static {
        databaseRootRef = FirebaseDatabase.getInstance().getReference();
        childEventListenerPairs = new ArrayList<>();
        valueEventListenerPairs = new ArrayList<>();
    }

    public static void setUpGameEventListeners(final Game game) {
        //Reference to list of players for game in database
        DatabaseReference playersListRef = databaseRootRef
                .child(Constants.GAMES_CONTAINER_JSON_KEY)
                .child(game.getRoomCode())
                .child(Constants.PLAYERS_CONTAINER_JSON_KEY);

        //Reference to game status
        DatabaseReference gameStatusRef = databaseRootRef
                .child(Constants.GAMES_CONTAINER_JSON_KEY)
                .child(game.getRoomCode())
                .child(Constants.GAME_STATUS_JSON_KEY);

        /* Firebase event listeners are triggered once immediately, and then each time the
        event fires. So it should add each player to the local Game.Players list right
         after the 'addChildEventListener' call. */

        //Create listener to handle players leaving and joining the game
        ChildEventListener playersEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                game.addPlayer(dataSnapshot.getValue(Player.class));

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                game.removePlayer(dataSnapshot.getValue(Player.class));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        playersListRef.addChildEventListener(playersEventListener);
        childEventListenerPairs.add(new Pair<>(playersListRef, playersEventListener));

        if (!game.isHost()) {
            //Create listener to handle change in game state (LOBBY -> IN PROGRESS)
            ValueEventListener gameStatusListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Constants.GAME_STATE state = dataSnapshot.getValue(Constants.GAME_STATE.class);

                    //When the game state is changed to IN_PROGRESS, the game has just begun, so
                    // call the appropriate method.
                    if (state.equals(Constants.GAME_STATE.IN_PROGRESS)) {
                        Log.d("DB", "GAME STARTED");
                        game.beginGame();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            gameStatusRef.addValueEventListener(gameStatusListener);
            valueEventListenerPairs.add(new Pair<>(gameStatusRef, gameStatusListener));
        }
    }

    public static void takeDownGameEventListeners() {
        //TODO: currently not removing them from the Lists, change from foreach loop so we can modify?
        for (Pair<DatabaseReference, ChildEventListener> pair : childEventListenerPairs) {
            pair.first.removeEventListener(pair.second);
        }

        for (Pair<DatabaseReference, ValueEventListener> pair : valueEventListenerPairs) {
            pair.first.removeEventListener(pair.second);
        }
    }

    public static void instantiateGameInDBAndAddLocalPlayer(final ValueCallback<String> changeFragment, final ValueCallback<String> returnRoomCode, final Context context) {
        //First generate a room code and create the room in the DB
        databaseRootRef.child(Constants.ROOM_CODES_CONTAINER_JSON_KEY)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get all the current room codes
                        List<String> existingRoomCodes = new ArrayList<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            existingRoomCodes.add(ds.getKey());
                        }


                        //Now create a unique room code.
                        Random rand = new Random();
                        String newRoomCode;
                        do {
                            newRoomCode = "";
                            for (int i = 0; i < Constants.ROOM_CODE_LENGTH; i++) {
                                int randomIndex = rand.nextInt(Constants.ALPHA_NUMERIC.length());
                                newRoomCode += Constants.ALPHA_NUMERIC.substring(randomIndex, randomIndex + 1);
                            }
                        } while (existingRoomCodes.contains(newRoomCode));

                        //We now have a unique room code, create required data in the database.

                        //Add the new room code to the room codes list
                        databaseRootRef
                                .child(Constants.ROOM_CODES_CONTAINER_JSON_KEY)
                                .child(newRoomCode)
                                .setValue(Constants.EMPTY_STR);

                        //Create the game from the room code
                        DatabaseReference game = databaseRootRef
                                .child(Constants.GAMES_CONTAINER_JSON_KEY)
                                .child(newRoomCode);

                        //Set the game status
                        game.child(Constants.GAME_STATUS_JSON_KEY)
                                .setValue(Constants.GAME_STATE.LOBBY);

                        //Add the creating/host player to the game
                        addLocalPlayerToGameInDB(newRoomCode, context);

                        changeFragment.onReceiveValue(newRoomCode);
                        returnRoomCode.onReceiveValue(newRoomCode);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public static void removeSelfOrDeleteGameIfLastPlayer(final String roomCode, final Context context) {
        //Get players
        databaseRootRef.child(Constants.GAMES_CONTAINER_JSON_KEY)
                .child(roomCode)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Constants.GAME_STATE currState = dataSnapshot
                                .child(Constants.GAME_STATUS_JSON_KEY)
                                .getValue(Constants.GAME_STATE.class);

                        //Only remove if the game is not in progress
                        if (currState != Constants.GAME_STATE.IN_PROGRESS) {
                            //Check how many players are left.
                            // If 1, then delete game (we are last player)
                            if (dataSnapshot.child(Constants.PLAYERS_CONTAINER_JSON_KEY).getChildrenCount() == 1) {
                                //Delete the entire game from "Games"
                                databaseRootRef.child(Constants.GAMES_CONTAINER_JSON_KEY)
                                        .child(roomCode)
                                        .removeValue();

                                //Delete the room code from "Room Codes" list
                                databaseRootRef.child(Constants.ROOM_CODES_CONTAINER_JSON_KEY)
                                        .child(roomCode)
                                        .removeValue();
                            } else {
                                //otherwise, just remove the local player from the game
                                Player localPlayer = LocalPlayer.getLocalPlayer(context);

                                databaseRootRef.child(Constants.GAMES_CONTAINER_JSON_KEY)
                                        .child(roomCode)
                                        .child(Constants.PLAYERS_CONTAINER_JSON_KEY)
                                        .child(localPlayer.getId())
                                        .removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public static void addLocalPlayerToGameInDB(final String roomCode, Context context) {
        Player localPlayer = LocalPlayer.getLocalPlayer(context);

        databaseRootRef
                .child(Constants.GAMES_CONTAINER_JSON_KEY)
                .child(roomCode)
                .child(Constants.PLAYERS_CONTAINER_JSON_KEY)
                .child(localPlayer.getId())
                .setValue(localPlayer);
    }

    public static void createInboundQueue(String roomCode, String localID, final ValueCallback<SubmitMessage> onNewArrival) {
        DatabaseReference localPlayer = databaseRootRef
                .child(Constants.GAMES_CONTAINER_JSON_KEY)
                .child(roomCode)
                .child(Constants.PLAYERS_CONTAINER_JSON_KEY)
                .child(localID);

        localPlayer
                .child(Constants.INBOUND_QUEUE_CONTAINER_KEY)
                .setValue(Constants.EMPTY_STR);

        DatabaseReference queue = localPlayer.child(Constants.INBOUND_QUEUE_CONTAINER_KEY);

        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                SubmitMessage value = null;

                try {
                    value = dataSnapshot.getValue(SubmitMessage.class);
                } catch (Exception e) {
                    Log.d("DB", e.getMessage());
                }

                Log.d("QUEUE", "RECEIVING: " + value.toString());
                onNewArrival.onReceiveValue(value);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        childEventListenerPairs.add(new Pair<>(queue, listener));

        localPlayer
                .child(Constants.INBOUND_QUEUE_CONTAINER_KEY)
                .addChildEventListener(listener);
    }

    public static void removeMessageFromInboundQueue(SubmitMessage message, String localPlayerID, String roomCode){
        databaseRootRef.child(Constants.GAMES_CONTAINER_JSON_KEY)
                .child(roomCode)
                .child(Constants.PLAYERS_CONTAINER_JSON_KEY)
                .child(localPlayerID)
                .child(Constants.INBOUND_QUEUE_CONTAINER_KEY)
                .child(Integer.toString(message.getTurnCount()))
                .removeValue();
    }

    public static void updateGameStatus(final String roomCode, Constants.GAME_STATE newState) {
        databaseRootRef.child(Constants.GAMES_CONTAINER_JSON_KEY)
                .child(roomCode)
                .child(Constants.GAME_STATUS_JSON_KEY)
                .setValue(newState);
    }

    public static void checkIfAbleToJoinGame(final String roomCode, final String localPlayerID, final ValueCallback<Constants.JOIN_ATTEMPT_RESPONSE> onLobbyVerified) {

        databaseRootRef.child(Constants.GAMES_CONTAINER_JSON_KEY)
                .child(roomCode)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Constants.JOIN_ATTEMPT_RESPONSE response = Constants.JOIN_ATTEMPT_RESPONSE.VALID_LOBBY;

                        //Make sure that the game exists
                        if (!dataSnapshot.exists()) {
                            response = Constants.JOIN_ATTEMPT_RESPONSE.GAME_NOT_CREATED;

                            //Check if they are already in the players list
                        } else if (dataSnapshot.child(Constants.PLAYERS_CONTAINER_JSON_KEY).hasChild(localPlayerID)) {
                            //TODO: Do something about this. This is allowed, but we need to
                            // handle it differently (i.e. don't re-add them to the database.)

                            //Make sure its in lobby state (not in progress)
                        } else if (!dataSnapshot.child(Constants.GAME_STATUS_JSON_KEY)
                                .getValue().equals(Constants.GAME_STATE.LOBBY.toString())) {

                            response = Constants.JOIN_ATTEMPT_RESPONSE.GAME_IN_PROGRESS;

                            //Make sure it isn't full of players already
                        } else if (dataSnapshot.child(Constants.PLAYERS_CONTAINER_JSON_KEY)
                                .getChildrenCount() > Constants.MAX_LOBBY_SIZE) {

                            response = Constants.JOIN_ATTEMPT_RESPONSE.LOBBY_FULL;
                        }

                        onLobbyVerified.onReceiveValue(response);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public static void getNotepad(final String roomCode, final String myID,final List<Player> playerIDs,  final ValueCallback<Notepad> callback) {
        databaseRootRef
                .child(Constants.GAMES_CONTAINER_JSON_KEY)
                .child(roomCode)
                .child(Constants.NOTEPAD_CONTAINER_KEY)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Notepad myNotepad = null;

                        for(Player player: playerIDs) {
                            DataSnapshot playerLevel = dataSnapshot.child(player.getId());
                            String id = playerLevel.getKey();
                            if(myID.equals(id)){
                                List<String> payloads = new ArrayList<>();
                                String name = player.getDisplayName();
                                for (int i=0; i<playerIDs.size();i++){
                                    payloads.add(playerLevel.child(Integer.toString(i)).getValue().toString());
                                }
                                myNotepad= new Notepad(id, name,payloads);
                            }
                        }
                        callback.onReceiveValue(myNotepad);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public static void submitDrawingOrPhrase(final String roomCode, final SubmitMessage messageToSend, String targetPlayerID) {
        //Push data to the notepad repo/data store
        databaseRootRef
                .child(Constants.GAMES_CONTAINER_JSON_KEY)
                .child(roomCode)
                .child(Constants.NOTEPAD_CONTAINER_KEY)
                .child(messageToSend.getOrigPlayerID())
                .child(Integer.toString(messageToSend.getTurnCount()))
                .setValue(messageToSend.getPayload());

        //transmit the info to the next player's queue
        databaseRootRef
                .child(Constants.GAMES_CONTAINER_JSON_KEY)
                .child(roomCode)
                .child(Constants.PLAYERS_CONTAINER_JSON_KEY)
                .child(targetPlayerID)
                .child(Constants.INBOUND_QUEUE_CONTAINER_KEY)
                .child(Integer.toString(messageToSend.getTurnCount()))
                .setValue(messageToSend);
    }

    public static void createPlayerOrdering(String roomCode, List<String> playerList) {
        databaseRootRef
                .child(Constants.GAMES_CONTAINER_JSON_KEY)
                .child(roomCode)
                .child(Constants.ORDER_CONTAINER_JSON_KEY)
                .setValue(playerList);
    }

    public static void getPlayerOrdering(String roomCode, final ValueCallback<List<String>> callback, final int playerCount) {
        databaseRootRef
                .child(Constants.GAMES_CONTAINER_JSON_KEY)
                .child(roomCode)
                .child(Constants.ORDER_CONTAINER_JSON_KEY)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        List<String> playerOrderIDs = new ArrayList<>();

                        for (int i = 0; i < playerCount; i++) {
                            playerOrderIDs.add(dataSnapshot.child(Integer.toString(i)).getValue().toString());
                        }

                        callback.onReceiveValue(playerOrderIDs);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }



}
