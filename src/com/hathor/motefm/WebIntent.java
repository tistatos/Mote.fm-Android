package com.hathor.motefm;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

public class WebIntent extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_web_intent);
		Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/Lato-Reg.ttf");
		TextView tv = (TextView) findViewById(R.id.textView1);
		TextView tv2 = (TextView) findViewById(R.id.textView2);
		tv.setTypeface(tf);
		tv2.setTypeface(tf);
		
		new Thread(new Runnable() {
		    @Override
		    public void run() {
		        try {
		            Thread.sleep(3000);
		    		
		    		Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.mote.fm"));
		    		startActivity(web);
		        } catch (InterruptedException e) {
		            e.printStackTrace();
		        }
		    }
		}).start();
		

	}
}
