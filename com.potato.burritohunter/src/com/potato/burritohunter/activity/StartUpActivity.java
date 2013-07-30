package com.potato.burritohunter.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class StartUpActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = new Intent (this, MapActivity.class);
		startActivity(intent);
		finish();
	}
}
