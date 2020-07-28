package com.example.firebase_database;

public class Upload {
    private String mName;
    private String mImageUri;

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

    public String getmImageUri() {
        return mImageUri;
    }
}

