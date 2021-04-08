package com.example.telestrations.utils;

public final class Constants {
    public static final String GAMES_CONTAINER_JSON_KEY = "Games";
    public static final String ROOM_CODES_CONTAINER_JSON_KEY = "RoomCodes";
    public static final String PLAYERS_CONTAINER_JSON_KEY = "Players";
    public static final String ORDER_CONTAINER_JSON_KEY = "Order";
    public static final String GAME_STATUS_JSON_KEY = "GameStatus";
    public static final String NOTEPAD_CONTAINER_KEY = "Notepads";
    public static final String INBOUND_QUEUE_CONTAINER_KEY = "InboundQueue";

    public static final String ORIG_PLAYER_ID_KEY = "OrigPlayerID";
    public static final String PAYLOAD_KEY = "Payload";

    public static final String EMPTY_STR = "";
    public static final String ALPHA_NUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";

    public static final int ROOM_CODE_LENGTH = 3;
    public static final int MAX_LOBBY_SIZE = 5;

    public static final String BUNDLE_ROOM_CODE = "CODE";
    public static final String BUNDLE_IS_HOST = "IS_HOST";
    public static final String BUNDLE_TURN_COUNT = "TURN_COUNT";
    public static final String BUNDLE_ON_SUBMITTED = "ON_SUBMIT";
    public static final String BUNDLE_SUBMIT_MESSAGE = "SUBMIT_MESSAGE";
    public static final String BUNDLE_PAYLOAD = "PAYLOAD";


    public static final String LOADING_MESSAGE="LOADING_MESSAGE";


    public enum GAME_STATE {
        LOBBY, IN_PROGRESS, END_OF_GAME
    }

    public enum JOIN_ATTEMPT_RESPONSE{
        VALID_LOBBY, GAME_NOT_CREATED, LOBBY_FULL, GAME_IN_PROGRESS
    }
}
