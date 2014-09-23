package com.example.tistatos.test;

import android.util.Log;

import com.example.tistatos.test.websocketrails.WebSocketRailsChannel;
import com.example.tistatos.test.websocketrails.WebSocketRailsDataCallback;
import com.example.tistatos.test.websocketrails.WebSocketRailsDispatcher;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;

public class MoteSocketManager{

    private WebSocketRailsDispatcher mSocket;
    private WebSocketRailsChannel mPartyChannel;
    private URL mServerURL; //mote.fm server
    private String mPartyID; //party channel

    public MoteSocketManager(String wsServerURL)
    {
        try
        {
            mServerURL = new URL(wsServerURL);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
    }

    public boolean connect()
    {
        mSocket = new WebSocketRailsDispatcher(mServerURL);
        mSocket.connect();
        return true;
    }

    public String getState()
    {
        return mSocket.getState();
    }

    public void SubscribeToParty(String partyID)
    {
        mPartyID = partyID;
        mPartyChannel = mSocket.subscribe(partyID);
    }

    public void triggerEvent(String event, WebSocketRailsDataCallback callback)
    {
        mSocket.trigger(event,"",callback,null);
    }

    public void AddCallback(String trigger, WebSocketRailsDataCallback callback)
    {
        mPartyChannel.bind(trigger,callback);
    }
}