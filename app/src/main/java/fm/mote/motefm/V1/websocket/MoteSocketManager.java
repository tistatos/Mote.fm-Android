package fm.mote.motefm.V1.websocket;

import fm.mote.motefm.V1.websocket.websocketrails.WebSocketRailsChannel;
import fm.mote.motefm.V1.websocket.websocketrails.WebSocketRailsDataCallback;
import fm.mote.motefm.V1.websocket.websocketrails.WebSocketRailsDispatcher;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;

public class MoteSocketManager{

    private WebSocketRailsDispatcher mSocket;
    private WebSocketRailsChannel mPartyChannel;
    private URL mServerURL; //mote.fm server
    private String mPartyID; //party channel

    /**
     * public constructor
     * @param wsServerURL url to connect socket to
     */
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

    /**
     * connect socket to server
     * @return always true
     */
    public boolean connect()
    {
        mSocket = new WebSocketRailsDispatcher(mServerURL);
        mSocket.connect();
        return true;
    }

    /**
     * get state of socket
     * @return state of socket
     */
    public String getState()
    {
        return mSocket.getState();
    }

    /**
     * subscribe socket to party
     * @param partyID id of party
     */
    public void SubscribeToParty(String partyID, String authToken, String userEmail)
    {
        mPartyID = partyID;
        mPartyChannel = mSocket.subscribe(partyID);
    }

    /**
     * Trigger an event in party channel and listen on reply
     * @param event the name of the event
     * @param callback callback for the event
     */
    public void triggerEvent(String event, WebSocketRailsDataCallback callback)
    {
        mSocket.trigger(event,"",callback,null);
    }

    /**
     * add constant callback function to event from server
     * @param trigger the event name to trigger the callback
     * @param callback callback for this event
     */
    public void AddCallback(String trigger, WebSocketRailsDataCallback callback)
    {
        mPartyChannel.bind(trigger,callback);
    }
}