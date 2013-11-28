package com.potato.burritohunter.fragment;

import java.math.BigDecimal;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.Marker;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.potato.burritohunter.R;
import com.potato.burritohunter.activity.MapActivity;
import com.potato.burritohunter.stuff.BurritoClickListeners;
import com.potato.burritohunter.stuff.SearchResult;
import com.potato.burritohunter.stuff.SomeUtil;
import com.potato.burritohunter.stuff.Spot;

public class SampleListFragment extends ListFragment
{

  public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
  {
    return inflater.inflate( R.layout.list, null );
  }

  @Override
  public void onListItemClick( ListView l, View v, int position, long id )
  {
    super.onListItemClick( l, v, position, id );
    Marker m = (Marker) getListAdapter().getItem( position );
    MapActivity.instance.getSlidingMenu().toggle( true );
    BurritoClickListeners.onMarkerClicked( m );

    MyOtherMapFragment.map.animateCamera( CameraUpdateFactory.newLatLng( m.getPosition() ) );



  }

  public static class SlidingMenuAdapter extends ArrayAdapter<Marker>
  {

    public SlidingMenuAdapter( Context context )
    {
      super( context, 0 );
    }

    public View getView( int position, View convertView, ViewGroup parent )
    {
      if ( convertView == null )
      {
        convertView = LayoutInflater.from( getContext() ).inflate( R.layout.sliding_menu_item, null );
      }
      TextView title = (TextView) convertView.findViewById( R.id.title );
      Marker key = getItem( position );
      SearchResult searchResult = MapActivity.currentSearchResults.get( key );

      ImageView iv = (ImageView) convertView.findViewById( R.id.icon_sliding );
      ImageLoader.getInstance().displayImage( searchResult.photoIcon, iv, SomeUtil.getImageOptions() );
      title.setText( searchResult._name );
      TextView desc = (TextView) convertView.findViewById( R.id.desc );
      desc.setText( searchResult.address );
      ImageView ratingImage = (ImageView) convertView.findViewById( R.id.rating_slidingIV );
      boolean selected = MapActivity.selectedSearchResults.contains( key );
      ratingImage.setImageBitmap( Spot.ratingToHollowBitmap( searchResult.rating, false ) );
      TextView ratingText = (TextView) convertView.findViewById( R.id.rating_slidingTV );
      double rating = round (searchResult.rating, 1);
      ratingText.setText( rating + "" );

      // CheckBox checkBox = (CheckBox) convertView.findViewById( R.id.sliding_menu_checkbox );
      //checkBox.setOnCheckedChangeListener( new OnCheckedChangeListener() { 
      //  @Override
      //  public void onCheckedChanged( CompoundButton buttonView, boolean isChecked )
      //  {
      //  }
      //});
      //checkBox.setChecked( MapActivity.selectedSearchResults.contains( key ) );
      return convertView;
    }
    public static double round(double value, int places) {
      if (places < 0) throw new IllegalArgumentException();

      BigDecimal bd = new BigDecimal(value);
      bd = bd.setScale(places, BigDecimal.ROUND_HALF_UP);
      return bd.doubleValue();
  }
  }
}
