package fm.mote.motefm.party;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import fm.mote.motefm.R;
import fm.mote.motefm.V1.APIRequests;

/**
 * Created by tistatos on 2014-11-14.
 */
public class PartyAdapter extends BaseAdapter{

    protected List<APIRequests.Party> parties;
    Context context;
    LayoutInflater inflater;

    public PartyAdapter(Context context, List<APIRequests.Party> list)
    {
        parties = list;
        context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return parties.size();
    }

    @Override
    public Object getItem(int i) {
        return parties.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View myView = inflater.inflate(R.layout.layout_list_parties, null);
        TextView name = (TextView)myView.findViewById(R.id.party_name);
        TextView hash = (TextView)myView.findViewById(R.id.party_hash);

        APIRequests.Party party = parties.get(i);
        name.setText(party.name);
        hash.setText(party.partyHash);

        return myView;
    }
}
