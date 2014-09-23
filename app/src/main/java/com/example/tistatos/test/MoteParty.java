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

    private static final String MOTE_WS_SERVER = "http://10.0.2.2:3000/websocket";

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

    public String getPartyName()
    {
        if(mPartyName != "")
            return mPartyName;
        return "";
    }

    public void playNextSong()
    {
        mSpotify.play(getNextSong().getSpotifyURI());
    }

    public MoteSong getNextSong()
    {
        Collections.sort(mSongs);
        return mSongs.get(0);
    }

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



    public boolean connect()
    {
        return mSocket.connect();
    }

    public void subscribeParty()
    {
        mSocket.SubscribeToParty(mPartyID);
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
                    song.addVote(voter);
                    Log.d("mote", "vote added!");
                }
            }
        });
    }

    public boolean getPartyQueue()
    {
        mSocket.triggerEvent("get_queue", new WebSocketRailsDataCallback(){

            @Override
            public void onDataAvailable(Object data) {
                LinkedHashMap<String,String> parsed = (LinkedHashMap<String,String>)data;
                MoteSong song = new MoteSong(parsed.get("URI"),null);
                //mSongs.add(song);
                Log.d("motesocket", song.getSpotifyURI());
                //playNextSong();

            }
        });
        return  false;
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
    }

    public void skipSong()
    {
        mSongs.get(0).setAsPlayed();
        Collections.sort(mSongs);
    }

    public void removeSong(MoteSong song)
    {
        mSongs.remove(song);
        Collections.sort(mSongs);
    }

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

    public void removeSong(int index)
    {
        mSongs.remove(index);
        Collections.sort(mSongs);
    }
}
