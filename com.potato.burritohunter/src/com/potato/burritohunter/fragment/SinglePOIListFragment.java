package com.potato.burritohunter.fragment;

import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.potato.burritohunter.adapter.SinglePOIListAdapter;
import com.potato.burritohunter.stuff.SearchResult;


public class SinglePOIListFragment extends SherlockListFragment
{
  private List<SearchResult> singlePOIs;

  public void setSinglePOIs( List<SearchResult> singlePOIs)
  {
    this.singlePOIs = singlePOIs;
  }

  @Override
  public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
  {
    
    /*ArrayAdapter<String> adapter = new ArrayAdapter<String>( inflater.getContext(),
                                                             android.R.layout.simple_list_item_1, countries );*/
    setAdapter();

    return super.onCreateView( inflater, container, savedInstanceState );

    //use this to make a new adapter
    //http://theopentutorials.com/tutorials/android/listview/android-custom-listview-with-image-and-text-using-baseadapter/
  }
  
  //remove from here and try to set somewhere else
  public void setAdapter()
  {
    SinglePOIListAdapter adapter = new SinglePOIListAdapter (this, singlePOIs);
    setListAdapter( adapter );
  }


  @Override
  public void onListItemClick( ListView parent, View view, int position, long id )
  {
    SearchResult searchResult = (SearchResult) parent.getItemAtPosition(position);
    String searchResultId = searchResult.id;

    String url = "https://foursquare.com/v/"+searchResultId;
    Intent i = new Intent(Intent.ACTION_VIEW);
    i.setData(Uri.parse(url));
    startActivity(i);
  }
}