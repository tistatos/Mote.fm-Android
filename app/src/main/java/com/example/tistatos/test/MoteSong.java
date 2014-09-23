package com.example.tistatos.test;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class MoteSong implements Comparable<MoteSong>
{
    @JsonProperty("URI")
    private String mSpotifyURI;
    private ArrayList<MoteUser> mVoters;
    private MoteUser mSuggestor;
    private ePlayingStatus mPlayingStatus;

    public enum ePlayingStatus
    {
        kPlaying,
        kPlayed,
        kQueued
    }

    public MoteSong(String URI, MoteUser user)
    {
        //TODO: how should we handle user that adds the song?
        mSpotifyURI = URI;
        mVoters = new ArrayList<MoteUser>();
        mVoters.add(user);
        mSuggestor = user;
        mPlayingStatus = ePlayingStatus.kQueued;
    }

    public String getSpotifyURI()
    {
        return mSpotifyURI;
    }

    public MoteUser getSuggestor()
    {
        return mSuggestor;
    }

    public int voteCount()
    {
        return mVoters.size();
    }

    public void addVote(MoteUser voter)
    {
        mVoters.add(voter);
    }

    public void removeVote(MoteUser voter)
    {
        if(mVoters.contains(voter))
        {
            mVoters.remove(voter);
        }
    }

    public ePlayingStatus getStatus()
    {
        return mPlayingStatus;
    }

    public void setAsPlayed()
    {
        mPlayingStatus = ePlayingStatus.kPlayed;
    }
    public boolean isPlaying()
    {
        return (mPlayingStatus == ePlayingStatus.kPlaying);
    }

    @Override
    public int compareTo(MoteSong that) {
        if(this.getStatus() != that.getStatus())
        {
            //different status
            if(this.getStatus() == ePlayingStatus.kPlayed)
            {
                return -1;
            }
            if(this.getStatus() == ePlayingStatus.kQueued &&
               that.getStatus() == ePlayingStatus.kPlayed )
            {
                return 1;
            }
            else
            {
                return -1;
            }
        }
        else
        {
            //both have same status
            return (int) Math.signum(this.voteCount() - that.voteCount());
        }
    }

    public String playSong()
    {
        mPlayingStatus = ePlayingStatus.kPlaying;
        return mSpotifyURI;
    }

}
