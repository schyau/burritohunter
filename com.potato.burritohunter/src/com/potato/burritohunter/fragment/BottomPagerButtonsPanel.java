package com.potato.burritohunter.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.potato.burritohunter.R;

public class BottomPagerButtonsPanel extends SherlockFragment
{
  @SuppressLint( "NewApi" )
  public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
  {
    View vw = inflater.inflate( R.layout.bottom_pager_button_layout, container, false );
    //chyauchyau: set pane here
    return vw;
  }
}