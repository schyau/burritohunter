package com.potato.burritohunter.stuff;

import android.annotation.SuppressLint;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.potato.burritohunter.R;
import com.potato.burritohunter.adapter.ViewPagerAdapter;
import com.potato.burritohunter.fragment.BottomPagerButtonsPanel;
import com.potato.burritohunter.fragment.BottomPagerMarkerPanel;

// when marker is chosen, pane goes 

public class BottomPagerPanel
{
  private ViewPager _viewPager; 
  private ViewPagerAdapter _viewPagerAdapter;
  private static BottomPagerPanel _instance;

  private BottomPagerButtonsPanel _buttonsPanel;
  private BottomPagerMarkerPanel _markerPanel;

  public static BottomPagerPanel getInstance()
  {
    return _instance;
  }
  private BottomPagerPanel() {}
  
  public static void makeInstance( View vw, SherlockFragmentActivity fragmentActivity )
  {
    _instance = new BottomPagerPanel(); //race condition ?  double checked locking
    _instance.init(vw, fragmentActivity);
  }

  @SuppressLint( "NewApi" )
  private void init(View vw,SherlockFragmentActivity fragmentActivity)
  {
    _buttonsPanel = new BottomPagerButtonsPanel();
    _markerPanel = new BottomPagerMarkerPanel();

    _viewPager = (ViewPager) vw.findViewById(R.id.pagerBottom);

    _viewPagerAdapter = new ViewPagerAdapter( fragmentActivity.getSupportFragmentManager() );

    _viewPagerAdapter.addFragment( _buttonsPanel );
    _viewPagerAdapter.addFragment( _markerPanel );

    _viewPager.setAdapter( _instance._viewPagerAdapter );
    _viewPager.setCurrentItem( 0 );
  }

  public void setMarkerPanel(SearchResult sr )
  {
    _markerPanel.setViews( sr );
  }

  public void disableMarkerPanel ()
  {
    // if already on markerpanel
    if ( _viewPager.getCurrentItem() == 1 )
    {
      _viewPager.setCurrentItem( 0, true );      
    }
    // chyauchyau TODO pager code to disable moving to marker panel
  }
  
  public void enableMarkerPanel (SearchResult sr )
  {
    if ( _viewPager.getCurrentItem() == 0 )
    {
      _viewPager.setCurrentItem( 1, true );
    }
    _markerPanel.setViews( sr );
  }

  
}