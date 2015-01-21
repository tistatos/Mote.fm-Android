package fm.mote.motefm.V1.websocket;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

import fm.mote.motefm.V1.APIRequests;

public class MoteSong implements Comparable<MoteSong>
{
    private APIRequests.Track mTrack;
    private ePlayingStatus mPlayingStatus;

    /**
     * status of song's playing status
     */
    public enum ePlayingStatus
    {
        kPlaying,
        kPlayed,
        kQueued
    }

    /**
     * public constructor
     */
    public MoteSong(APIRequests.Track track)
    {
        mTrack = track;
        mPlayingStatus = ePlayingStatus.kQueued;
    }

    /**
     * get spotify URI related to song
     * @return the URI
     */
    public String getSpotifyURI()
    {
        return mTrack.info.uri;
    }

    /**
     * get spotify URI related to song
     * @return the URI
     */
    public String getTitle()
    {
        return mTrack.info.track;
    }

    /**
     * get spotify URI related to song
     * @return the URI
     */
    public String getArtist()
    {
        return mTrack.info.artist;
    }

    /**
     * get spotify URI related to song
     * @return the URI
     */
    public String getAlbumArt()
    {
        return mTrack.info.album_art;
    }

    /**
     * get user that suggested the song
     * @return the user
     */
    public APIRequests.User getSuggestor()
    {
        return mTrack.user;
    }


    /**
     * Get the vote count for this song
     * @return number of votes on this song
     */
    public int voteCount()
    {
        return mTrack.votes.size();
    }

    /**
     * get playing status of this song
     * @return status
     */
    public ePlayingStatus getStatus()
    {
        return mPlayingStatus;
    }

    /**
     * set song as played
     */
    public void setAsPlayed()
    {
        mPlayingStatus = ePlayingStatus.kPlayed;
    }

    /**
     * test if this song is playing
     * @return
     */
    public boolean isPlaying()
    {
        return (mPlayingStatus == ePlayingStatus.kPlaying);
    }

    @Override
    public int compareTo(MoteSong that)
    {
        if(this.getStatus() == that.getStatus())
        {
            return (int) Math.signum(that.voteCount() - this.voteCount());
        }

        if(this.getStatus() == ePlayingStatus.kPlaying)
        {
            return -1;
        }
        if(that.getStatus() == ePlayingStatus.kPlaying)
        {
            return 1;
        }

        if(this.getStatus() == ePlayingStatus.kPlayed &&
           that.getStatus() == ePlayingStatus.kQueued )
        {
            return 1;
        }
        else
        {
            return -1;
        }
    }

    /**
     * get a song URI and set it's status to playing
     * @return URI
     */
    public MoteSong playSong()
    {
        mPlayingStatus = ePlayingStatus.kPlaying;
        return this;
    }

}
