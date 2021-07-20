package com.example.android.miwok;

public class Word {
    private String miwok_word;
    private String english_word;
    private int imageID;
    private int soundID;

    public Word(String miwok_word, String english_word, int soundID){
        this.miwok_word = miwok_word;
        this.english_word = english_word;
        this.soundID = soundID;
    }

    public Word(String miwok_word, String english_word, int imageID, int soundID){
        this.miwok_word = miwok_word;
        this.english_word = english_word;
        this.imageID = imageID;
        this.soundID = soundID;
    }

    public String getMiwok_word() {
        return miwok_word;
    }

    public String getEnglish_word() {
        return english_word;
    }

    public int getImageID() {
        return imageID;
    }

    public int getSoundID(){
        return soundID;
    }

}
