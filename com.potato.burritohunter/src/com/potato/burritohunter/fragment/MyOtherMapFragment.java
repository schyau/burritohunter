package com.potato.burritohunter.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
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
  //public static LatLng PIVOT = new LatLng( 37.798052, -122.406278 );
  public static LatLng PIVOT = new LatLng( 0, 0 );
  private SupportMapFragment mMapFragment;
  private GoogleMap map;

  // i kinda cringe at this being static but owell luls
  public static TextView paneTitle;
  public static TextView paneDescription;
  public static CheckBox checkBox;
  public static Marker pivotMarker;

  private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

  /* save pivot to sharedprefs keys */
  private final static String PIVOT_LAT_KEY = "PIVOT LAT KEY";
  private final static String PIVOT_LNG_KEY = "PIVOT LNG KEY";

  /* save camerato shared prefs keys */
  private final static String CAMERA_ZOOM_KEY = "CAMERA ZOOM KEY";
  private final static String CAMERA_TILT_KEY = "CAMERA TILT KEY";
  private final static String CAMERA_BEARING_KEY = "CAMERA BEARING KEY";
  private final static String CAMERA_LAT_KEY = "CAMERA LAT KEY";
  private final static String CAMERA_LNG_KEY = "CAMERA LNG KEY";

  public static boolean shouldFindMe = false;

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

  @Override
  public void onCreate( Bundle b )
  {
    super.onCreate( b );

    //map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    /*
     * map.moveCamera( CameraUpdateFactory.newLatLngZoom( PIVOT, 16 ) ); pivotMarker = map.addMarker( new
     * MarkerOptions().position( PIVOT ).draggable( true ) .icon( BitmapDescriptorFactory.fromResource(
     * R.drawable.abs__ab_bottom_solid_dark_holo ) ) );
     */

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

    pivotMarker.setPosition( PIVOT ); // is this ncessary? I don't think so
    map.animateCamera( CameraUpdateFactory.newCameraPosition( CameraPosition.fromLatLngZoom( PIVOT, 12 ) ) );
  }

  @Override
  public void onStart()
  {
    super.onStart();
    // Connect the client.
    MapActivity.mLocationClient.connect();
    SharedPreferences prefs = getActivity().getSharedPreferences( "com.potato.burritohunter", Context.MODE_PRIVATE );
    double lat = Double.parseDouble( prefs.getString( PIVOT_LAT_KEY, "181" ) );
    double lng = Double.parseDouble( prefs.getString( PIVOT_LNG_KEY, "181" ) );
    // first, check to see if shared prefs values exist for pivot
    if ( lat == 181 || lng == 181 )
    { //if no, draw marker to hardcoded place, place camera there.
      updateAndDrawPivot( PIVOT );
      new AlertDialog.Builder( getActivity() )

      .setMessage( "Would you like to find your current location?" )
          .setPositiveButton( "Yes", new DialogInterface.OnClickListener()
            {
              public void onClick( DialogInterface dialog, int whichButton )
              {
                if ( MapActivity.mLocationClient.isConnected() )
                {
                  double lat = MapActivity.mLocationClient.getLastLocation().getLatitude();
                  double lng = MapActivity.mLocationClient.getLastLocation().getLongitude();
                  updateAndDrawPivot( new LatLng( lat, lng ) );
                }
                else
                {
                  //  new Thread(                 //MapActivity.mLocationClient.getLastLocation();)
                  Handler handler = new Handler();
                  handler.postDelayed( new Runnable()
                    {
                      public void run()
                      {
                        MyOtherMapFragment.shouldFindMe = false;
                      }
                    }, 5000 );

                  shouldFindMe = true;
                }
              }

            } ).setNegativeButton( "No", new DialogInterface.OnClickListener()
            {
              public void onClick( DialogInterface dialog, int whichButton )
              {
                /* User clicked cancel so do some stuff */
              }
            } ).create().show();
    }
    else
    {
      // else reload values, draw pivot and update camera
      updateAndDrawPivot( new LatLng( lat, lng ) );
    }
  }

  @Override
  public void onStop()
  {
    // Disconnecting the client invalidates it.
    savePivotToSharedPrefs();
    MapActivity.mLocationClient.disconnect();
    super.onStop();
  }

  private void initMap( GoogleMap map )
  {
    UiSettings settings = map.getUiSettings();
    settings.setAllGesturesEnabled( true );
    settings.setMyLocationButtonEnabled( true );
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

  public void savePivotToSharedPrefs()
  {
    SharedPreferences prefs = getActivity().getSharedPreferences( "com.potato.burritohunter", Context.MODE_PRIVATE );
    prefs.edit().clear();
    /* save stuff for next time! */
    if ( PIVOT != null )
    {
      String lat = PIVOT.latitude + "";
      String lng = PIVOT.longitude + "";
      prefs.edit().putString( PIVOT_LAT_KEY, lat ).commit();
      prefs.edit().putString( PIVOT_LNG_KEY, lng ).commit(); 
    }

    float zoom = map.getCameraPosition().zoom;
    float tilt = map.getCameraPosition().tilt;
    float bearing = map.getCameraPosition().bearing;
    LatLng target = map.getCameraPosition().target;
    double latitude = target.latitude;
    double longitude = target.longitude;
    prefs.edit().putFloat( CAMERA_ZOOM_KEY, zoom );
    prefs.edit().putFloat( CAMERA_TILT_KEY, tilt );
    prefs.edit().putFloat( CAMERA_BEARING_KEY, bearing );
    prefs.edit().putString( CAMERA_LAT_KEY, latitude + "" );
    prefs.edit().putString( CAMERA_LNG_KEY, longitude + "" );

    prefs.edit().commit();
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
