package com.potato.burritohunter.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.potato.burritohunter.R;
import com.potato.burritohunter.stuff.BurritoClickListeners.MapOnMarkerClickListener;

// this class should contain the map logic now...
public class MyOtherMapFragment extends SherlockFragment
{
  public static LatLng PIVOT = new LatLng( 37.798052, -122.406278 );
  private SupportMapFragment mMapFragment;
  private GoogleMap map;

  // i kinda cringe at this being static but owell luls
  public static TextView paneTitle;
  public static TextView paneDescription;
  public static CheckBox checkBox;
  public static Marker pivotMarker;

  // solution shamelessly stolen from
  // http://stackoverflow.com/questions/17476089/android-google-maps-fragment-and-viewpager-error-inflating-class-fragment
  @Override
  public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
  {
    View vw = inflater.inflate( R.layout.my_other_map_fragment, container, false );
    mMapFragment = ( (SupportMapFragment) getFragmentManager().findFragmentById( R.id.map_frag ) );
    map = mMapFragment.getMap();
    initMap( map );
    paneTitle = (TextView) vw.findViewById( R.id.trans_pane_title );
    paneDescription = (TextView) vw.findViewById( R.id.trans_pane_description );
    checkBox = (CheckBox) vw.findViewById( R.id.trans_pane_checkbox );
    return vw;

  }

  public GoogleMap getMap()
  {
    return map;
  }

  private void initMap( GoogleMap map )
  {
    UiSettings settings = map.getUiSettings();
    settings.setAllGesturesEnabled( true );
    settings.setMyLocationButtonEnabled( true );

    map.moveCamera( CameraUpdateFactory.newLatLngZoom( PIVOT, 16 ) );
    pivotMarker = map.addMarker( new MarkerOptions().position( PIVOT ).draggable( true )
        .icon( BitmapDescriptorFactory.fromResource( R.drawable.abs__ic_clear_disabled ) ) );
    map.setOnMarkerDragListener( new OnMarkerDragListener(){

      @Override
      public void onMarkerDrag( Marker marker )
      {
        // TODO Auto-generated method stub
        
      }

      @Override
      public void onMarkerDragEnd( Marker marker )
      {
        PIVOT = marker.getPosition();
        
      }

      @Override
      public void onMarkerDragStart( Marker marker )
      {
        // TODO Auto-generated method stub
        
      }
      
    });
    

    map.setOnMarkerClickListener( new MapOnMarkerClickListener() );
  }

  public void onDestroyView()
  {
    super.onDestroyView();

    try
    {
      Fragment fragment = ( getFragmentManager().findFragmentById( R.id.map_frag ) );
      FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
      ft.remove( fragment );
      ft.commit();
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
  }

  public static void setTitleDescriptionCheckbox( String title, String description, boolean checkbox )
  {
    paneTitle.setText( title );
    paneDescription.setText( description );
    checkBox.setChecked( checkbox );
  }
}
