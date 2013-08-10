package com.potato.burritohunter.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class POIListFragment extends ListFragment
{
  //TODO
  //please read this http://marakana.com/s/post/1250/android_fragments_tutorial
  String[] countries = new String[] { "India", "Pakistan", "Sri Lanka", "China", "Bangladesh", "Nepal", "Afghanistan",
                                     "North Korea", "South Korea", "Japan" };

  @Override
  public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
  {
    
    ArrayAdapter<String> adapter = new ArrayAdapter<String>( inflater.getContext(),
                                                             android.R.layout.simple_list_item_1, countries );

    
    setListAdapter( adapter );

    return super.onCreateView( inflater, container, savedInstanceState );

    //use this to make a new adapter
    //http://theopentutorials.com/tutorials/android/listview/android-custom-listview-with-image-and-text-using-baseadapter/
  }
}
