package com.example.tistatos.test;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class MoteSong implements Comparable<MoteSong>
{
    private String mSpotifyURI;
    private ArrayList<MoteUser> mVoters;
    private MoteUser mSuggestor;
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
     * @param URI spotify URI of song
     * @param suggestor the user that suggested the song
     */
    public MoteSong(String URI, MoteUser suggestor)
    {
        //TODO: how should we handle user that adds the song?
        mSpotifyURI = URI;
        mVoters = new ArrayList<MoteUser>();
        mVoters.add(suggestor);
        mSuggestor = suggestor;
        mPlayingStatus = ePlayingStatus.kQueued;
    }

    /**
     * get spotify URI related to song
     * @return the URI
     */
    public String getSpotifyURI()
    {
        return mSpotifyURI;
    }

    /**
     * get user that suggested the song
     * @return the user
     */
    public MoteUser getSuggestor()
    {
        return mSuggestor;
    }


    /**
     * Get the vote count for this song
     * @return number of votes on this song
     */
    public int voteCount()
    {
        return mVoters.size();
    }

    /**
     * add a vote to this song
     * @param voter the voter
     */
    public void addVote(MoteUser voter)
    {
        mVoters.add(voter);
    }

    /**
     * remove a vote from this song
     * @param voter voter to remove
     */
    public void removeVote(MoteUser voter)
    {
        if(mVoters.contains(voter))
        {
            mVoters.remove(voter);
        }
    }

    /**
     * test if a user has voted on this song
     * @param user the user
     * @return true if the user has voted on this song
     */
    public boolean isVoter(MoteUser user)
    {
        if(mVoters.contains(user))
        {
            return true;
        }
        else
        {
            return false;
        }
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
