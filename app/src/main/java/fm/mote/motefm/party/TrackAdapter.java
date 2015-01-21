package fm.mote.motefm.party;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import fm.mote.motefm.R;
import fm.mote.motefm.V1.APIRequests;
import fm.mote.motefm.V1.websocket.MoteSong;

/**
 * Created by tistatos on 2014-12-20.
 */
public class TrackAdapter extends BaseAdapter {

    List<MoteSong> tracks;
    LayoutInflater inflater;

    public TrackAdapter(Context context, List<MoteSong> list)
    {
        tracks = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return tracks.size();
    }

    @Override
    public Object getItem(int i) {
        return tracks.get(i);
    }

    @Override
    public long getItemId(int i) {
        return -1;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View myView = inflater.inflate(R.layout.layout_track_item, null);
        MoteSong track = tracks.get(i);
        TextView title = (TextView)myView.findViewById(R.id.tracktitle);
        title.setText(track.getTitle());
        TextView artist = (TextView)myView.findViewById(R.id.trackartist);
        artist.setText(track.getArtist());
        ImageView album = (ImageView)myView.findViewById(R.id.album_image);
        Uri albumURI = Uri.parse(track.getAlbumArt());
        TextView votes = (TextView)myView.findViewById(R.id.votes);
        votes.setText("" + track.voteCount());

        Bitmap mIcon_val = null;
        try {
            URL newurl = new URL(albumURI.toString());
            mIcon_val = BitmapFactory.decodeStream(newurl.openConnection() .getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        album.setImageBitmap(mIcon_val);

        int width = 72;
        int height = 72;
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width,height);
        album.setLayoutParams(parms);

        return myView;
    }
}
