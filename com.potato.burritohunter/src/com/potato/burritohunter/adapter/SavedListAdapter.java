// need 'no image' icon, a loading icon
package com.potato.burritohunter.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.potato.burritohunter.R;
import com.potato.burritohunter.database.DatabaseUtil;
import com.potato.burritohunter.database.SavedListItem;
import com.potato.burritohunter.stuff.GalleryPoiList;
import com.potato.burritohunter.stuff.SearchResult;

public class SavedListAdapter extends BaseAdapter// can practice using variant/invariant generics here!
{
  private Fragment frag;
  private List<SavedListItem> rowItems;
  private List<GalleryPoiList> gallery = new ArrayList<GalleryPoiList>();

  public SavedListAdapter( Fragment frag, List<SavedListItem> rowItems )
  {
    this.frag = frag;
    this.rowItems = rowItems;
  }

  /* private view holder class */
  public static class ViewHolder
  {
    ImageView imageView;
    TextView txtTitle;
    TextView txtDesc;
    public long id;
    GalleryPoiList galleryPoiList;
  }

  private List<String> getPhotoUrls( int position )
  {
    List<String> photoUrls = new ArrayList<String>();
    List<SearchResult> searchResults = DatabaseUtil.getDatabaseHelper().retrievePoints( rowItems.get( position )._id
                                                                                            + "" );
    for ( SearchResult sr : searchResults )
    {
      photoUrls.add( sr.photoIcon );
    }
    return photoUrls;
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
      holder.galleryPoiList = new GalleryPoiList( holder.imageView, getPhotoUrls( position ) );
      gallery.add( holder.galleryPoiList );

      holder.galleryPoiList.start();
      convertView.setTag( holder );
    }
    else
    {
      holder = (ViewHolder) convertView.getTag();
    }

    SavedListItem rowItem = (SavedListItem) getItem( position );

    holder.txtDesc.setText( "luls this is the primary key " + rowItem.get_id() );
    holder.txtTitle.setText( rowItem.get_title() );
    holder.galleryPoiList.setViewsAndUrls( holder.imageView, getPhotoUrls( position ) );
    holder.id = rowItem._id;
    //holder.imageView.setImageResource(rowItem.getImageId());

    return convertView;
  }

  public void whenFragmentOnStop()
  {
    for( GalleryPoiList threadGallery : gallery )
    {
      threadGallery.stopFlipping();
    }
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
