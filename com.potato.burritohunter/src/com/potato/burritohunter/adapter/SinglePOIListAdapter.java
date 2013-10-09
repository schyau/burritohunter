package com.potato.burritohunter.adapter;

import java.util.List;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.potato.burritohunter.R;
import com.potato.burritohunter.activity.MapActivity;
import com.potato.burritohunter.stuff.SearchResult;

public class SinglePOIListAdapter extends BaseAdapter implements OnItemClickListener // can practice using variant/invariant generics here!
{
  Fragment frag;
  List<SearchResult> rowItems;

  public SinglePOIListAdapter( Fragment frag, List<SearchResult> rowItems )
  {
    this.frag = frag;
    this.rowItems = rowItems;
  }

  @Override
  public int getCount()
  {
    return rowItems.size();
  }

  @Override
  public Object getItem( int position )
  {
    return rowItems.get( position  ) ;
  }

  @Override
  public long getItemId( int position )
  {
    return rowItems.indexOf( getItem( position ) );
  }

  public class ViewHolder
  {
    TextView txtTitle;
    public String id;
  }

  @Override
  public View getView( int position, View convertView, ViewGroup parent )
  {
    // TODO Auto-generated method stub
    ViewHolder holder = null;

    LayoutInflater mInflater = (LayoutInflater) frag.getActivity().getSystemService( Activity.LAYOUT_INFLATER_SERVICE );
    if ( convertView == null )
    {
      convertView = mInflater.inflate( R.layout.single_poi_list_item, null );
      holder = new ViewHolder();
      holder.txtTitle = (TextView) convertView.findViewById( R.id.title );
      holder.id = ( (SearchResult) getItem( position ) ).id;
      convertView.setTag( holder );
    }
    else
    {
      holder = (ViewHolder) convertView.getTag();
    }

    SearchResult rowItem = (SearchResult) getItem( position );
    holder.txtTitle.setText( rowItem._name );
    return convertView;
  }

  @Override
  public void onItemClick( AdapterView<?> arg0, View arg1, int arg2, long arg3 )
  {
    // TODO lead to description page. 
  }
}
