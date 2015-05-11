package fm.mote.motefm.V1.websocket;

import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotify.sdk.android.playback.ConnectionStateCallback;
import com.spotify.sdk.android.playback.Player;
import com.spotify.sdk.android.playback.PlayerNotificationCallback;
import com.spotify.sdk.android.playback.PlayerState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import fm.mote.motefm.V1.APIRequests;
import fm.mote.motefm.V1.websocket.websocketrails.WebSocketRailsDataCallback;

/**
 * Created by tistatos on 2014-09-19.
 */
public class MoteParty implements
        PlayerNotificationCallback, ConnectionStateCallback {

    private String mPartyID;
    private String mPartyName;
    private ArrayList<MoteSong> mSongs;
    private MoteSocketManager mSocket;
    private Player mSpotify;

    private ArrayList<MoteSong> mChanges;
    private boolean mHasChanged;
    private boolean mPlaying;
    // address to mote's websocket server
    private static final String MOTE_WS_SERVER = "http://10.0.2.2:3001/websocket";

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
        mChanges = new ArrayList<MoteSong>();
        mHasChanged = false;
        mPlaying = false;
    }

    public void setPlaylist(APIRequests.Party party)
    {
        for(APIRequests.Track t :  party.tracks )
        {
            mSongs.add(new MoteSong(t));
        }
    }

    /**
     * Get name of party
     * @return party name
     */
    public String getPartyName()
    {
        return mPartyName;
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
    public void subscribeParty(String authToken, String userEmail)
    {
        mSocket.SubscribeToParty(mPartyID, authToken, userEmail);
        //FIXME: this should be its own class
        mSocket.AddCallback("new_track", new WebSocketRailsDataCallback() {
            @Override
            public void onDataAvailable(Object data) {
                Log.d("motefm", "started new");
                LinkedHashMap<String,String> parsed = (LinkedHashMap<String,String>)data;
                APIRequests.Track track;
                ObjectMapper om = new ObjectMapper();
                if(data instanceof Map)
                {
                    track = (APIRequests.Track) om.convertValue(data, new TypeReference<APIRequests.Track>() {});
                    Log.d("motefm", "New " + track.info.track);
                    mChanges.add(new MoteSong(track));
                    mHasChanged = true;
                }
            }
        });

        mSocket.AddCallback("new_vote", new WebSocketRailsDataCallback() {
            @Override
            public void onDataAvailable(Object data) {

                LinkedHashMap<String,String> parsed = (LinkedHashMap<String,String>)data;
                APIRequests.Track track;
                ObjectMapper om = new ObjectMapper();
                if(data instanceof Map)
                {
                    track = (APIRequests.Track) om.convertValue(data, new TypeReference<APIRequests.Track>() {});
                    Log.d("motefm", "Vote " + track.info.track);
                    mChanges.add(new MoteSong(track));
                    mHasChanged = true;
                }
            }
        });

        mSocket.AddCallback("delete_vote", new WebSocketRailsDataCallback() {
            @Override
            public void onDataAvailable(Object data) {
                LinkedHashMap<String,String> parsed = (LinkedHashMap<String,String>)data;
                APIRequests.Track track;
                ObjectMapper om = new ObjectMapper();
                if(data instanceof Map)
                {
                    track = (APIRequests.Track) om.convertValue(data, new TypeReference<APIRequests.Track>() {});
                    Log.d("motefm", "Delete " + track.info.track);
                    mChanges.add(new MoteSong(track));
                    mHasChanged = true;
                }

            }
        });
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

    public void applyChanges()
    {
        if(!mHasChanged)
            return;

        for(MoteSong s : mChanges)
        {
            MoteSong prev = findSong(s.getSpotifyURI());
            if(prev != null)
                mSongs.remove(prev);
            mSongs.add(s);
        }
        Collections.sort(mSongs);
        mChanges.clear();
        mHasChanged = false;
        if(!mPlaying)
        {
            playNextSong();
            mPlaying = true;
        }
    }

    @Override
    public void onLoggedIn() {

    }

    @Override
    public void onLoggedOut() {

    }

    @Override
    public void onLoginFailed(Throwable throwable) {

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
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        if(eventType.equals(EventType.TRACK_END))
            playNextSong();
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String s) {

    }

    public ArrayList<MoteSong> getPlaylist()
    {
        return mSongs;
    }
}
