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
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
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
import com.potato.burritohunter.stuff.BottomPagerPanel;
import com.potato.burritohunter.stuff.BurritoClickListeners.MapOnMarkerClickListener;
import com.potato.burritohunter.stuff.FoursquareRequestAsyncTask;
import com.potato.burritohunter.stuff.SearchResult;
import com.potato.burritohunter.stuff.SomeUtil;

// this class should contain the map logic now...
public class MyOtherMapFragment extends SherlockFragment
{
  //public static LatLng PIVOT = new LatLng( 37.798052, -122.406278 );
  public static LatLng PIVOT = new LatLng( 0, 0 );
  private SupportMapFragment mMapFragment;
  public static GoogleMap map;

  public static Marker paneMarker;
  public static Marker pivotMarker;

  public static EditText mySearchView;

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

  public static final String SEARCH_RESULT_SERIALIZED_STRING_KEY = "SEARCH RESULT SERIALIZED STRING KEY"; // search results 
  public static final String SEARCH_RESULT_SELECTED_SERIALIZED_STRING_KEY = "SEARCH_RESULT_SELECTED_SERIALIZED_STRING_KEY"; // selected search result
  private static final String SEARCH_QUERY_KEY = "SEARCH QUERY KEY";

  public static boolean shouldFindMe = false;
  private static boolean ONSTOPLOCK = true; //should we skip on stop?

  private static View vw;
  public static View loadingView;

  // http://stackoverflow.com/questions/17476089/android-google-maps-fragment-and-viewpager-error-inflating-class-fragment
  @Override
  public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
  {
    if ( vw != null )
    {
      ViewGroup parent = (ViewGroup) vw.getParent();
      if ( parent != null )
        parent.removeView( vw );
    }
    try
    {
      vw = inflater.inflate( R.layout.my_other_map_fragment, container, false );
    }
    catch ( InflateException e )
    {
      /* map is already there, just return view as it is */
    }

    mMapFragment = ( (SupportMapFragment) getFragmentManager().findFragmentById( R.id.map_frag ) );
    map = mMapFragment.getMap();

    mySearchView = (EditText) vw.findViewById( R.id.mySearchView );
    loadingView = (ImageView) vw.findViewById( R.id.loading );

    mySearchView.setOnEditorActionListener( new OnEditorActionListener()
      {
        @Override
        public boolean onEditorAction( TextView v, int actionId, KeyEvent event )
        {
          if ( actionId == EditorInfo.IME_ACTION_SEARCH
               || ( ( event != null ) && ( event.getKeyCode() == event.KEYCODE_ENTER ) ) )
          {
            double lat = MyOtherMapFragment.PIVOT.latitude;
            double lng = MyOtherMapFragment.PIVOT.longitude;
            SomeUtil.startLoadingRotate( MyOtherMapFragment.loadingView );
            new FoursquareRequestAsyncTask( lat, lng, v.getText().toString(), SomeUtil.getBus(), MapActivity.instance )
                .execute(); // need to get this info
            return false;
          }
          return false;
        }
      } );

    ImageView cancelView = (ImageView) vw.findViewById( R.id.search_cancel );
    cancelView.setOnClickListener( new OnClickListener()
      {
        @Override
        public void onClick( View v )
        {
          mySearchView.setText( "" );
        }
      } );
    //vw.findViewById( R.id.find_me )
    //    .setOnClickListener( new BurritoClickListeners.FindMeOnClickListener( MapActivity.instance, this ) );
    //may have to rebuild dynamically instead of using xml
    BottomPagerPanel.makeInstance( vw, (SherlockFragmentActivity) getActivity() );
    initMap( map );
    //chyauchyau: set pane here
    return vw;
  }

  @Override
  public void onCreate( Bundle b )
  {
    super.onCreate( b );

    //map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    /*
     * map.moveCamera( CameraUpdateFactory.newLatLngZoom( PIVOT, 16 ) ); pivotMarker = map.addMarker( new
     * MarkerOptions().position( PIVOT ).dragable( true ) .icon( BitmapDescriptorFactory.fromResource(
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
        .icon( BitmapDescriptorFactory.fromResource( R.drawable.center_mark ) ) );

    pivotMarker.setPosition( PIVOT ); // is this ncessary? I don't think so
  }

  private static final int ZOOM_THRESHOLD = 7;

  public void moveCameraToLatLng( LatLng latlng )
  {
    CameraPosition currentCP = map.getCameraPosition();
    CameraPosition.Builder updateCP = new CameraPosition.Builder().target( latlng );
    boolean shouldZoom = currentCP.zoom < ZOOM_THRESHOLD;
    float currentZoom = currentCP.zoom;
    float suggestedZoom = 12;
    updateCP = updateCP.zoom( shouldZoom ? suggestedZoom : currentZoom );
    CameraUpdate cp = CameraUpdateFactory.newCameraPosition( updateCP.build() );
    //map.animateCamera( cp );
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

    // inflate search results
    String searchResultSerializedString = prefs.getString( SEARCH_RESULT_SERIALIZED_STRING_KEY, "" );
    HashMap<String, Marker> reverseSearchResultHashMap = new HashMap<String, Marker>();
    MapActivity.clearSearchResults();
    for ( int i = 0; i < searchResultSerializedString.length(); i += 24 )
    {
      String id = searchResultSerializedString.substring( i, i + 24 );
      DatabaseHelper dbHelper = DatabaseUtil.getDatabaseHelper();
      Cursor c = dbHelper.retrieveSinglePoint( id );
      SearchResult sr = dbHelper.getSearchResult( c );

      // TODO make a big ass Marker class with its own onclicklistener
      Marker marker = getMap()
          .addMarker( new MarkerOptions().position( new LatLng( sr._lat, sr._lng ) )
                          .icon( BitmapDescriptorFactory.fromResource( R.drawable.item_unselected ) ) );

      MapActivity.currentSearchResults.put( marker, sr );
      reverseSearchResultHashMap.put( id, marker );

      MapActivity.slidingMenuAdapter.add( marker );
    }

    //inflate selected search results
    String searchResultSelectedSerializedString = prefs.getString( SEARCH_RESULT_SELECTED_SERIALIZED_STRING_KEY, "" );
    for ( int i = 0; i < searchResultSelectedSerializedString.length(); i += 24 )
    {
      String id = searchResultSelectedSerializedString.substring( i, i + 24 );
      Marker marker = reverseSearchResultHashMap.get( id );
      MapActivity.selectedSearchResults.add( marker );
      //change marker state 
      marker.setIcon( BitmapDescriptorFactory.fromResource( R.drawable.item_selected ) );
      SearchResult sr = MapActivity.currentSearchResults.get( marker );
      Log.d( "com.potato.burritohunter", "Here: " + sr._name + ",      id: " + id );
    }
    //TODO set save panemarker in onstop and set to last chosen panemarker here
    // restore the search result that was typed
    double lat = Double.parseDouble( prefs.getString( PIVOT_LAT_KEY, "181" ) );
    double lng = Double.parseDouble( prefs.getString( PIVOT_LNG_KEY, "181" ) );
    // first, check to see if shared prefs values exist for pivot
    if ( lat == 181 || lng == 181 )
    { //if no, draw marker to hardcoded place, place camera there.
      updateAndDrawPivot( PIVOT );

      CameraUpdate cp = CameraUpdateFactory.newCameraPosition( new CameraPosition.Builder().target( PIVOT ).build() );
      map.animateCamera( cp );

      final Context thisContext = getActivity();
      new AlertDialog.Builder( getActivity() )

      .setMessage( "Would you like to find your current location?" )
          .setPositiveButton( "Yes", new DialogInterface.OnClickListener()
            {
              public void onClick( DialogInterface dialog, int whichButton )
              {
                if ( MapActivity.mLocationClient.isConnected() )
                {
                  Location loc = MapActivity.mLocationClient.getLastLocation();
                  if ( loc == null )
                  {
                    Toast.makeText( thisContext, "an error occurred, apologies", Toast.LENGTH_SHORT ).show();
                  }
                  else
                  {
                    double lat = MapActivity.mLocationClient.getLastLocation().getLatitude();
                    double lng = MapActivity.mLocationClient.getLastLocation().getLongitude();
                    LatLng lastKnownLatLng = new LatLng( lat, lng );
                    updateAndDrawPivot( lastKnownLatLng );
                    moveCameraToLatLng( lastKnownLatLng );
                  }
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
      // else reload values, draw pivot
      updateAndDrawPivot( new LatLng( lat, lng ) );
      //retrieve paneMarker if one exists()
      String paneMarkerId = prefs.getString( PANEMARKER_ID_KEY, PANEMARKER_ID_CLEAR_VALUE );

      // check if paneMarker should be inflated
      if ( PANEMARKER_ID_CLEAR_VALUE.equals( paneMarkerId ) )
      {//no
        paneMarker = null;
      }
      else
      {//yes
        paneMarker = reverseSearchResultHashMap.get( paneMarkerId );
      }

      // update camera to the last known configuration
      float zoom = prefs.getFloat( CAMERA_ZOOM_KEY, Float.MAX_VALUE );
      float tilt = prefs.getFloat( CAMERA_TILT_KEY, Float.MAX_VALUE );
      float bearing = prefs.getFloat( CAMERA_BEARING_KEY, Float.MAX_VALUE );
      String latStr = prefs.getString( CAMERA_LAT_KEY, CAMERA_DEFAULT_VAL );
      String lngStr = prefs.getString( CAMERA_LNG_KEY, CAMERA_DEFAULT_VAL );
      CameraPosition currentCP = map.getCameraPosition();

      LatLng latlng = PIVOT;
      if ( CAMERA_DEFAULT_VAL.compareTo( latStr ) != 0 && CAMERA_DEFAULT_VAL.compareTo( lngStr ) != 0 )
      {
        try
        {
          double latDbl = Double.parseDouble( latStr );
          double lngDbl = Double.parseDouble( lngStr );
          latlng = new LatLng( latDbl, lngDbl );
        }
        catch ( Exception e )
        {
          //somewhere, Joshua Bloch is crying.
        }

      }
      CameraPosition.Builder updateCP = new CameraPosition.Builder().target( latlng );
      if ( zoom != Float.MAX_VALUE )
      {
        updateCP = updateCP.zoom( zoom ); // (zoomzoom)
      }
      if ( tilt != Float.MAX_VALUE )
      {
        updateCP = updateCP.tilt( tilt );
      }
      if ( bearing != Float.MAX_VALUE )
      {
        updateCP = updateCP.bearing( bearing );
      }

      CameraUpdate cp = CameraUpdateFactory.newCameraPosition( updateCP.build() );
      map.animateCamera( cp );
    }

    String searchQuery = prefs.getString( SEARCH_QUERY_KEY, "" );
    ONSTOPLOCK = false;
  }

  private static final String CAMERA_DEFAULT_VAL = Float.MAX_VALUE + "";

  // save pivot, current search results, selected search results, query, and disconnect location client
  @Override
  public void onStop()
  {
    // we have a problem with on stop being called twice... so this will be our ghetto solution.
    if ( ONSTOPLOCK ) //should we skip onstop?
    {

      super.onStop();
      return;
    }
    ONSTOPLOCK = true;
    //save pivot
    saveCameraSettings();

    // save current search results
    saveSearchResultsToSharedPrefs( getActivity(), MapActivity.currentSearchResults.values(),
                                    SEARCH_RESULT_SERIALIZED_STRING_KEY );

    //save selected points
    ArrayList<SearchResult> selectedSearchResults = new ArrayList<SearchResult>();
    for ( Marker m : MapActivity.selectedSearchResults )
    {
      selectedSearchResults.add( MapActivity.currentSearchResults.get( m ) );
    }
    saveSearchResultsToSharedPrefs( getActivity(), selectedSearchResults, SEARCH_RESULT_SELECTED_SERIALIZED_STRING_KEY );

    //save query
    saveSearchQueryToSharedPrefs();

    //clear maps and disconnect location client
    MapActivity.selectedSearchResults.clear();
    for ( Marker m : MapActivity.currentSearchResults.keySet() )
    {
      m.remove();
    }
    map.clear();
    MapActivity.currentSearchResults.clear();
    MapActivity.slidingMenuAdapter.clear();
    MapActivity.mLocationClient.disconnect();
    super.onStop();
  }

  @Override
  public void onResume()
  {
    super.onResume();
    if ( paneMarker == null )
      BottomPagerPanel.getInstance().disableMarkerPanel();
  }

  //TODO chyauchyau save searchQuery
  private void saveSearchQueryToSharedPrefs()
  {
    SharedPreferences prefs = getActivity().getSharedPreferences( "com.potato.burritohunter", Context.MODE_PRIVATE );
    prefs.edit().clear();
    //chyauchyauCharSequence query = MapActivity.searchView.getQuery();

    //    String value = query == null ? "" : query.toString();

    //  prefs.edit().putString( SEARCH_QUERY_KEY, value ).commit();
  }

  private void initMap( GoogleMap map )
  {
    UiSettings settings = map.getUiSettings();
    settings.setAllGesturesEnabled( true );
    settings.setMyLocationButtonEnabled( true );
    settings.setMyLocationButtonEnabled( true );
    map.setOnMapLongClickListener( new OnMapLongClickListener()
      {
        @Override
        public void onMapLongClick( LatLng point )
        {
          updateAndDrawPivot( point );
          moveCameraToLatLng( point );
        }
      } );
    map.setOnMapClickListener( new OnMapClickListener()
      {
        @Override
        public void onMapClick( LatLng point )
        {
          //chyauchyau -- show button screen
          //chyauchyau -- marker should not be highlighted
          InputMethodManager imm = (InputMethodManager) getActivity().getSystemService( Context.INPUT_METHOD_SERVICE );
          imm.hideSoftInputFromWindow( mySearchView.getWindowToken(), 0 );
          paneMarker = null;
          BottomPagerPanel.getInstance().disableMarkerPanel();
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
      BottomPagerPanel.getInstance().handleOnDestroy();
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

  private static final String PANEMARKER_ID_KEY = "panemarker_id_key";
  private static final String PANEMARKER_ID_CLEAR_VALUE = "panemarker_id_clear_value";

  public void saveCameraSettings()
  {
    SharedPreferences prefs = getActivity().getSharedPreferences( "com.potato.burritohunter", Context.MODE_PRIVATE );
    prefs.edit().clear();

    // save panemarker
    // save pivot
    // save camera position
    if ( paneMarker != null && MapActivity.currentSearchResults.get( paneMarker ) != null )
    {
      SearchResult sr = MapActivity.currentSearchResults.get( paneMarker );
      prefs.edit().putString( PANEMARKER_ID_KEY, sr.id ).commit();
      prefs.edit().clear();
    }
    else
    {
      // clear it 
      prefs.edit().putString( PANEMARKER_ID_KEY, PANEMARKER_ID_CLEAR_VALUE ).commit();
      prefs.edit().clear();
    }

    /* save stuff for next time! */
    if ( PIVOT != null )
    {
      String lat = PIVOT.latitude + "";
      String lng = PIVOT.longitude + "";
      prefs.edit().putString( PIVOT_LAT_KEY, lat ).commit();
      prefs.edit().clear();
      prefs.edit().putString( PIVOT_LNG_KEY, lng ).commit();
      prefs.edit().clear();
    }
    LatLng target = map.getCameraPosition().target;
    double latitude = target.latitude;
    double longitude = target.longitude;
    if ( latitude == 0 && longitude == 0 )
    {

    }
    else
    {

      float zoom = map.getCameraPosition().zoom;
      float tilt = map.getCameraPosition().tilt;
      float bearing = map.getCameraPosition().bearing;
      
      prefs.edit().putFloat( CAMERA_ZOOM_KEY, zoom ).commit();
      prefs.edit().clear();
      prefs.edit().putFloat( CAMERA_TILT_KEY, tilt ).commit();
      prefs.edit().clear();
      prefs.edit().putFloat( CAMERA_BEARING_KEY, bearing ).commit();
      prefs.edit().clear();
      prefs.edit().putString( CAMERA_LAT_KEY, latitude + "" ).commit();
      prefs.edit().clear();
      prefs.edit().putString( CAMERA_LNG_KEY, longitude + "" ).commit();
      prefs.edit().clear();
    }

    //    prefs.edit().commit();
  }

  public static void saveSearchResultsToSharedPrefs( Activity activity, Collection<SearchResult> searchResults,
                                                     String key )
  {
    SharedPreferences prefs = activity.getSharedPreferences( "com.potato.burritohunter", Context.MODE_PRIVATE );
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

  public static void changeMarkerState( Marker marker )
  {
    if ( !marker.equals( MyOtherMapFragment.pivotMarker ) ) // why doesn't == work?
    {
      boolean selected = MapActivity.selectedSearchResults.contains( marker );
      if ( marker.equals( paneMarker ) )
      {
        if ( selected )
        {
          marker.setIcon( BitmapDescriptorFactory.fromResource( R.drawable.item_unselected ) );
          MapActivity.selectedSearchResults.remove( marker );
        }
        else
        {
          marker.setIcon( BitmapDescriptorFactory.fromResource( R.drawable.item_selected ) );
          MapActivity.selectedSearchResults.add( marker );
        }
        BottomPagerPanel.getInstance().setBottomPagerButtonsNumsSelectedTextView( MapActivity.selectedSearchResults
                                                                                      .size() + "" );
        //checkBox.setChecked( !selected );    //chyauchyau
      }
      else
      {
        //checkBox.setChecked( selected );    //chyauchyau
      }
    }
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
