package fm.mote.motefm.V1;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
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
    private static String errorMessage = "";

    private static String sendAuthedPostRequest(String url, APIResponse user, String jsondata)
    {
        try{
            HttpPost httpPost = new HttpPost(API_URL + "/" + url);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("X-User-Email", user.user.email);
            httpPost.setHeader("X-User-Token", user.application.authenticationToken);
            httpPost.setHeader("X-User-App", user.application.applicationToken);
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setEntity(new StringEntity(jsondata));

            HttpResponse response = new DefaultHttpClient().execute(httpPost);

            StatusLine status = response.getStatusLine();

            if(status.getStatusCode() == 200) {
                String jsonReply = "";
                try {
                    jsonReply = EntityUtils.toString(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("mote_debug", jsonReply);
                return jsonReply;
            }
            else
            {
                try {
                    errorMessage = EntityUtils.toString(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    private static String sendAuthedGetRequest(String url, APIResponse user, String json)
    {
        try {
            HttpGet httpGet = new HttpGet(API_URL + "/" + url);
            httpGet.setHeader("X-User-Email", user.user.email);
            httpGet.setHeader("X-User-Token", user.application.authenticationToken);
            httpGet.setHeader("X-User-App", user.application.applicationToken);
            httpGet.setHeader("Content-type", "application/json");
            httpGet.setHeader("Accept", "application/json");

            HttpResponse response = new DefaultHttpClient().execute(httpGet);

            StatusLine status = response.getStatusLine();

            if(status.getStatusCode() == 200) {
                String jsonReply = "";
                try {
                    jsonReply = EntityUtils.toString(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("mote_debug", jsonReply);
                return jsonReply;
            }
            else
            {
                try {
                    errorMessage = EntityUtils.toString(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    private static String sendPostRequest(String url, String jsondata)
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
            else
            {
                try {
                    errorMessage = EntityUtils.toString(response.getEntity());
                    Log.d("motedebug", errorMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
    public static APIResponse loginRequest(String email, String password)
    {
        Map<String, Map<String, String> > request = new HashMap<String, Map<String, String> >();
        Map<String, String> data = new HashMap<String, String>();

        data.put("email", email);
        data.put("password", password);

        request.put("user", data);
        ObjectMapper om = new ObjectMapper();


        String json = null;
        try {
            json = om.writer().writeValueAsString(request);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        String response = sendPostRequest("v1/users/sign_in.json",json);
        APIResponse login = null;

        try {
            login = om.readValue(response, new TypeReference<APIResponse>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }

        return login;
    }

    public static APIPartyResponse getPartyByHash(String hash, APIResponse user)
    {
        String response = sendAuthedGetRequest("v1/parties/" + hash + ".json",user , "");

        ObjectMapper om = new ObjectMapper();
        APIPartyResponse party = null;
        try {
            party = om.readValue(response, new TypeReference<APIPartyResponse>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return party;

    }

    public static APIPartyResponse createParty(String partyName, APIResponse user)
    {
        Map<String, Map<String, String> > request = new HashMap<String, Map<String, String> >();
        Map<String, String> data = new HashMap<String, String>();
        data.put("name", partyName);
        request.put("party", data);

        ObjectMapper om = new ObjectMapper();

        String json = null;
        try {
            json = om.writer().writeValueAsString(request);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        String response = sendAuthedPostRequest("v1/parties.json",user,json);
        APIPartyResponse party = null;
        try {
            party = om.readValue(response, new TypeReference<APIPartyResponse>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }

        return party;
    }

    public static String getErrorMessage()
    {
        return  errorMessage;
    }

    public static class APIResponse implements Serializable
    {
        @JsonProperty("user")
        public UserLogin user;
        @JsonProperty("application")
        public Application application;
        @JsonProperty("errors")
        public String errors;
        @JsonProperty("notes")
        public boolean notes;
        @JsonProperty("success")
        public boolean success;
    }

    public static class APIPartyResponse extends APIResponse implements Serializable
    {
        @JsonProperty("party")
        public Party party;
    }

    public static class Party implements Serializable
    {
        @JsonProperty("name")
        public String name;
        @JsonProperty("party_hash")
        public String partyHash;
        @JsonProperty("tracks")
        public List<Track> tracks;
        @JsonProperty("user")
        public User user;
    }

    public static class Track implements Serializable
    {
        @JsonProperty("id")
        public int id;

        @JsonProperty("created_at")
        public String createdAt;

        @JsonProperty("playing")
        public int playing;

        @JsonProperty("info")
        public SongInfo info;

        @JsonProperty("votes")
        public List<User> votes;

        @JsonProperty("user")
        public User user;
    }

    public static class SongInfo implements Serializable
    {
        @JsonProperty("track")
        public String track;

        @JsonProperty("artist")
        public String artist;

        @JsonProperty("album")
        public String album;

        @JsonProperty("album_art")
        public String album_art;

        @JsonProperty("uri")
        public String uri;

    }

    public static class UserLogin implements Serializable
    {
        @JsonProperty("name")
        public String name;

        @JsonProperty("id")
        public int id;

        @JsonProperty("email")
        public String email;

        @JsonProperty("identities")
        public String[] identities;

        @JsonProperty("parties")
        public List<Party> parties;
    }

    public static class User implements Serializable
    {
        @JsonProperty("name")
        public String name;

        @JsonProperty("email")
        public String email;
    }

    public static class Application implements Serializable
    {
        @JsonProperty("authentication_token")
        public String authenticationToken;

        @JsonProperty("application_token")
        public String applicationToken;
    }
}

