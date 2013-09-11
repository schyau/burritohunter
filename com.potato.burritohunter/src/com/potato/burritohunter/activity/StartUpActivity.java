package com.potato.burritohunter.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.actionbarsherlock.app.SherlockActivity;
import com.potato.burritohunter.R;
import com.potato.burritohunter.stuff.ADS;

public class StartUpActivity extends SherlockActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startup);
		final Context context = this;
		ADS.getInstance().init(getApplicationContext());
	    Handler handler = new Handler(); 
	    handler.postDelayed(new Runnable() { 
	         public void run() { 
	     		Intent intent = new Intent (context, MapActivity.class);
	     		startActivity(intent);
	     		finish();
	         } 
	    }, 300); 
	}
}
