package com.potato.burritohunter.fragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.potato.burritohunter.R;
import com.potato.burritohunter.activity.MapActivity;
import com.potato.burritohunter.database.DatabaseHelper;
import com.potato.burritohunter.database.DatabaseUtil;
import com.potato.burritohunter.stuff.BurritoClickListeners;
import com.potato.burritohunter.stuff.BurritoClickListeners.MapOnMarkerClickListener;
import com.potato.burritohunter.stuff.SearchResult;

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

  private static final String SEARCH_RESULT_SERIALIZED_STRING_KEY = "SEARCH RESULT SERIALIZED STRING KEY"; // search results 
  private static final String SEARCH_RESULT_SELECTED_SERIALIZED_STRING_KEY = "SEARCH_RESULT_SELECTED_SERIALIZED_STRING_KEY"; // selected search result
  private static final String SEARCH_QUERY_KEY = "SEARCH QUERY KEY";

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

  // inflate pivot, current search results, selected search results, query
  // connect location client
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

    // inflate search results
    String searchResultSerializedString = prefs.getString( SEARCH_RESULT_SERIALIZED_STRING_KEY, "" );
    ArrayList<String> serializedStringArrayList = new ArrayList<String>();
    HashMap<String, Marker> reverseSearchResultHashMap = new HashMap<String, Marker>();
    for ( int i = 0; i < searchResultSerializedString.length(); i += 24 )
    {
      String id = searchResultSerializedString.substring( i, i + 24 );
      DatabaseHelper dbHelper = DatabaseUtil.getDatabaseHelper();
      Cursor c = dbHelper.retrieveSinglePoint( id );
      SearchResult sr = dbHelper.getSearchResult( c );

      // TODO make a big ass Marker class with its own onclicklistener
      Marker marker = getMap().addMarker( new MarkerOptions().position( new LatLng( sr._lat, sr._lng ) )
                                              .title( sr._name ).snippet( "Kiel is cool" )
                                              .icon( BitmapDescriptorFactory.fromResource( R.drawable.ic_launcher ) ) );

      MapActivity.currentSearchResults.put( marker, sr );
      reverseSearchResultHashMap.put( id, marker );

    }

    //inflate selected search results
    String searchResultSelectedSerializedString = prefs.getString( SEARCH_RESULT_SELECTED_SERIALIZED_STRING_KEY, "" );
    for ( int i = 0; i < searchResultSelectedSerializedString.length(); i += 24 )
    {
      String id = searchResultSerializedString.substring( i, i + 24 );
      Marker marker = reverseSearchResultHashMap.get( id );
      MapActivity.selectedSearchResults.add( marker );
    }
    ArrayList<String> selectedSerializedStringArrayList = new ArrayList<String>();
    String searchQuery = prefs.getString( SEARCH_QUERY_KEY, "" );

    //TODO inflate the stored prefs here

    // restore the search result that was typed
  }

  // save pivot, current search results, selected search results, query, and disconnect location client
  @Override
  public void onStop()
  {
    //save pivot
    savePivotToSharedPrefs();

    // save current search results
    saveSearchResultsToSharedPrefs( MapActivity.currentSearchResults.values(), SEARCH_RESULT_SERIALIZED_STRING_KEY );

    //save selected points
    ArrayList<SearchResult> selectedSearchResults = new ArrayList<SearchResult>();
    for ( Marker m : MapActivity.selectedSearchResults )
    {
      selectedSearchResults.add( MapActivity.currentSearchResults.get( m ) );
    }
    saveSearchResultsToSharedPrefs( selectedSearchResults, SEARCH_RESULT_SELECTED_SERIALIZED_STRING_KEY );

    //save query
    saveSearchQueryToSharedPrefs();

    //clear maps and disconnect location client
    MapActivity.currentSearchResults.clear();
    MapActivity.selectedSearchResults.clear();
    MapActivity.mLocationClient.disconnect();
    super.onStop();
  }

  private void saveSearchQueryToSharedPrefs()
  {
    SharedPreferences prefs = getActivity().getSharedPreferences( "com.potato.burritohunter", Context.MODE_PRIVATE );
    prefs.edit().clear();

    String value = MapActivity.searchView.getQuery().toString();

    prefs.edit().putString( SEARCH_QUERY_KEY, value ).commit();
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

  public void saveSearchResultsToSharedPrefs( Collection<SearchResult> searchResults, String key )
  {
    SharedPreferences prefs = getActivity().getSharedPreferences( "com.potato.burritohunter", Context.MODE_PRIVATE );
    prefs.edit().clear();

    // bundle up all searchresult ids
    StringBuffer sb = new StringBuffer();
    for ( SearchResult sr : searchResults ) // MapActivity.currentSearchResults.values() )
    {
      String id = sr.id;
      sb.append( id );
    }
    String serializedString = sb.toString();
    prefs.edit().putString( key, serializedString ).commit(); //SEARCH_RESULT_SERIALIZED_STRING_KEY
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
