package com.example.tistatos.test;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.graphics.Typeface;

import com.example.tistatos.test.websocketrails.WebSocketRailsChannel;
import com.example.tistatos.test.websocketrails.WebSocketRailsDataCallback;
import com.example.tistatos.test.websocketrails.WebSocketRailsDispatcher;
import com.spotify.sdk.android.Spotify;
import com.spotify.sdk.android.authentication.SpotifyAuthentication;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.playback.ConnectionStateCallback;
import com.spotify.sdk.android.playback.Player;
import com.spotify.sdk.android.playback.PlayerNotificationCallback;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;


public class MyActivity extends Activity implements
        PlayerNotificationCallback, ConnectionStateCallback {

    // TODO: Replace with your client ID
    private static final String CLIENT_ID = "22a724c740fe4fe4a5edc45efd7e4ab6";
    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "eriktest://mahtest";

    private Player mPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_party);

        TextView partyTitle = (TextView)findViewById(R.id.partyTitleText);
        Typeface latoRegular = Typeface.createFromAsset(this.getAssets(), "fonts/Lato-Reg.ttf");

        partyTitle.setTypeface(latoRegular);

        SpotifyAuthentication.openAuthWindow(CLIENT_ID, "token", REDIRECT_URI,
               new String[]{"user-read-private", "streaming"}, null, this);
    }

    private boolean connectWebsocket()
    {
        WebSocketRailsDispatcher mDispatcher;

        try
        {
            mDispatcher = new WebSocketRailsDispatcher(new URL("http://192.168.0.109:3000/websocket"));
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
            return false;
        }
        mDispatcher.connect();

        WebSocketRailsChannel channel = mDispatcher.subscribe("messages");
        channel.bind("update_chat",new WebSocketRailsDataCallback() {
            @Override
            public void onDataAvailable(Object data) {
                LinkedHashMap<String,String> jek = (LinkedHashMap<String,String>)data;
                Log.e("mote", jek.get("nickname") + " " + jek.get("message") );
                mPlayer.clearQueue();
                mPlayer.queue(jek.get("message"));
            }
        });

        LinkedHashMap<String,String> msgdata = new LinkedHashMap<String, String>();
        msgdata.put("nickname", "android");
        msgdata.put("body", "android can chat too");

        LinkedHashMap<String,Object> data = new LinkedHashMap<String, Object>();
        data.put("data",msgdata);

        mDispatcher.trigger("message",data,new WebSocketRailsDataCallback() {
            @Override
            public void onDataAvailable(Object data) {
                Log.e("trigger","YES");
            }
        },
        new WebSocketRailsDataCallback() {
            @Override
            public void onDataAvailable(Object data) {
                Log.e("trigger","NOPE");

            }
        });
        return true;
    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        if (uri != null) {
            AuthenticationResponse response = SpotifyAuthentication.parseOauthResponse(uri);
            Spotify spotify = new Spotify(response.getAccessToken());

            //FIXME: Doesnt work on emulator!
            mPlayer = spotify.getPlayer(this, "My Company Name", this, new Player.InitializationObserver() {
                @Override
                public void onInitialized() {
                    mPlayer.addConnectionStateCallback(MyActivity.this);
                    mPlayer.addPlayerNotificationCallback(MyActivity.this);
                    mPlayer.play("spotify:track:1DAshXYxxLHC6otfko4Djs");
                    connectWebsocket();
                    Log.d("MainActivity", "playing music");
                }

                @Override
                public void onError(Throwable throwable) {
                    Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                }
            });
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onNewCredentials(String s) {
        Log.d("MainActivity", "User credentials blob received");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(EventType eventType) {
        Log.d("MainActivity", "Playback event received: " + eventType.name());
    }

    @Override
    protected void onDestroy()
    {
        Spotify.destroyPlayer(this);

        super.onDestroy();
    }
}
