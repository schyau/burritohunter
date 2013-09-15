package com.potato.burritohunter.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import com.potato.burritohunter.activity.MapActivity;
import com.potato.burritohunter.stuff.BurritoClickListeners;
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

  private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

  // solution shamelessly stolen from
  // http://stackoverflow.com/questions/17476089/android-google-maps-fragment-and-viewpager-error-inflating-class-fragment
  @Override
  public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
  {
    View vw = inflater.inflate( R.layout.my_other_map_fragment, container, false );

    mMapFragment = ( (SupportMapFragment) getFragmentManager().findFragmentById( R.id.map_frag ) );
    map = mMapFragment.getMap();
    vw.findViewById( R.id.find_me )
        .setOnClickListener( new BurritoClickListeners.FindMeOnClickListener( MapActivity.instance, this ) );
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

  public void updateAndDrawPivot( LatLng latLng )
  {
    PIVOT = latLng;
    if ( pivotMarker != null )
    {
      pivotMarker.remove();
    }
    pivotMarker = map.addMarker( new MarkerOptions().position( PIVOT ).draggable( true )
        .icon( BitmapDescriptorFactory.fromResource( R.drawable.abs__ab_bottom_solid_dark_holo ) ) ); 

    pivotMarker.setPosition( PIVOT );
  }

  @Override
  public void onStart()
  {
    super.onStart();
    // Connect the client.
    MapActivity.mLocationClient.connect();
  }

  @Override
  public void onStop()
  {
    // Disconnecting the client invalidates it.
    MapActivity.mLocationClient.disconnect();
    super.onStop();
  }

  private void initMap( GoogleMap map )
  {
    UiSettings settings = map.getUiSettings();
    settings.setAllGesturesEnabled( true );
    settings.setMyLocationButtonEnabled( true );

    map.moveCamera( CameraUpdateFactory.newLatLngZoom( PIVOT, 16 ) );
    pivotMarker = map.addMarker( new MarkerOptions().position( PIVOT ).draggable( true )
        .icon( BitmapDescriptorFactory.fromResource( R.drawable.abs__ab_bottom_solid_dark_holo ) ) );
    map.setOnMarkerDragListener( new OnMarkerDragListener()
      {
        @Override
        public void onMarkerDrag( Marker marker )
        {
        }

        @Override
        public void onMarkerDragEnd( Marker marker )
        {
          PIVOT = marker.getPosition();
        }

        @Override
        public void onMarkerDragStart( Marker marker )
        {
        }
      } );
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

  @Override
  public void onActivityResult( int requestCode, int resultCode, Intent data )
  {
    // Decide what to do based on the original request code
    switch ( requestCode )
    {
      case CONNECTION_FAILURE_RESOLUTION_REQUEST:
        //if activity's result is okay
        switch ( resultCode )
        {
          case Activity.RESULT_OK:
            //try request again... asin getlastknownlocation?
            break;
        }
    }
  }

  public static void setTitleDescriptionCheckbox( String title, String description, boolean checkbox )
  {
    paneTitle.setText( title );
    paneDescription.setText( description );
    checkBox.setChecked( checkbox );
  }

  // Define a DialogFragment that displays the error dialog
  public static class ErrorDialogFragment extends DialogFragment
  {
    // Global field to contain the error dialog
    private Dialog mDialog;

    // Default constructor. Sets the dialog field to null
    public ErrorDialogFragment()
    {
      super();
      mDialog = null;
    }

    // Set the dialog to display
    public void setDialog( Dialog dialog )
    {
      mDialog = dialog;
    }

    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState )
    {
      return mDialog;
    }
  }

}
