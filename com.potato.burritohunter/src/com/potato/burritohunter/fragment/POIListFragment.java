package com.potato.burritohunter.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.potato.burritohunter.adapter.SavedListAdapter;
import com.potato.burritohunter.database.SavedListItem;

public class POIListFragment extends ListFragment
{
  private List <SavedListItem> poiList; //make this parcelable?
  //TODO
  //please read this http://marakana.com/s/post/1250/android_fragments_tutorial
  String[] countries = new String[] { "India", "Pakistan", "Sri Lanka", "China", "Bangladesh", "Nepal", "Afghanistan",
                                     "North Korea", "South Korea", "Japan" };
  // every fragment must have a default ctor so don't override that
  
  @Override
  public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
  {
    
    /*ArrayAdapter<String> adapter = new ArrayAdapter<String>( inflater.getContext(),
                                                             android.R.layout.simple_list_item_1, countries );*/
    SavedListAdapter adapter = new SavedListAdapter (inflater.getContext(), poiList);
    setListAdapter( adapter );

    return super.onCreateView( inflater, container, savedInstanceState );

    //use this to make a new adapter
    //http://theopentutorials.com/tutorials/android/listview/android-custom-listview-with-image-and-text-using-baseadapter/
  }
  public void setPoiList (  List <SavedListItem> poiList ) // probably considered sad shitty design, use parcelable instead 
  {
    this.poiList = poiList;
  }
}
