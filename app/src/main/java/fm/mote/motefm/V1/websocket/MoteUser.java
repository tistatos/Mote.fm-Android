package fm.mote.motefm.V1.websocket;

/**
 * Created by tistatos on 2014-09-19.
 */
public class MoteUser
{
    private String mUserPic;
    private String mUserName;
    private String mUserEmail;

    /**
     * public constructor
     */
    public MoteUser(String mUserName, String mUserEmail)
    {
        mUserPic = "";
        mUserName = mUserName; //FIXME
        mUserEmail = mUserEmail;
    }

    //FIXME: Should this be string?
    public String getUserName()
    {
        return  mUserName;
    }
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
}
