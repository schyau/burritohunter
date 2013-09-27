package com.potato.burritohunter.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.Marker;
import com.potato.burritohunter.R;
import com.potato.burritohunter.activity.MapActivity;
import com.potato.burritohunter.stuff.SearchResult;

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
    MyOtherMapFragment.changeMarkerState( m );
    MyOtherMapFragment.setPanenlText( MapActivity.currentSearchResults.get( m ) );
    MyOtherMapFragment.map.animateCamera( CameraUpdateFactory.newLatLng( m.getPosition() ) );
    MyOtherMapFragment.paneMarker = m;

    m.showInfoWindow();
    Log.d( "asdf", "asdlfkja;sdf" );
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
      title.setText( searchResult._name );
      TextView desc = (TextView) convertView.findViewById( R.id.desc );
      desc.setText( searchResult.address );

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

  }
}
