package com.potato.burritohunter.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.potato.burritohunter.R;
import com.potato.burritohunter.stuff.BottomPagerPanel;
import com.potato.burritohunter.stuff.BurritoClickListeners;
import com.potato.burritohunter.stuff.SearchResult;
import com.potato.burritohunter.stuff.SomeUtil;

public class BottomPagerMarkerPanel extends SherlockFragment
{
  // hello.
  // i'd like to take this time to make a formal apology to my species.  i am an idiot and i'm sorry.
  // these should-be instance variables are now class variables
  // have a good day.
  private static TextView _title;
  private static TextView _desc;
  private static ImageButton _bottomPagerRightArrow;
  public static ViewPager _viewPager;
  public static ImageView _imageIcon;

  public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
  {
    View vw = inflater.inflate( R.layout.bottom_pager_marker_layout, container, false );
    _title = (TextView) vw.findViewById( R.id.bottomPagerMarkerTitle );
    _desc = (TextView) vw.findViewById( R.id.bottomPagerMarkerDesc );
    _bottomPagerRightArrow = (ImageButton) vw.findViewById( R.id.bottomPagerRightArrow ); //do we need it?
    _imageIcon = (ImageView) vw.findViewById( R.id.bottom_pager_marker_picture );
    _imageIcon.setOnClickListener( new BurritoClickListeners.OnBottomMarkerPanelPictureClickListener() );
    _bottomPagerRightArrow.setOnClickListener( new BurritoClickListeners.RightButtonNavigation() );
    return vw;
  }

  // decouple using a dedicated bean, but searchresult works for now
  public void setViews( SearchResult sr )
  {
    _title.setText( sr._name );
    _desc.setText( sr.address );
    
    // this is safe passing in null url
    ImageLoader.getInstance().displayImage( sr.photoIcon, _imageIcon, SomeUtil.getImageOptions() );
    // TODO add a listener too
  }

  public void setViewPager( ViewPager viewPager )
  {
    _viewPager = viewPager;
  }
}
