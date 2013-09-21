package com.potato.burritohunter.fragment;

import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.potato.burritohunter.activity.MapActivity;
import com.potato.burritohunter.adapter.SavedListAdapter;
import com.potato.burritohunter.database.DatabaseUtil;
import com.potato.burritohunter.database.SavedListItem;
import com.potato.burritohunter.stuff.SearchResult;

// funfax: every fragment must have a default ctor so don't override that
//TODO http://marakana.com/s/post/1250/android_fragments_tutorial

//use this to make a new adapter
//http://theopentutorials.com/tutorials/android/listview/android-custom-listview-with-image-and-text-using-baseadapter/
public class POIListFragment extends SherlockListFragment
{
  static SinglePOIListFragment single = new SinglePOIListFragment();
  private List <SavedListItem> poiList; //make this parcelable?

  @Override
  public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
  {
    SavedListAdapter adapter = new SavedListAdapter (this, poiList);
    setListAdapter( adapter );

    View vw = super.onCreateView( inflater, container, savedInstanceState );
    vw.setBackgroundColor( Color.WHITE );
    return vw;
  }
  public void setPoiList (  List <SavedListItem> poiList )
  { //this is sad shitty design, TODO use parcelable instead 
    this.poiList = poiList;
  }

  @Override
  public void onListItemClick( ListView parent, View view, int position, long id )
  {
    SavedListItem savedListItem = (SavedListItem) parent.getItemAtPosition(position);
    long foreignKey = savedListItem._id;
    List<SearchResult> list = DatabaseUtil.getDatabaseHelper().retrievePoints( foreignKey+"" );
    
    single.setSinglePOIs(list);
    single.setAdapter();

    // will have to expand on this when we get to the fourth one
    if (MapActivity.viewPagerAdapter.getCount() == 4 )
    {
      MapActivity.viewPagerAdapter.removeView( MapActivity.viewPager,3 );
    }
    if (MapActivity.viewPagerAdapter.getCount() == 3 )
    {
      MapActivity.viewPagerAdapter.replaceView( MapActivity.viewPager,2, single );
    }
    else
    {
      MapActivity.viewPagerAdapter.addFragment( single );
    }
    MapActivity.viewPagerAdapter.notifyDataSetChanged();
    MapActivity.viewPager.setCurrentItem( 2 );
  }
}
