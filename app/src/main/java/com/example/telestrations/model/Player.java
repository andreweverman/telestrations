package com.example.telestrations.model;
import java.io.Serializable;
import androidx.annotation.NonNull;

public class Player implements Serializable {
    public String displayName;
    public String id;
    public String imageFileName;
    public int nameColor;

    //No-arg ctor required for firebase (KEEP)
    public Player(){

    }

    public Player(String displayName, String id, String imageFileName, int nameColor){
        this.displayName = displayName;
        this.id = id;
        this.imageFileName = imageFileName;
        this.nameColor = nameColor;
    }

    @NonNull
    @Override
    public String toString() {
        return this.displayName + "\n" + this.id + "\n" + this.imageFileName + "\n" + this.nameColor;
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null || obj.getClass() != this.getClass()){
            return false;
        }

        //only consider equals if unique ID is equal
        if(((Player) obj).getId().equals(this.getId())){
            return true;
        }

        return false;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String newName){
        this.displayName = newName;
    }

    public String getId() {
        return id;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public int getNameColor() {
        return nameColor;
    }

    public void setNameColor(int nameColor) {
        this.nameColor = nameColor;
    }



}
