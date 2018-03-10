package com.example.tushar.mc_final;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tushar on 3/10/18.
 */

public class User {

    private String mEmail;
    private String mName;
    private boolean mPrivFlag; //To determine the privacy setting of the user
    private String mUserLocation;
    private ArrayList<String> mFriends;
    private ArrayList<String> mSent;
    private ArrayList<String> mReceived;

    public User()
    {

    }

    public User( String argEmail, String argName, boolean argPrivFlag, String argUserLocation, ArrayList<String> argFriends, ArrayList<String> argSent, ArrayList<String> argReceived)
    {
        this.mEmail = argEmail;
        this.mName = argName;
        this.mPrivFlag = argPrivFlag;
        this.mUserLocation = argUserLocation;
        this.mFriends = argFriends;
        this.mSent = argSent;
        this.mReceived = argReceived;
    }

















    public List<String> getmFriends() {
        return mFriends;
    }

    public void setmFriends(ArrayList<String> mFriends) {
        this.mFriends = mFriends;
    }

    public List<String> getmSent() {
        return mSent;
    }

    public void setmSent(ArrayList<String> mSent) {
        this.mSent = mSent;
    }

    public List<String> getmReceived() {
        return mReceived;
    }

    public void setmReceived(ArrayList<String> mReceived) {
        this.mReceived = mReceived;
    }
    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public boolean ismPrivFlag() {
        return mPrivFlag;
    }

    public void setmPrivFlag(boolean mPrivFlag) {
        this.mPrivFlag = mPrivFlag;
    }

    public String getmUserLocation() {
        return mUserLocation;
    }

    public void setmUserLocation(String mUserLocation) {
        this.mUserLocation = mUserLocation;
    }
}
