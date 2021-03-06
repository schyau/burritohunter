package com.potato.burritohunter.stuff;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.potato.burritohunter.R;
import com.potato.burritohunter.adapter.ViewPagerAdapter;
import com.potato.burritohunter.fragment.BottomPagerButtonsPanel;
import com.potato.burritohunter.fragment.BottomPagerMarkerPanel;

// when marker is chosen, pane goes

public class BottomPagerPanel
{
  public static MyViewPager _viewPager;
  private ViewPagerAdapter _viewPagerAdapter;
  private static transient BottomPagerPanel _instance;

  private static BottomPagerButtonsPanel _buttonsPanel;
  private static BottomPagerMarkerPanel _markerPanel;

  
  public static final int PANEL_MARKER = 0;
  public static final int PANEL_BUTTONS = 1; // buttons is first one displayed.

  public static BottomPagerPanel getInstance()
  {
    return _instance;
  }

  private BottomPagerPanel()
  {
  }

  public static void makeInstance( View vw, SherlockFragmentActivity fragmentActivity )
  { //if( _instance == null )
    //{
    _instance = new BottomPagerPanel(); //race condition ?  double checked locking
    _instance.init( vw, fragmentActivity );
    //}
  }

  @SuppressLint( "NewApi" )
  private void init( View vw, SherlockFragmentActivity fragmentActivity )
  {
    _buttonsPanel = new BottomPagerButtonsPanel();
    _markerPanel = new BottomPagerMarkerPanel();

    //_viewPager = (MyViewPager) vw.findViewById( R.id.pagerBottom );
    _viewPager.setAdapter( null );
    _viewPagerAdapter = new ViewPagerAdapter( fragmentActivity.getSupportFragmentManager() );

    _viewPagerAdapter.setFirst( _markerPanel );
    _viewPagerAdapter.setSecond ( _buttonsPanel );

    _viewPager.setAdapter( _viewPagerAdapter );
    _viewPager.setCurrentItem( PANEL_BUTTONS );
    _buttonsPanel.setViewPager( _viewPager );
    _markerPanel.setViewPager( _viewPager );
  }

  public void setMarkerPanel( SearchResult sr )
  {
    _markerPanel.setViews( sr );
  }

  public void disableMarkerPanel()
  {
    // if already on markerpanel
    if ( _viewPager.getCurrentItem() == PANEL_MARKER )
    {
      _viewPager.setCurrentItem( PANEL_BUTTONS, true );
    }
    _viewPager.setPagingEnabled( false );
    _buttonsPanel.setNaviButtonClickable( false );
  }

  public void enableMarkerPanel( SearchResult sr )
  {
    if ( _viewPager.getCurrentItem() == PANEL_BUTTONS )
    {
      _viewPager.setCurrentItem( PANEL_MARKER, true );
    }
    _markerPanel.setViews( sr );
    _viewPager.setPagingEnabled( true );
    _buttonsPanel.setNaviButtonClickable( true );
  }

  public void setBottomPagerButtonsNumsSelectedTextView( String text )
  {
    _buttonsPanel.setNumSelectedTextView( text );
  }

  public void handleOnDestroy()
  {
    _viewPagerAdapter.removeView( _viewPager, 1 );
    _viewPagerAdapter.removeView( _viewPager, 0 );
  }
}
