package com.example.telestrations.model;

import java.io.Serializable;

import androidx.annotation.NonNull;

public class SubmitMessage implements Serializable {

    private String origPlayerID;
    private String payload;
    private int turnCount;

    //NO-ARG CTOR REQUIRED FOR FIREBASE, don't remove
    public SubmitMessage(){

    }

    public SubmitMessage(String ID, String payload, int turnCount){
        this.origPlayerID = ID;
        this.payload = payload;
        this.turnCount = turnCount;
    }

    public String getOrigPlayerID() {
        return origPlayerID;
    }

    public int getTurnCount() {
        return turnCount;
    }

    public String getPayload() {
        return payload;
    }

    @NonNull
    @Override
    public String toString() {
        return this.origPlayerID + ", " + this.payload + ", " + this.turnCount;
    }
}
