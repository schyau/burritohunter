package com.potato.burritohunter.fragment;

import java.util.ArrayList;
import java.util.Collection;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.potato.burritohunter.R;
import com.potato.burritohunter.activity.MapActivity;
import com.potato.burritohunter.stuff.ADS;
import com.potato.burritohunter.stuff.BurritoClickListeners.MapOnMarkerClickListener;
import com.potato.burritohunter.stuff.BurritoClickListeners;
import com.potato.burritohunter.stuff.FoursquareRequestAsyncTask;
import com.potato.burritohunter.stuff.SearchResult;
import com.potato.burritohunter.stuff.SetupThread;
import com.potato.burritohunter.stuff.SomeUtil;
import com.potato.burritohunter.stuff.Spot;

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
  public final static String PIVOT_LAT_KEY = "PIVOT LAT KEY";
  public final static String PIVOT_LNG_KEY = "PIVOT LNG KEY";

  /* save camerato shared prefs keys */
  public final static String CAMERA_ZOOM_KEY = "CAMERA ZOOM KEY";
  public final static String CAMERA_TILT_KEY = "CAMERA TILT KEY";
  public final static String CAMERA_BEARING_KEY = "CAMERA BEARING KEY";
  public final static String CAMERA_LAT_KEY = "CAMERA LAT KEY";
  public final static String CAMERA_LNG_KEY = "CAMERA LNG KEY";

  public static final String SEARCH_RESULT_SERIALIZED_STRING_KEY = "SEARCH RESULT SERIALIZED STRING KEY"; // search results 
  public static final String SEARCH_RESULT_SELECTED_SERIALIZED_STRING_KEY = "SEARCH_RESULT_SELECTED_SERIALIZED_STRING_KEY"; // selected search result
  public static final String SEARCH_QUERY_KEY = "SEARCH QUERY KEY";

  public static boolean shouldFindMe = false;

  private static View vw;
  public static View loadingView;

  public static TextView title;
  public static TextView desc;
  public static TextView numSelectedTextView;
  public static ImageView imageIcon;

  public static ImageView ratingNumselected;

  private static String id;

  // http://stackoverflow.com/questions/17476089/android-google-maps-fragment-and-viewpager-error-inflating-class-fragment
  @Override
  public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
  {
    Log.d( "asdf", "oncreateview" );
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
    catch ( Exception e )
    {
      e.printStackTrace();
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
          BurritoClickListeners.clearUnsaved();
        }
      } );
    cancelView.setOnLongClickListener( new OnLongClickListener() {

      @Override
      public boolean onLongClick( View v )
      {
        BurritoClickListeners.clearAll();
        return false;
      }
      
    });
    title = (TextView) vw.findViewById( R.id.bottomPagerMarkerTitle );
    desc = (TextView) vw.findViewById( R.id.bottomPagerMarkerDesc );
    numSelectedTextView = (TextView) vw.findViewById( R.id.num_selected );
    imageIcon = (ImageView) vw.findViewById( R.id.bottom_pager_marker_picture );
    ratingNumselected = (ImageView) vw.findViewById( R.id.rating_numselected );
    imageIcon.setOnClickListener( new BurritoClickListeners.OnBottomMarkerPanelPictureClickListener() );
    View linearLayout = vw.findViewById( R.id.bottomPagerMarkerLL );
    linearLayout.setOnClickListener( new OnClickListener()
      {

        @Override
        public void onClick( View v )
        {
          if ( id != null )
          {
            SomeUtil.launchFourSquareDetail( MapActivity.instance, id );
          }
        }

      } );

    title.setText( "luls this is the title" );
    desc.setText( "luls this is the desc" );
    //BottomPagerPanel.makeInstance( vw, (SherlockFragmentActivity) getActivity() );

    //rm bottom: make instance here
    initMap( map );
    //chyauchyau: set pane here
    return vw;
  }

  @Override
  public void onCreate( Bundle b )
  {
    Log.d( "asdf", "oncreate" );
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

  public static void updateAndDrawPivot( LatLng latLng )
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

  public static void moveCameraToLatLng( LatLng latlng )
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
    Log.d( "asdf", "onstart" );
    //clear maps and disconnect location client
    MapActivity.selectedSearchResults.clear();
    for ( Marker m : MapActivity.currentSearchResults.keySet() )
    {
      m.remove();
    }
    map.clear();
    // Connect the client.
    MapActivity.mLocationClient.connect();
    MapActivity.clearSearchResults();
    new SetupThread( getActivity() ).execute();
  }

  public static final String CAMERA_DEFAULT_VAL = Float.MAX_VALUE + "";

  // save pivot, current search results, selected search results, query, and disconnect location client
  @Override
  public void onStop()
  {
    Log.d( "asdf", "onstop" );
    //save pivot

    CameraPosition cameraPosition = map.getCameraPosition();
    saveCameraSettings( getActivity(), cameraPosition );

    // save current search results
    saveSearchResultsToSharedPrefs( ADS.getInstance().getSharedPreferences(),
                                    MapActivity.currentSearchResults.values(), SEARCH_RESULT_SERIALIZED_STRING_KEY );

    //save selected points
    ArrayList<SearchResult> selectedSearchResults = new ArrayList<SearchResult>();
    for ( Marker m : MapActivity.selectedSearchResults )
    {
      selectedSearchResults.add( MapActivity.currentSearchResults.get( m ) );
    }
    saveSearchResultsToSharedPrefs( ADS.getInstance().getSharedPreferences(), selectedSearchResults,
                                    SEARCH_RESULT_SELECTED_SERIALIZED_STRING_KEY );

    //save query
    saveSearchQueryToSharedPrefs();
    MapActivity.mLocationClient.disconnect();
    super.onStop();
  }

  @Override
  public void onResume()
  {
    super.onResume();
    Log.d( "asdf", "onResume" );
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
          //BottomPagerPanel.getInstance().disableMarkerPanel();
          disablePane();
          //rm bottom: disable when clicked
        }

      } );
    map.setOnMarkerClickListener( new MapOnMarkerClickListener() );
  }

  public static void disablePane()
  {
    id = null;
    title.setText( "title disabled" );
    desc.setText( "desc Disabled" );
    ratingNumselected.setImageBitmap( Spot.getGrayCircle() );
    imageIcon.setImageResource( R.drawable.rufknkddngme );
  }

  public static void enablePane( SearchResult sr )
  {
    id = sr.id;
    title.setText( sr._name );
    desc.setText( sr.address ); //change round item according to rating
    ratingNumselected.setImageBitmap( Spot.ratingToHollowBitmap( sr.rating ) );
    ImageLoader.getInstance().displayImage( sr.photoIcon, imageIcon, SomeUtil.getImageOptions() );
  }

  public void onDestroyView()
  {
    Log.d( "asdf", "ondestroyview" );
    super.onDestroyView();
    try
    {
      Fragment fragment = ( getFragmentManager().findFragmentById( R.id.map_frag ) );
      FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
      ft.remove( fragment );
      ft.commit();
      //BottomPagerPanel.getInstance().handleOnDestroy();
      //
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

  public static final String PANEMARKER_ID_KEY = "panemarker_id_key";
  public static final String PANEMARKER_ID_CLEAR_VALUE = "panemarker_id_clear_value";

  public static void saveCameraSettings( Activity activity, CameraPosition cameraPosition )
  {
    SharedPreferences prefs = activity.getSharedPreferences( "com.potato.burritohunter", Context.MODE_PRIVATE );
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
    double latitude = cameraPosition.target.latitude;
    double longitude = cameraPosition.target.longitude;
    if ( latitude == 0 && longitude == 0 )
    {

    }
    else
    {

      float zoom = cameraPosition.zoom;
      float tilt = cameraPosition.tilt;
      float bearing = cameraPosition.bearing;

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

  public static void saveSearchResultsToSharedPrefs( SharedPreferences prefs, Collection<SearchResult> searchResults,
                                                     String key )
  {
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

    if ( key.compareTo( SEARCH_RESULT_SELECTED_SERIALIZED_STRING_KEY ) == 0 )
      Log.d( "asdf", "just wrote " + serializedString );
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
          //marker.setIcon( BitmapDescriptorFactory.fromResource( R.drawable.item_unselected ) );
          MapActivity.selectedSearchResults.remove( marker );
        }
        else
        {
          //marker.setIcon( BitmapDescriptorFactory.fromResource( R.drawable.item_selected ) );
          MapActivity.selectedSearchResults.add( marker );
        }
        SearchResult sr = MapActivity.currentSearchResults.get( marker );
        Bitmap bmp = Spot.ratingToBitmap( sr.rating, !selected );
        marker.setIcon( BitmapDescriptorFactory.fromBitmap( bmp ) );
        //rm bottom: change textview with number
        //BottomPagerPanel.getInstance().setBottomPagerButtonsNumsSelectedTextView( MapActivity.selectedSearchResults
        //                                                                            .size() + "" );
        setBottomNumSelectedTextView( MapActivity.selectedSearchResults.size() + "" );

        //checkBox.setChecked( !selected );    //chyauchyau
      }
      else
      {
        //checkBox.setChecked( selected );    //chyauchyau
      }
    }
  }

  public static void setNumSelectedRatingToGray()
  {
    ratingNumselected.setImageBitmap( Spot.getGrayCircle() );
  }

  public static void setBottomNumSelectedTextView( String num )
  {
    numSelectedTextView.setText( num );
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
