package com.potato.burritohunter.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.potato.burritohunter.R;
import com.potato.burritohunter.stuff.SearchResult;

public class BottomPagerMarkerPanel extends SherlockFragment
{
  public TextView _title;
  public TextView _desc;

  public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
  {
    View vw = inflater.inflate( R.layout.bottom_pager_marker_layout, container, false );
    this._title = (TextView) vw.findViewById(R.id.bottomPagerMarkerTitle);
    this._desc = (TextView) vw.findViewById(R.id.bottomPagerMarkerDesc);
    return vw;
  }
  
  // decouple using a dedicated bean, but searchresult works for now
  public void setViews ( SearchResult sr )
  {
    _title.setText( sr._name );
    _desc.setText( sr.address );
  }
}