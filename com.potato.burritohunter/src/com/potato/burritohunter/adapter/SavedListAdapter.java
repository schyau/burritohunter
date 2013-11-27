// need 'no image' icon, a loading icon
package com.potato.burritohunter.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
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

  private List<SearchResult> getSearchResults( int position )
  {
    List<String> photoUrls = new ArrayList<String>();
    List<SearchResult> searchResults = DatabaseUtil.getDatabaseHelper().retrievePoints( rowItems.get( position )._id
                                                                                            + "" );
    return searchResults;
  }

  private List<String> getPhotoUrls( List<SearchResult> searchResults )
  {
    List<String> photoUrls = new ArrayList<String>();
    for ( SearchResult sr : searchResults )
    {
      photoUrls.add( sr.photoIcon );
    }
    return photoUrls;
  }

  public void removeItem( int location )
  {
    if ( rowItems != null )
    {
      rowItems.remove( location );
    }
    //else{why is this null!??}
  }

  @Override
  public View getView( final int position, View convertView, ViewGroup parent )
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
      final ViewHolder fHolder = holder;
      ( new AsyncTask<Void, Void, Void>()
        {
          List<String> photoUrlsList;
          String description;

          @Override
          protected Void doInBackground( Void... params )
          {
            List<SearchResult> list = getSearchResults( position );
            photoUrlsList = getPhotoUrls( list );
            description = makeDescription( list );

            return null;
          }

          @Override
          protected void onPostExecute( Void result )
          {
            fHolder.galleryPoiList = new GalleryPoiList( fHolder, photoUrlsList );
            fHolder.galleryPoiList.start();
            fHolder.txtDesc.setText( description );
            gallery.add( fHolder.galleryPoiList );
            String numViewsStr = "";
            if ( photoUrlsList == null )
            {
              numViewsStr = "0";
            }
            numViewsStr = "" + photoUrlsList.size();

            fHolder.numViews.setText( numViewsStr );
          }

        } ).execute();

      holder.numViews = (TextView) convertView.findViewById( R.id.poiCount );

      convertView.setTag( holder );
    }
    else
    {
      holder = (ViewHolder) convertView.getTag();
      final ViewHolder fHolder = holder;
      ( new AsyncTask<Void, Void, Void>()
        {
          List<String> photoUrlsList;
          String description;

          @Override
          protected Void doInBackground( Void... params )
          {
            List<SearchResult> list = getSearchResults( position );
            photoUrlsList = getPhotoUrls( list );
            description = makeDescription( list );
            return null;
          }

          @Override
          protected void onPostExecute( Void val )
          {
            fHolder.galleryPoiList.setViewsAndUrls( photoUrlsList );
            fHolder.galleryPoiList.start();
            fHolder.txtDesc.setText( description );
            String numViewsStr = "";
            if ( photoUrlsList == null )
            {
              numViewsStr = "0";
            }
            numViewsStr = "" + photoUrlsList.size();

            fHolder.numViews.setText( numViewsStr );
          }

        } ).execute();

    }

    SavedListItem rowItem = (SavedListItem) getItem( position );

    holder.txtDesc.setText( "luls this is the primary key " + rowItem.get_id() );
    holder.txtTitle.setText( rowItem.get_title() );
    holder.id = rowItem._id;
    //holder.imageView.setImageResource(rowItem.getImageId());

    return convertView;
  }

  private String makeDescription( List<SearchResult> searchResults )
  {
    if ( searchResults == null || searchResults.size() == 0 )
    {
      return "There's nothing in this list!";
    }
    StringBuilder sb = new StringBuilder();
    Iterator<SearchResult> iterator = searchResults.iterator();
    int count = 0;
    while ( iterator.hasNext() )
    {
      SearchResult sr = iterator.next();
      if ( searchResults.size() == 1 )
      {
        return sr._name;
      }
      if ( iterator.hasNext() )
      {
        if(count != 0)
        {
          sb.append(", " );
        }
        sb.append( sr._name );
      }
      else
      {
        sb.append( " and " + sr._name );
      }
      count++;
    }
    return sb.toString();
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
