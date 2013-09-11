package com.potato.burritohunter.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.potato.burritohunter.R;
import com.potato.burritohunter.stuff.SearchResult;

public class DetailFragment extends SherlockFragment
{
  public SearchResult detail;
  
  public DetailFragment () {super();}
  private TextView _textView ;
  
  //should use setarguments instead
  public void setDetail ( SearchResult detail){
    this.detail = detail;
  }
  public View onCreateView ( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
  {
    //super.onCreateView ( inflater, container, savedInstanceState ); //is this needed?
    View v = inflater.inflate( R.layout.search_result_detail, null );
    _textView = (TextView)v.findViewById( R.id.title );
    return v;
    // look up how to display title
  }
  
  @Override
  public void onActivityCreated(Bundle b)
  {
    super.onActivityCreated( b );
    _textView.setText ( detail._name);
  }
}