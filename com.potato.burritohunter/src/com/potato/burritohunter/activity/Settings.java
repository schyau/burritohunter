package com.potato.burritohunter.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.potato.burritohunter.R;

public class Settings extends FragmentActivity
{
  private Context _context;
  private TextView aboutTv;
  private TextView openSource;
  private SeekBar searchresults_seekbar;
  private TextView searchlimitText;
  private SeekBar distance_seekbar;
  private TextView distancetext;

  public static final String DISTANCE_KEY = "distance_key";
  public static final String SEARCH_RESULTS_KEY = "search_results_key";

  public void writeSharedPrefsInt( String key, int value )
  {
    SharedPreferences prefs = getSharedPreferences( "com.potato.burritohunter", Context.MODE_PRIVATE );
    Editor e = prefs.edit();
    e.putInt( key, value );
    e.commit();
  }

  @Override
  public void onCreate( Bundle b )
  {
    super.onCreate( b );
    // http://javatechig.com/android/android-seekbar-example/
    setContentView( R.layout.settings );
    aboutTv = (TextView) findViewById( R.id.About );
    openSource = (TextView) findViewById( R.id.open_source );
    searchresults_seekbar = (SeekBar) findViewById( R.id.searchresults_seekbar );
    searchlimitText = (TextView) findViewById( R.id.searchlimitText );
    distance_seekbar = (SeekBar) findViewById( R.id.distance_seekbar );
    distancetext = (TextView) findViewById( R.id.distancetext );

    _context = this;
    final FragmentManager fm = getSupportFragmentManager();

    SharedPreferences prefs = getSharedPreferences( "com.potato.burritohunter", Context.MODE_PRIVATE );

    int search_results = prefs.getInt( SEARCH_RESULTS_KEY, -1 );
    if ( search_results < 0 || search_results > 100 )
    {
      writeSharedPrefsInt( SEARCH_RESULTS_KEY, 50 );
      search_results = 50;
    }
    searchlimitText.setText( search_results + " results" );
    searchresults_seekbar.setProgress( search_results );

    int distance = prefs.getInt( DISTANCE_KEY, -1 );
    if ( distance < 0 || distance > 100 )
    {
      writeSharedPrefsInt( DISTANCE_KEY, 50 );
      distance = 50;
    }
    distancetext.setText( convertProgressToMileage( distance ) + " miles away" );
    distance_seekbar.setProgress( distance );

    searchresults_seekbar.setOnSeekBarChangeListener( new OnSeekBarChangeListener()
      {

        @Override
        public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser )
        {
          searchlimitText.setText( progress + " results" );
        }

        @Override
        public void onStartTrackingTouch( SeekBar seekBar )
        {
          // TODO Auto-generated method stub

        }

        @Override
        public void onStopTrackingTouch( SeekBar seekBar )
        {
          writeSharedPrefsInt( SEARCH_RESULTS_KEY, seekBar.getProgress() );
        }

      } );
    distance_seekbar.setOnSeekBarChangeListener( new OnSeekBarChangeListener()
      {

        @Override
        public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser )
        {
          distancetext.setText( convertProgressToMileage( progress ) + " miles away" );
        }

        @Override
        public void onStartTrackingTouch( SeekBar seekBar )
        {
        }

        @Override
        public void onStopTrackingTouch( SeekBar seekBar )
        {
          writeSharedPrefsInt( DISTANCE_KEY, seekBar.getProgress() );
        }
      } );

    aboutTv.setOnClickListener( new OnClickListener()
      {
        @Override
        public void onClick( View v )
        {

          /*
           * FragmentTransaction ft = fm.beginTransaction(); ft.replace( R.id.content_fragment, fragment,
           * AnnouncementsListFragment.class.getName() ); ft.commit();
           */
          //startActivity( new Intent( _context, Settings.class ) );
        }
      } );
    openSource.setOnClickListener( new OnClickListener()
      {
        @Override
        public void onClick( View v )
        {

        }
      } );
  }

  public static double milesToKm( double miles )
  {
    return miles * 1609.3439999999999;
  }

  public static int FIRST_SECTION_LIMIT = 35;
  public static double FIRST_SECTION_MULTIPLIER = 0.10; //3.5 + 17.5

  public static int SECOND_SECTION_LIMIT = 70;
  public static double SECOND_SECTION_MULTIPLIER = .5;

  public static double THIRD_SECTION_MULTIPLIER = 1;

  // |-- 0.1 icncr --|-- 0.5 incr --|-- 1.0 incr --|

  public static double convertProgressToMileage( int num )
  {
    num++;// start at 1
    double retVal = num;
    if ( num >= FIRST_SECTION_LIMIT )
    {
      retVal = FIRST_SECTION_MULTIPLIER * FIRST_SECTION_LIMIT;
    }
    else
    {
      return Math.floor( ( retVal * FIRST_SECTION_MULTIPLIER ) * 100 ) / 100;
    }
    if ( num >= SECOND_SECTION_LIMIT )
    {
      retVal += ( SECOND_SECTION_LIMIT - FIRST_SECTION_LIMIT ) * SECOND_SECTION_MULTIPLIER;
    }
    else
    {
      return Math.floor( ( retVal + ( ( num - FIRST_SECTION_LIMIT ) * SECOND_SECTION_MULTIPLIER ) ) * 100 ) / 100;
    }
    return Math.floor( ( retVal + num - SECOND_SECTION_LIMIT ) * 100 ) / 100;
  }
  // 35, .10 incr  3.5 miles
  // 70, .5 incr 17.5 miles
  // 100, 1 incr 51 miles
}
