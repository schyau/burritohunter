package com.potato.burritohunter.adapter;

import java.util.ArrayList;
import java.util.List;

import com.potato.burritohunter.R;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

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
  
  // http://stackoverflow.com/questions/13664155/dynamically-add-and-remove-view-to-viewpager
  public int removeView (ViewPager pager, int position)
  {
    // ViewPager doesn't have a delete method; the closest is to set the adapter
    // again.  When doing so, it deletes all its views.  Then we can delete the view
    // from from the adapter and finally set the adapter to the pager again.  Note
    // that we set the adapter to null before removing the view from "views" - that's
    // because while ViewPager deletes all its views, it will call destroyItem which
    // will in turn cause a null pointer ref.
    pager.setAdapter (null);
    fragments.remove (position);
    pager.setAdapter (this);

    return position;
  }
  public int replaceView (ViewPager pager, int position, Fragment frag)
  {
    // ViewPager doesn't have a delete method; the closest is to set the adapter
    // again.  When doing so, it deletes all its views.  Then we can delete the view
    // from from the adapter and finally set the adapter to the pager again.  Note
    // that we set the adapter to null before removing the view from "views" - that's
    // because while ViewPager deletes all its views, it will call destroyItem which
    // will in turn cause a null pointer ref.
    pager.setAdapter (null);
    fragments.remove (position);
    fragments.add( frag);
    pager.setAdapter (this);

    return position;
  }
}
