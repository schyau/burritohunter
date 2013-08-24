package com.potato.burritohunter.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter 
{
  List<Fragment> fragments;

  public ViewPagerAdapter( FragmentManager mgr )
  {
    super( mgr );
    fragments = new ArrayList<Fragment>();
  }

  @Override
  public Fragment getItem( int position )
  {
    return fragments.get( position );
  }

  @Override
  public int getCount()
  {
    return fragments == null ? 0 : fragments.size();
  }
  
  public void addFragment ( Fragment frag )
  {
    fragments.add( frag );
  }
}
