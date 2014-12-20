package fm.mote.motefm.party;

import fm.mote.motefm.V1.APIRequests;
import fm.mote.motefm.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fm.mote.motefm.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MoteFmPartyList extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mote_fm_party_list);


        TextView userText = (TextView) findViewById(R.id.txtv_welcome_user);
        ListView view = (ListView) findViewById(R.id.lst_party);
        APIRequests.UserLoginResponse user= (APIRequests.UserLoginResponse)getIntent().getExtras().getSerializable("user");

        userText.setText("Welcome, " + user.user.name);




        List<APIRequests.Party> list = user.user.parties;

        PartyAdapter adapter = new PartyAdapter(this, list);
        view.setAdapter(adapter);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                APIRequests.Party party = (APIRequests.Party)adapterView.getItemAtPosition(i);
            }
        })
    }

    @Override
    public void onBackPressed() {
    }
}
