package com.potato.burritohunter.fragment;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.potato.burritohunter.adapter.SavedListAdapter;
import com.potato.burritohunter.database.SavedListItem;

// funfax: every fragment must have a default ctor so don't override that
//TODO http://marakana.com/s/post/1250/android_fragments_tutorial

//use this to make a new adapter
//http://theopentutorials.com/tutorials/android/listview/android-custom-listview-with-image-and-text-using-baseadapter/
public class POIListFragment extends SherlockListFragment
{
  private List <SavedListItem> poiList; //make this parcelable?

  @Override
  public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
  {
    SavedListAdapter adapter = new SavedListAdapter (this, poiList);
    setListAdapter( adapter );

    return super.onCreateView( inflater, container, savedInstanceState );
  }
  public void setPoiList (  List <SavedListItem> poiList )
  { //this is sad shitty design, TODO use parcelable instead 
    this.poiList = poiList;
  }

  @Override
  public void onListItemClick( ListView parent, View view, int position, long id )
  {
    startActivity (new Intent( "com.potato.burritohunter.activity.PageDetails"));
  }
}
