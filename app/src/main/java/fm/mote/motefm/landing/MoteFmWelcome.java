package fm.mote.motefm.landing;

import fm.mote.motefm.R;
import fm.mote.motefm.V1.APIRequests;
import fm.mote.motefm.party.MoteFmPartyList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MoteFmWelcome extends Activity {

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
                APIRequests.APIResponse user = login();
                if(user != null)
                {
                    Intent partyList = new Intent(getBaseContext(), MoteFmPartyList.class);
                    partyList.putExtra("user", user);
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

    private APIRequests.APIResponse login()
    {
        String email = ((EditText) findViewById(R.id.txt_user)).getText().toString();
        String password= ((EditText) findViewById(R.id.txt_password)).getText().toString();
        return APIRequests.loginRequest(email, password);
    }

    private String getLoginError()
    {
        String error;
        error = APIRequests.getErrorMessage();
        return "Error: " + error;
    }
}
