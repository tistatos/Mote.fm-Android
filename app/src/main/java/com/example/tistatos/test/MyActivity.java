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
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.authentication.SpotifyAuthentication;
import com.spotify.sdk.android.playback.Player;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;


public class MyActivity extends Activity
{

    // TODO: Replace with your client ID
    private static final String CLIENT_ID = "22a724c740fe4fe4a5edc45efd7e4ab6";
    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "eriktest://mahtest";

    private MoteParty mParty;
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        if (uri != null) {
            AuthenticationResponse response = SpotifyAuthentication.parseOauthResponse(uri);
            Spotify spotify = new Spotify(response.getAccessToken());

            mPlayer = spotify.getPlayer(this, "My Company Name", this, new Player.InitializationObserver() {
                @Override
                public void onInitialized() {
                    mParty = new MoteParty("testparty", mPlayer);

                    if(mParty.connect())
                    {
                        Log.d("motefm", "WS Connected");

                        mParty.subscribeParty(); //TODO: should be inside of initialized
                        mParty.getPartyQueue();
                    }
                    else
                    {
                        Log.e("motefm", "could not connect to WS");
                    }
                    mPlayer.addConnectionStateCallback(mParty);
                    mPlayer.addPlayerNotificationCallback(mParty);
                }

                @Override
                public void onError(Throwable throwable) {
                    Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                }
            });


        }
    }

    @Override
    protected void onDestroy()
    {
        Spotify.destroyPlayer(this);

        super.onDestroy();
    }
}
