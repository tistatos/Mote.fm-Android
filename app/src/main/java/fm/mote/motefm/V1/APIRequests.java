package fm.mote.motefm.V1;

import android.text.AndroidCharacter;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tistatos on 2014-12-17.
 */
public class APIRequests {
    private static final String API_URL = "http://10.0.2.2:3001";

    private static String sendRequest(String url, String jsondata)
    {
        try {
            HttpPost httpPost = new HttpPost(API_URL + "/" + url);
            httpPost.setEntity(new StringEntity(jsondata));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse response = new DefaultHttpClient().execute(httpPost);

            StatusLine status = response.getStatusLine();

            if(status.getStatusCode() == 200) {
                String jsonReply = "";
                try {
                    jsonReply = EntityUtils.toString(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("motedebug", jsonReply);
                return jsonReply;
            }
            return null;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static UserLoginResponse loginRequest(String email, String password)
    {
        Map<String, Map<String, String> > request = new HashMap<String, Map<String, String> >();
        Map<String, String> data = new HashMap<String, String>();

        data.put("email", email);
        data.put("password", password);

        request.put("user", data);
        String json = new GsonBuilder().create().toJson(request,Map.class);

        String response = sendRequest("/v1/users/sign_in.json",json);
        UserLoginResponse loginResponse = new Gson().fromJson(response, UserLoginResponse.class);
        return loginResponse;
    }

    public class UserLoginResponse implements Serializable
    {
        @SerializedName("user")
        public UserLogin user;
        @SerializedName("application")
        public Application application;
        @SerializedName("errors")
        public String errors;
        @SerializedName("notes")
        public String notes;
        @SerializedName("success")
        public boolean success;
    }

    public class Party implements Serializable
    {
        @SerializedName("name")
        public String name;
        @SerializedName("party_hash")
        public String partyHash;
        @SerializedName("tracks")
        public List<Track> tracks;
        @SerializedName("user")
        public User user;
    }

    public class Track implements Serializable
    {
        @SerializedName("id")
        public int id;
        @SerializedName("info")
        public SongInfo info;
        @SerializedName("votes")
        public List<User> votes;
        @SerializedName("user")
        public User user;
    }

    public class SongInfo implements Serializable
    {
        @SerializedName("track")
        public String track;
        @SerializedName("artist")
        public String artist;
        @SerializedName("album")
        public String album;
        @SerializedName("album_art")
        public String album_art;
        @SerializedName("uri")
        public String uri;

    }

    public class UserLogin implements Serializable
    {
        @SerializedName("name")
        public String name;
        @SerializedName("id")
        public int id;
        @SerializedName("email")
        public String email;
        @SerializedName("identities")
        public String[] identities;
        @SerializedName("parties")
        public List<Party> parties;
    }


    public class User implements Serializable
    {
        @SerializedName("name")
        public String name;
        @SerializedName("email")
        public String email;
    }

    public class Application implements Serializable
    {
        @SerializedName("authentication_token")
        public String authenticationToken;
        @SerializedName("application_token")
        public String applicationToken;
    }
}
