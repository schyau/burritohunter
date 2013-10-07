package com.potato.burritohunter.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.potato.burritohunter.R;
import com.potato.burritohunter.stuff.BottomPagerPanel;
import com.potato.burritohunter.stuff.SearchResult;

public class BottomPagerMarkerPanel extends SherlockFragment
{
  private TextView _title;
  private TextView _desc;
  private ImageButton _bottomPagerRightArrow;
  private ViewPager _viewPager;

  public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
  {
    View vw = inflater.inflate( R.layout.bottom_pager_marker_layout, container, false );
    this._title = (TextView) vw.findViewById( R.id.bottomPagerMarkerTitle );
    this._desc = (TextView) vw.findViewById( R.id.bottomPagerMarkerDesc );
    this._bottomPagerRightArrow = (ImageButton) vw.findViewById( R.id.bottomPagerRightArrow ); //do we need it?
    _bottomPagerRightArrow.setOnClickListener( new OnClickListener(){

      @Override
      public void onClick( View v )
      {
        _viewPager.setCurrentItem( BottomPagerPanel.PANEL_BUTTONS, true );
      }
      
    });
    return vw;
  }

  // decouple using a dedicated bean, but searchresult works for now
  public void setViews( SearchResult sr )
  {
    _title.setText( sr._name );
    _desc.setText( sr.address );
  }

  public void setViewPager ( ViewPager viewPager)
  {
    this._viewPager = viewPager;
  }
}
