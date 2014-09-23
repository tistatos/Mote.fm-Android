package com.example.tistatos.test;

/**
 * Created by tistatos on 2014-09-19.
 */
public class MoteUser
{
    private String mMoteID;
    private String mUserPic;
    private String mUserName;

    public MoteUser(String moteID)
    {
        mMoteID = moteID;
        mUserPic = "";
        mUserName = moteID; //FIXME
    }

    //FIXME: Should this be string?
    public String getUserPicture()
    {
        if(mUserPic != "" )
            return mUserPic;
        //TODO: get picture from our API "http://sonic.mote.fm/user/[USERID]/Picture"?
        return "not implemented";
    }

    public String getUserName()
    {
        if(mUserName != "" )
            return mUserName;
        //TODO: get picture from our API "http://sonic.mote.fm/user/[USERID]/name"?
        return "not implemented";
    }
}
