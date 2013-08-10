// need 'no image' icon, a loading icon
package com.potato.burritohunter.adapter;

import java.util.List;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.potato.burritohunter.R;
import com.potato.burritohunter.database.DatabaseUtil;
import com.potato.burritohunter.database.SavedListItem;
import com.potato.burritohunter.fragment.SinglePOIListFragment;
import com.potato.burritohunter.stuff.SearchResult;

public class SavedListAdapter extends BaseAdapter// can practice using variant/invariant generics here!
{
  private Fragment frag;
  private List<SavedListItem> rowItems;

  public SavedListAdapter( Fragment frag, List<SavedListItem> rowItems )
  {
    this.frag = frag;
    this.rowItems = rowItems;
  }

  /* private view holder class */
  private class ViewHolder
  {
    ImageView imageView;
    TextView txtTitle;
    TextView txtDesc;
  }

  public View getView( int position, View convertView, ViewGroup parent )
  {
    ViewHolder holder = null;

    LayoutInflater mInflater = (LayoutInflater) frag.getActivity().getSystemService( Activity.LAYOUT_INFLATER_SERVICE );
    if ( convertView == null )
    {
      convertView = mInflater.inflate( R.layout.saved_list_item, null );
      holder = new ViewHolder();
      holder.txtDesc = (TextView) convertView.findViewById( R.id.desc );
      holder.txtTitle = (TextView) convertView.findViewById( R.id.title );
      holder.imageView = (ImageView) convertView.findViewById( R.id.icon );
      convertView.setTag( holder );
    }
    else
    {
      holder = (ViewHolder) convertView.getTag();
    }

    SavedListItem rowItem = (SavedListItem) getItem( position );

    holder.txtDesc.setText( "luls this is the primary key " + rowItem.get_id() );
    holder.txtTitle.setText( rowItem.get_title() );
    //holder.imageView.setImageResource(rowItem.getImageId());

    return convertView;
  }

  @Override
  public int getCount()
  {
    return rowItems.size();
  }

  @Override
  public Object getItem( int position )
  {
    return rowItems.get( position );
  }

  @Override
  public long getItemId( int position )
  {
    return rowItems.indexOf( getItem( position ) );
  }

}
