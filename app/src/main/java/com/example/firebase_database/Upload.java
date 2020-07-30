package com.example.firebase_database;

import com.google.firebase.database.Exclude;

public class Upload {
    private String mName;
    private String mImageUri;
    private String mKey;

    public Upload(){

    }

    public Upload(String mName, String mImageUri) {
        if(mName.trim().equals("")){
            this.mName = " No Name";
        }
        this.mName = mName;
        this.mImageUri = mImageUri;
    }

    public void setmImageUri(String mImageUri) {
        this.mImageUri = mImageUri;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmName() {
        return mName;
    }

    @Exclude
    public String getmKey() {
        return mKey;
    }

    @Exclude
    public void setmKey(String mKey) {
        this.mKey = mKey;
    }

    public String getmImageUri() {
        return mImageUri;
    }
}

