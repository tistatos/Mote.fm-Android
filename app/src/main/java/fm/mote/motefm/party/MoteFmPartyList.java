package fm.mote.motefm.party;

import fm.mote.motefm.V1.APIRequests;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import java.util.List;

import fm.mote.motefm.R;

public class MoteFmPartyList extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {

    APIRequests.APIResponse user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mote_fm_party_list);

        TextView userText = (TextView) findViewById(R.id.txtv_welcome_user);
        ListView view = (ListView) findViewById(R.id.lst_party);
        user = (APIRequests.APIResponse)getIntent().getExtras().getSerializable("user");
        userText.setText("Welcome, " + user.user.name);

        List<APIRequests.Party> list = user.user.parties;
        PartyAdapter adapter = new PartyAdapter(this, list);
        view.setAdapter(adapter);
        view.setOnItemClickListener(this);

        Button startParty = (Button) findViewById(R.id.btn_start_party);
        startParty.setOnClickListener(this);

    }

    @Override
    protected void onResume()
    {
        super.onResume();

    }
    @Override
    public void onBackPressed() {
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        APIRequests.Party party = (APIRequests.Party)adapterView.getItemAtPosition(i);

        APIRequests.APIPartyResponse response = APIRequests.getPartyByHash(party.partyHash, user);
        startParty(response);

    }

    @Override
    public void onClick(View view) {
        TextView partyNameTextView = (TextView)findViewById(R.id.txt_new_party_name);
        String partyName = partyNameTextView.getText().toString();
        APIRequests.APIPartyResponse response;
        response = APIRequests.createParty(partyName, user);

        startParty(response);
    }

    private void startParty(APIRequests.APIPartyResponse party)
    {
        Intent partyI = new Intent(getBaseContext(), MoteFmPartyView.class);
        //FIXME: do call to API to get proper party object here
        partyI.putExtra("party", party);
        startActivity(partyI);
    }
}
