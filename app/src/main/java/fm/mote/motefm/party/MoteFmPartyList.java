package fm.mote.motefm.party;

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


        TextView user = (TextView) findViewById(R.id.txtv_welcome_user);
        ListView view = (ListView) findViewById(R.id.lst_party);
        String username = getIntent().getStringExtra("username");

        user.setText("Welcome, " + username);

        String[] parties = new String[] { "Testing Party #1", "Testing party #2"};

        ArrayList<String> list = new ArrayList<String>();
        for(int i = 0; i < parties.length; i++)
        {
            list.add(parties[i]);
        }


        StableArrayAdapter adapter = new StableArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, list);
        view.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
    }

    private class StableArrayAdapter extends ArrayAdapter<String>
    {
        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects)
        {
            super(context, textViewResourceId, objects);

            for(int i = 0; i < objects.size(); i++)
            {
                mIdMap.put(objects.get(i), i);
            }

        }

        @Override
        public long getItemId(int position)
        {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds()
        {
            return true;
        }
    }
}
