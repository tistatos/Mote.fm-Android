package com.example.tistatos.test;

/**
 * Created by tistatos on 2014-09-19.
 */
public class MoteUser
{
    private String mMoteID;
    private String mUserPic;
    private String mUserName;

    /**
     * public constructor
     * @param moteID id of user
     */
    public MoteUser(String moteID)
    {
        mMoteID = moteID;
        mUserPic = "";
        mUserName = moteID; //FIXME
    }

    //FIXME: Should this be string?

    /**
     * get string to user's picture
     * @return url to user pic
     */
    public String getUserPicture()
    {
        if(mUserPic != "" )
            return mUserPic;
        //TODO: get picture from our API "http://sonic.mote.fm/user/[USERID]/Picture"?
        return "not implemented";
    }

    /**
     * get user's name
     * @return the name
     */
    public String getUserName()
    {
        if(mUserName != "" )
            return mUserName;
        //TODO: get picture from our API "http://sonic.mote.fm/user/[USERID]/name"?
        return "not implemented";
    }
}
