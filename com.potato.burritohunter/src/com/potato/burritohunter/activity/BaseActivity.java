package com.potato.burritohunter.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.potato.burritohunter.R;
import com.potato.burritohunter.fragment.SampleListFragment;
import com.potato.burritohunter.preference.EditPreferencesHC;

public class BaseActivity extends SlidingFragmentActivity
{
  protected ListFragment mFrag;
  protected Context _context;

  @Override
  public void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    _context = this;

    // set the Behind View
    setBehindContentView( R.layout.menu_frame );
    if ( savedInstanceState == null )
    {
      FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
      mFrag = new SampleListFragment();
      t.replace( R.id.menu_frame, mFrag );
      Button settings = (Button) findViewById( R.id.settings_button );
      settings.setOnClickListener( new OnClickListener()
        {
          @Override
          public void onClick( View v )
          {
            startActivity( new Intent( _context, EditPreferencesHC.class ) );
          }
        } );
      t.commit();
    }
    else
    {
      mFrag = (ListFragment) this.getSupportFragmentManager().findFragmentById( R.id.menu_frame );
    }

    // customize the SlidingMenu
    SlidingMenu sm = getSlidingMenu();
    sm.setShadowWidthRes( R.dimen.shadow_width );
    sm.setShadowDrawable( R.drawable.shadow );
    sm.setBehindOffsetRes( R.dimen.slidingmenu_offset );
    sm.setFadeDegree( 0.35f );
    sm.setTouchModeAbove( SlidingMenu.LEFT );//TOUCHMODE_FULLSCREEN);

    getSupportActionBar().setDisplayHomeAsUpEnabled( true );
  }
}
