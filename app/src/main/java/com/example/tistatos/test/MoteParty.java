package com.example.tistatos.test;

import android.util.Log;

import com.example.tistatos.test.MoteSocketManager;
import com.example.tistatos.test.MoteSong;
import com.example.tistatos.test.MoteUser;
import com.example.tistatos.test.websocketrails.WebSocketRailsDataCallback;
import com.spotify.sdk.android.playback.ConnectionStateCallback;
import com.spotify.sdk.android.playback.Player;
import com.spotify.sdk.android.playback.PlayerNotificationCallback;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;

/**
 * Created by tistatos on 2014-09-19.
 */
public class MoteParty implements
        PlayerNotificationCallback, ConnectionStateCallback {

    private String mPartyID;
    private String mPartyName;
    private MoteUser mPartyHost;
    private ArrayList<MoteSong> mSongs;
    private ArrayList<MoteUser> mMoteUsers;
    private MoteSocketManager mSocket;
    private Player mSpotify;

    // address to mote's websocket server
    private static final String MOTE_WS_SERVER = "http://10.0.2.2:3000/websocket";

    /**
     * public constructor
     * @param partyID id of party
     * @param player spotify player
     */
    public MoteParty(String partyID, Player player)
    {
        mPartyID = partyID;
        //FIXME: ID != name;
        mPartyName = mPartyID;
        mSocket = new MoteSocketManager(MOTE_WS_SERVER);
        mSpotify = player;
        mSongs = new ArrayList<MoteSong>();
        mMoteUsers = new ArrayList<MoteUser>();
    }

    /**
     * Get name of party
     * @return party name
     */
    public String getPartyName()
    {
        if(mPartyName != "")
            return mPartyName;
        return "";
    }

    /**
     * play next song in songlist
     */
    public void playNextSong()
    {
        mSpotify.play(getNextSong().getSpotifyURI());
    }

    /**
     * get next song in list
     * @return the song
     */
    public MoteSong getNextSong()
    {
        //FIXME: should this return the not playing song?
        if(mSongs.get(0).getStatus() == MoteSong.ePlayingStatus.kPlaying)
        {
            mSongs.get(0).setAsPlayed();
        }

        Collections.sort(mSongs);
        return mSongs.get(0).playSong();
    }

    /**
     * Find a user from the party guests
     * @param userName
     * @return the user
     */
    public MoteUser findUser(String userName)
    {
        for(MoteUser u : mMoteUsers)
        {
            if (u.getUserName().equals(userName))
            {
                return u;
            }
        }
        return null;
    }

    /**
     * find song in queue from it spotify URI
     * @param URI spotify uri
     * @return the song, null if not found
     */
    public MoteSong findSong(String URI)
    {
        for(MoteSong s : mSongs)
        {
            if (s.getSpotifyURI().equals(URI))
            {
                return s;
            }
        }
        return null;
    }


    /**
     * connect websocket to mote server
     * @return always true right now
     */
    public boolean connect()
    {
        return mSocket.connect();
    }

    /**
     * subscribe to the event related to our party
     */
    public void subscribeParty()
    {
        mSocket.SubscribeToParty(mPartyID);
        //FIXME: this should be its own class
        mSocket.AddCallback("update_party", new WebSocketRailsDataCallback() {
            @Override
            public void onDataAvailable(Object data) {
                LinkedHashMap<String,String> parsed = (LinkedHashMap<String,String>)data;
                MoteUser voter = findUser(parsed.get("nickname"));

                if(voter == null)
                {
                    voter = new MoteUser(parsed.get("nickname"));
                    mMoteUsers.add(voter);
                    Log.d("mote", "New User!");

                }

                MoteSong song = findSong(parsed.get("URI"));

                if(song == null)
                {
                    song = new MoteSong(parsed.get("URI"),voter);
                    Log.d("mote", "New song!");
                    mSongs.add(song);

                }
                else
                {
                    //test if user aldready voted on song
                    if(song.isVoter(voter))
                    {
                        song.removeVote(voter);
                        Log.d("mote", "vote removed!");
                        if(song.voteCount() <= 0)
                        {
                            mSongs.remove(song);
                            Log.d("mote", "song removed!");
                        }
                    }
                    else
                    {
                        song.addVote(voter);
                        Log.d("mote", "vote added!");
                    }
                }
                Collections.sort(mSongs);

                if(mSongs.get(0).getStatus() != MoteSong.ePlayingStatus.kPlaying)
                {
                    Log.d("mote", "Start playing!");
                    playNextSong();
                }
            }
        });
    }

    /**
     * request this party's queue from the server (in case of crash or returning to session
     * @return always returns true
     */
    public boolean getPartyQueue()
    {
        mSocket.triggerEvent("get_queue", new WebSocketRailsDataCallback(){

            @Override
            public void onDataAvailable(Object data) {
                LinkedHashMap<String,String> parsed = (LinkedHashMap<String,String>)data;
                //TODO: not database implementation on sever side
                Log.d("motesocket", "CONNECTED!");

            }
        });
        return true;
    }

    /**
     * skip current playing song
     */
    public void skipSong()
    {
        mSongs.get(0).setAsPlayed();
        Collections.sort(mSongs);
    }

    /**
     * remove a song from the queue
     * @param song song we want to remove
     */
    public void removeSong(MoteSong song)
    {
        mSongs.remove(song);
        Collections.sort(mSongs);
    }

    /**
     * remove a song from the queue
     * @param spotifyURI URI of song we want to remove
     */
    public void removeSong(String spotifyURI)
    {
        for(MoteSong s : mSongs)
        {
            if (s.getSpotifyURI() == spotifyURI)
            {
                mSongs.remove(s);
                Collections.sort(mSongs);
            }
        }
    }

    /**
     * remove a song from the queue
     * @param index Index of song we want to remove
     */
    public void removeSong(int index)
    {
        mSongs.remove(index);
        Collections.sort(mSongs);
    }

    @Override
    public void onLoggedIn() {

    }

    @Override
    public void onLoggedOut() {

    }

    @Override
    public void onTemporaryError() {

    }

    @Override
    public void onNewCredentials(String s) {

    }

    @Override
    public void onConnectionMessage(String s) {

    }

    @Override
    public void onPlaybackEvent(EventType eventType) {
        if(eventType == EventType.END_OF_CONTEXT)
        {
            Log.d("moteplayback", "Play next song!");
            playNextSong();
        }
    }

    private void printQueue(){
        Log.d("moteQueue", "Queue start:");
        for(MoteSong s : mSongs)
        {
            Log.d("moteQueue", s.getSpotifyURI() + " " + s.voteCount() + " " + s.getStatus());
        }
    }
}
