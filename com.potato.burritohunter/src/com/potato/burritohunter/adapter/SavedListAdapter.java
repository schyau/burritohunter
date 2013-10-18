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
import android.widget.ViewFlipper;

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
    public ImageView imageView;
    public ImageView imageView1;
    public ViewFlipper viewFlipper;
    public TextView txtTitle;
    public TextView txtDesc;
    public long id;
    public GalleryPoiList galleryPoiList;
    public TextView numViews;
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

  public void removeItem(int location)
  {
    if (rowItems!=null)
    {
      rowItems.remove( location );
    }
    //else{why is this null!??}
  }
  
  @Override
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
      holder.imageView1 = (ImageView) convertView.findViewById( R.id.icon1 );
      holder.viewFlipper = (ViewFlipper) convertView.findViewById( R.id.view_flipper );
      holder.galleryPoiList = new GalleryPoiList( holder, getPhotoUrls( position ) );
      holder.numViews = (TextView) convertView.findViewById( R.id.poiCount );
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
    List<String> photoUrlsList = getPhotoUrls( position );
    holder.galleryPoiList.setViewsAndUrls( photoUrlsList );
    holder.galleryPoiList.start();
    holder.id = rowItem._id;
    String numViewsStr = "";
    if ( photoUrlsList == null )
    {
      numViewsStr = "0";
    }
    numViewsStr = ""+photoUrlsList.size();     
   
    holder.numViews.setText( numViewsStr );
    //holder.imageView.setImageResource(rowItem.getImageId());

    return convertView;
  }

  public void whenFragmentOnStop()
  {
    for ( GalleryPoiList threadGallery : gallery )
    {
      threadGallery.stopFlippingNaoJUSTSTHAPPP();
    }
  }

  public void startFlipping()
  {
    for ( GalleryPoiList threadGallery : gallery )
    {
      threadGallery.start();
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
