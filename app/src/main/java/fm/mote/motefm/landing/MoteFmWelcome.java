package fm.mote.motefm.landing;

import fm.mote.motefm.R;
import fm.mote.motefm.party.MoteFmPartyList;
import fm.mote.motefm.util.SystemUiHider;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.GsonBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoteFmWelcome extends Activity {
    static final String APP_TOKEN = "75d13339880c3a190cf36690574f9da5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mote_fm_welcome);
        EditText password = (EditText) findViewById(R.id.txt_password);
        password.setTypeface(Typeface.DEFAULT);
        password.setTransformationMethod(new PasswordTransformationMethod());

        // Only for debugging FIXME
         StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
         StrictMode.setThreadPolicy(policy);

        final Button login = (Button)findViewById(R.id.btn_login);

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Send login request to server
                LoginReply.User user = login();
                if(user != null)
                {
                    Intent partyList = new Intent(getBaseContext(), MoteFmPartyList.class);
                    partyList.putExtra("username", user.name);
                    startActivity(partyList);
                }
                else
                {
                    TextView error = (TextView) findViewById(R.id.txt_error_text);
                    error.setText(getLoginError());
                }
            }
        });
    }

    private LoginReply.User login()
    {
        String email = ((EditText) findViewById(R.id.txt_user)).getText().toString();
        String password= ((EditText) findViewById(R.id.txt_password)).getText().toString();
        return HttpRequest.executeLogin(email, password);
    }

    private String getLoginError()
    {
        String error =  "Error Logging in";
        return "Error: " + error;
    }

    public class LoginReply{
        @SerializedName("user")
        public User user;

        public class User
        {
            @SerializedName("name")
            public String name;
            @SerializedName("id")
            public int id;
            @SerializedName("email")
            public String email;
            @SerializedName("authentication_token")
            public String auth_token;
            @SerializedName("identities")
            public List<String> ident;
            @SerializedName("parties")
            public List<String> parties;
        }
    }

    private static class HttpRequest
    {
        public static LoginReply.User executeLogin(String email, String password) {

            Map<String, Map<String, String> > request = new HashMap<String, Map<String, String> >();
            Map<String, String> users = new HashMap<String, String>();

            users.put("email", email);
            users.put("password", password);

            request.put("user", users);

            String json = new GsonBuilder().create().toJson(request, Map.class);
            HttpResponse resp = makeRequest("http://130.236.112.148:3001/v1/users/sign_in.json", json);
            StatusLine status = resp.getStatusLine();
            if(status.getStatusCode() == 200)
            {
                String jsonReply = "";
                try {
                    jsonReply = EntityUtils.toString(resp.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("motedebug", jsonReply);
                Gson gson = new Gson();
                LoginReply rpl = gson.fromJson(jsonReply, LoginReply.class);
                return rpl.user;
            }
            return null;
        }

        public static HttpResponse makeRequest(String uri, String json) {
            try {
                HttpPost httpPost = new HttpPost(uri);
                httpPost.setEntity(new StringEntity(json));
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                httpPost.setHeader("Authorization", "Token " + APP_TOKEN);

                return new DefaultHttpClient().execute(httpPost);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}
