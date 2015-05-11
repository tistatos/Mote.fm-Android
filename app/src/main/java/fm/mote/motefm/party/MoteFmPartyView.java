package fm.mote.motefm.party;

import fm.mote.motefm.V1.APIRequests;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.spotify.sdk.android.Spotify;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.authentication.SpotifyAuthentication;
import com.spotify.sdk.android.playback.Config;
import com.spotify.sdk.android.playback.Player;


import fm.mote.motefm.R;
import fm.mote.motefm.V1.websocket.MoteParty;

public class MoteFmPartyView extends Activity {

    // FIXME: Replace with your client ID
    private static final String CLIENT_ID = "22a724c740fe4fe4a5edc45efd7e4ab6";
    // FIXME: Replace with your redirect URI
    private static final String REDIRECT_URI = "eriktest://mahtest";
    private APIRequests.APIResponse user;
    private Player mPlayer;
    private MoteParty mParty;
    private APIRequests.Party party;
    private TrackAdapter mAdapter;
    private Handler mHandler = new Handler();
    private Runnable run = new Runnable(){

        @Override
        public void run() {
            mParty.applyChanges();
            mAdapter.notifyDataSetChanged();
            mHandler.postDelayed(this,100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mote_fm_party_view);

        TextView partyTitle = (TextView) findViewById(R.id.party_name);
        TextView partyHash = (TextView) findViewById(R.id.party_hash);

        APIRequests.APIPartyResponse partyR = (APIRequests.APIPartyResponse)getIntent().getExtras().getSerializable("party");
        user = (APIRequests.APIResponse)getIntent().getExtras().getSerializable("user");
        party = partyR.party;
        partyTitle.setText(party.name);
        partyHash.setText(party.partyHash);

        if(user.user.identities.length != 0)
        {
            for(APIRequests.Identity ident : user.user.identities)
            {
                if(ident.provider == "spotify")
                {
                    //dostuff


                }
            }
        }
        SpotifyAuthentication.openAuthWindow(CLIENT_ID, "token", REDIRECT_URI,
                new String[]{"user-read-private", "streaming"}, null, this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        if (uri != null) {
            AuthenticationResponse response = SpotifyAuthentication.parseOauthResponse(uri);
            Spotify spotify = new Spotify();
            Config playerConfig = new Config(this, response.getAccessToken() ,CLIENT_ID);

            mPlayer = spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                @Override
                public void onInitialized() {
                    mParty = new MoteParty(party.partyHash, mPlayer);
                    mParty = new MoteParty(party.partyHash, mPlayer);
                    //mParty = new MoteParty(party.partyHash, mPlayer, user.application.authenticationToken user.user.email);

                    if(mParty.connect())
                    {
                        Log.d("motefm", "WS Connected");

                        mParty.subscribeParty(user.application.authenticationToken, user.user.email); //TODO: should be inside of initialized
                        mParty.setPlaylist(party);
                        ListView view = (ListView) findViewById(R.id.tracklist);

                        mAdapter = new TrackAdapter(getBaseContext(), mParty.getPlaylist());
                        view.setAdapter(mAdapter);
                        mHandler.postDelayed(run, 100);
                        if(mParty.getPlaylist().size() > 0)
                        {
                            mParty.playNextSong();
                        }
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
