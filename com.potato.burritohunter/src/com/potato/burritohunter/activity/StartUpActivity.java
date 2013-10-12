package com.potato.burritohunter.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.actionbarsherlock.app.SherlockActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.potato.burritohunter.R;
import com.potato.burritohunter.stuff.ADS;

public class StartUpActivity extends SherlockActivity
{
  // TODO prefer enums somehow?
  public static final String SKIP_STARTUP_FLAG = "skip startup flag";

  @Override
  protected void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    final Context context = this;
    ADS.getInstance().init( getApplicationContext() );
    ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).build();
    ImageLoader.getInstance().init(config);
    if ( getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().getBoolean( SKIP_STARTUP_FLAG, false ) )
    {
      Intent intent = new Intent( context, MapActivity.class );
      startActivity( intent );
      finish();
    }
    else
    {
      setContentView( R.layout.activity_startup );

      Handler handler = new Handler();
      handler.postDelayed( new Runnable()
        {
          public void run()
          {
            Intent intent = new Intent( context, MapActivity.class );
            startActivity( intent );
            finish();
          }
        }, 300 );
    }
  }
}
