// TODO images, currentlocation and searching pivot markers

package com.potato.burritohunter.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.potato.burritohunter.R;
import com.potato.burritohunter.adapter.ViewPagerAdapter;
import com.potato.burritohunter.database.DatabaseHelper;
import com.potato.burritohunter.database.DatabaseUtil;
import com.potato.burritohunter.foursquare.explore.FoursquareExploreResult;
import com.potato.burritohunter.foursquare.explore.Group;
import com.potato.burritohunter.foursquare.explore.Item;
import com.potato.burritohunter.foursquare.explore.Response;
import com.potato.burritohunter.foursquare.search.Category;
import com.potato.burritohunter.foursquare.search.Icon;
import com.potato.burritohunter.foursquare.search.Location;
import com.potato.burritohunter.foursquare.search.Venue;
import com.potato.burritohunter.fragment.MyOtherMapFragment;
import com.potato.burritohunter.fragment.POIListFragment;
import com.potato.burritohunter.fragment.SampleListFragment;
import com.potato.burritohunter.fragment.SinglePOIListFragment;
import com.potato.burritohunter.stuff.ADS;
import com.potato.burritohunter.stuff.BurritoClickListeners.ViewPagerOnPageChangeListener;
import com.potato.burritohunter.stuff.GalleryPoiList;
import com.potato.burritohunter.stuff.SearchResult;
import com.potato.burritohunter.stuff.SetupThread;
import com.potato.burritohunter.stuff.SomeUtil;
import com.potato.burritohunter.stuff.Spot;
import com.squareup.otto.Subscribe;

// should be renamed to something else, like main screen, because it holds a viewpager now instead of a map
public class MapActivity extends BaseActivity implements GooglePlayServicesClient.ConnectionCallbacks,
    GooglePlayServicesClient.OnConnectionFailedListener
{
  private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
  public static final Map<Marker, SearchResult> currentSearchResults = new ConcurrentHashMap<Marker, SearchResult>();
  public static final Set<Marker> selectedSearchResults = Collections.synchronizedSet( new HashSet<Marker>() );
  private static final String TAG = MapActivity.class.getName();
  public static ViewPagerAdapter viewPagerAdapter;
  public static ViewPager viewPager;
  public static SampleListFragment.SlidingMenuAdapter slidingMenuAdapter;
  public static LocationClient mLocationClient;
  public static MapActivity instance;
  public static MenuItem viewInMap;
  public static SlidingMenu slidingMenu;
  public static POIListFragment _listFragment;

  public static MyOtherMapFragment _mapFragment;

  @Override
  public void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    getSupportActionBar().hide();
    setContentView( R.layout.activity_map );
    mLocationClient = new LocationClient( this, this, this );
    _mapFragment = new MyOtherMapFragment();
    instance = this;

    _listFragment = new POIListFragment();

    viewPager = (ViewPager) findViewById( R.id.pager );
    viewPagerAdapter = new ViewPagerAdapter( getSupportFragmentManager() );
    viewPagerAdapter.addFragment( _mapFragment );
    viewPagerAdapter.addFragment( _listFragment );

    viewPager.setAdapter( viewPagerAdapter );
    viewPager.setOnPageChangeListener( new ViewPagerOnPageChangeListener( getSlidingMenu() ) );
    viewPager.setCurrentItem( 0 );
    viewPager.setOffscreenPageLimit( 2 );

    getSlidingMenu().setTouchModeAbove( SlidingMenu.LEFT );
    getSlidingMenu().setOnOpenedListener( new SlidingMenu.OnOpenedListener()
      {

        @Override
        public void onOpened()
        {
          SomeUtil.hideSoftKeyboard( MapActivity.instance, MyOtherMapFragment.mySearchView );

        }

      } );

    slidingMenuAdapter = new SampleListFragment.SlidingMenuAdapter( _context );
    mFrag.setListAdapter( slidingMenuAdapter );
  }

  //Called when the Activity becomes visible.
  @Override
  protected void onStart()
  {
    super.onStart();

    SharedPreferences prefs = getSharedPreferences( "com.potato.burritohunter", Context.MODE_PRIVATE );
    if ( prefs.contains( ENTERED_GRACEFULLY ) )
    { // onstop wasn't called, clear shared prefs

      prefs.edit().clear().commit();
    }

    prefs.edit().putString( ENTERED_GRACEFULLY, "blah" ).commit();
  }

  // Called when the Activity is no longer visible.
  @Override
  protected void onStop()
  {
    super.onStop();
    // delete from sp
    SharedPreferences prefs = getSharedPreferences( "com.potato.burritohunter", Context.MODE_PRIVATE );
    prefs.edit().remove( ENTERED_GRACEFULLY ).commit();
  }

  public static final String ENTERED_GRACEFULLY = "entered gracefully";

  @Override
  public void onPause()
  {
    super.onPause();
    SomeUtil.getBus().unregister( this );
    if ( viewPager.getCurrentItem() == 1 )
    {
      GalleryPoiList.kontinue = false;
    }
  }

  @Override
  public void onResume()
  {
    super.onResume();
    SomeUtil.getBus().register( this );
    if ( viewPager.getCurrentItem() == 1 )
    {
      POIListFragment.listAdapter.startFlipping();
      GalleryPoiList.kontinue = true;
    }
  }

  //remove markers except ones that were selected.  to clear all markers, clear selected markers before calling this func
  public static void clearSearchResults()
  {
    // clear everything
    slidingMenuAdapter.clear();
    currentSearchResults.clear();
    selectedSearchResults.clear();
  }

  static Marker newPaneMarker;

  public static SearchResult convertVenueToSearchResult( Venue venue )
  {
    Location location = venue.getLocation();
    String id = venue.getId();
    String name = venue.getName();

    if ( location == null || id == null || name == null )
      return null;
    double lat = location.getLat();
    double lng = location.getLng();
    String address = location.getAddress();
    if ( lat == Double.MIN_VALUE || lng == Double.MIN_VALUE )
      return null;

    SearchResult mySearchResult = new SearchResult();
    mySearchResult.rating = venue.getRating();
    mySearchResult._lat = lat;
    mySearchResult._lng = lng;
    mySearchResult._name = name;
    mySearchResult.address = address;
    mySearchResult.id = id;
    mySearchResult._canonicalAddress = venue.getCanonicalUrl();
    mySearchResult.time = "" + System.currentTimeMillis();

    List<Category> categories = venue.getCategories();
    if ( categories != null && categories.size() > 0 )
    {
      Category category = categories.get( 0 );
      if ( category != null )
      {
        Icon icon = category.getIcon();
        if ( icon != null )
        {
          String prefix = icon.getPrefix();
          String bg = "bg_";
          String imageSize = "32";
          String suffix = icon.getSuffix();
          String iconUrl = prefix + bg + imageSize + suffix;
          mySearchResult.photoIcon = iconUrl;
        }
      }
    }

    if ( mySearchResult.photoIcon == null || "".equals( mySearchResult.photoIcon ) )
    {
      //TODO set default pic here!!
      //mySearchResult.photoIcon = 
    }
    return mySearchResult;
  }

  @Subscribe
  public void subscriberWithASillyName( FoursquareExploreResult searchResult )
  {
    newPaneMarker = null;
    final List<Venue> venues = new ArrayList<Venue>();
    Response response = searchResult.getResponse();

    List<Group> groups = response.getGroups();
    Group g = groups.get( 0 );
    List<Item> items = g.getItems();
    for ( Item item : items )
    {
      venues.add( item.getVenue() );
    }
    Toast.makeText( this, venues.size() + " results found", Toast.LENGTH_SHORT ).show();

    /* store all current ids on map */
    final ArrayList<String> ids = new ArrayList<String>();

    String paneMarkerId = null;

    if ( MyOtherMapFragment.paneMarker != null )
    {
      Marker marker = MyOtherMapFragment.paneMarker;
      paneMarkerId = currentSearchResults.get( marker ).id;
      MyOtherMapFragment.disablePane();
    }
    //get all ids
    for ( Marker m : selectedSearchResults )
    {
      SearchResult sr = currentSearchResults.get( m );
      String id = sr.id;
      ids.add( id );
    }
    //remove markers
    for ( Marker m : currentSearchResults.keySet() )
    {
      m.remove();
    }
    /* end new */
    clearSearchResults();
    /* new */

    final String fPaneMarkerId = paneMarkerId;
    // restore ids, repopulate currentsearchresults and slidermenuadapter
    final DatabaseHelper dbHelper = DatabaseUtil.getDatabaseHelper();

    ( new AsyncTask<Void, Void, Void>()
      {

        @Override
        protected Void doInBackground( Void... params )
        {
          // revive all selected markers that were existing before
          for ( final String id : ids )
          {

            Cursor c = dbHelper.retrieveSinglePoint( id );

            final SearchResult sr = dbHelper.getSearchResult( c );
            runOnUiThread( new Runnable()
              {

                @Override
                public void run()
                {

                  LatLng pos = new LatLng( sr._lat, sr._lng );
                  Marker marker = _mapFragment.getMap().addMarker( new MarkerOptions()
                                                                       .position( pos )
                                                                       .title( sr._name )
                                                                       .icon( BitmapDescriptorFactory.fromBitmap( Spot
                                                                                  .ratingToBitmap( sr.rating, true,
                                                                                                   false ) ) ) );
                  if ( sr.id.equals( fPaneMarkerId ) )
                  {
                    newPaneMarker = marker;
                  }
                  currentSearchResults.put( marker, sr );
                  selectedSearchResults.add( marker );
                  slidingMenuAdapter.add( marker );
                }
              } );

          }
          return null;
        }

        @Override
        protected void onPostExecute( Void result )
        {
          //inflate all new markers from the internets
          MyOtherMapFragment.paneMarker = newPaneMarker;

          /* end new */
          for ( Venue venue : venues )
          {
            SearchResult mySearchResult = convertVenueToSearchResult( venue );
            if ( mySearchResult == null )
              continue;
            //mySearchResult.photoUrl = 
            dbHelper.insertPoint( mySearchResult );
            if ( ids.contains( mySearchResult.id ) ) //already accounted for, // new todo: 
            {
              continue;
            }
            LatLng pos = new LatLng( mySearchResult._lat, mySearchResult._lng );

            // TODO make a big ass Marker class with its own onclicklistener
            Marker marker = _mapFragment.getMap().addMarker( new MarkerOptions()
                                                                 .position( pos )
                                                                 .title( mySearchResult._name )
                                                                 .snippet( "Kiel is cool" )
                                                                 .icon( BitmapDescriptorFactory.fromBitmap( Spot
                                                                            .ratingToBitmap( mySearchResult.rating,
                                                                                             false, false ) ) ) );

            currentSearchResults.put( marker, mySearchResult );

            slidingMenuAdapter.add( marker );
          }

          setPaneMarkerBitmap( true );
          if ( MyOtherMapFragment.paneMarker != null )
          {
            SearchResult sr = currentSearchResults.get( MyOtherMapFragment.paneMarker );
            MyOtherMapFragment.enablePane( sr );
          }
        }
      } ).execute();

  }

  public static void setPaneMarkerBitmap( boolean enabled )
  {
    if ( MyOtherMapFragment.paneMarker != null )
    {
      SearchResult sr = currentSearchResults.get( MyOtherMapFragment.paneMarker );
      if ( sr != null ) //it happens when clearing points from map that don't persist
      {
        boolean selected = selectedSearchResults.contains( MyOtherMapFragment.paneMarker );
        MyOtherMapFragment.paneMarker.setIcon( BitmapDescriptorFactory.fromBitmap( Spot.ratingToBitmap( sr.rating,
                                                                                                        selected,
                                                                                                        enabled ) ) );
      }
    }
  }

  public GoogleMap getMap()
  {
    return _mapFragment.getMap();
  }

  @Override
  public void onBackPressed()
  {
    if ( viewPager.getCurrentItem() == 0 )
    {
      // If the user is currently looking at the first step, allow the system to handle the
      // Back button. This calls finish() on this activity and pops the back stack.
      super.onBackPressed();
    }
    else
    {
      // Otherwise, select the previous step.
      viewPager.setCurrentItem( viewPager.getCurrentItem() - 1 );
    }
  }

  //delete this once you get viewin map working
  /*
   * @Override public boolean onCreateOptionsMenu( Menu menu ) { MenuInflater inflater = this.getSupportMenuInflater();
   * inflater.inflate( R.menu.main, menu ); return super.onCreateOptionsMenu( menu ); }
   * 
   * @Override public boolean onOptionsItemSelected( MenuItem item ) { switch ( item.getItemId() ) { case
   * R.id.viewinmap: viewInMapAction(); return true; } return super.onOptionsItemSelected( item ); }
   */

  //TODO save map positions in db so you can restore it back
  public void viewInMapAction()
  {
    /*
     * MapActivity.selectedSearchResults.clear(); for ( Marker m : MapActivity.currentSearchResults.keySet() ) {
     * m.remove(); } _mapFragment.getMap().clear(); MapActivity.currentSearchResults.clear();
     * MapActivity.slidingMenuAdapter.clear();
     */

    // clear current, selected, and sliding
    // retrieve points.
    final CameraPosition cameraPosition = MyOtherMapFragment.map.getCameraPosition();
    ( new AsyncTask<Void, Void, Void>()
      {
        @Override
        protected Void doInBackground( Void... params )
        {
          DatabaseHelper dbHelper = DatabaseUtil.getDatabaseHelper();
          List<SearchResult> searchResults = dbHelper.retrievePoints( SinglePOIListFragment.staticForeignKey + "" );
          MyOtherMapFragment.saveSearchResultsToSharedPrefs( ADS.getInstance().getSharedPreferences(), searchResults,
                                                             MyOtherMapFragment.SEARCH_RESULT_SERIALIZED_STRING_KEY );
          MyOtherMapFragment
              .saveSearchResultsToSharedPrefs( ADS.getInstance().getSharedPreferences(), searchResults,
                                               MyOtherMapFragment.SEARCH_RESULT_SELECTED_SERIALIZED_STRING_KEY );
          MyOtherMapFragment.paneMarker = null; //

          //saveCameraSettings will check to see if paneMarker is null and write to sp accordingly
          MyOtherMapFragment.saveCameraSettings( MapActivity.instance, cameraPosition );
          //MyOtherMapFragment.`
          return null;
        }

        @Override
        protected void onPostExecute( Void params )
        {
          MapActivity.selectedSearchResults.clear();
          for ( Marker m : MapActivity.currentSearchResults.keySet() )
          {
            m.remove();
          }
          _mapFragment.getMap().clear();
          MapActivity.currentSearchResults.clear();
          MapActivity.slidingMenuAdapter.clear();
          ( new SetupThread( MapActivity.instance ) ).execute();
        }

      } ).execute();

    getSlidingMenu().setTouchModeAbove( SlidingMenu.LEFT );
    viewPager.setCurrentItem( 0 );
  }

  @Override
  public void onDisconnected()
  { // locationservices dropped connection from error
    Toast.makeText( this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT ).show();
  }

  @Override
  public void onConnectionFailed( ConnectionResult connectionResult )
  { //locationservices
    if ( connectionResult.hasResolution() )
    {
      try
      {
        connectionResult.startResolutionForResult( this, CONNECTION_FAILURE_RESOLUTION_REQUEST );
      }
      catch ( IntentSender.SendIntentException e )
      {
        e.printStackTrace();
      }
    }
    else
    {
      showErrorDialog( connectionResult.getErrorCode() );
    }
  }

  // Define a DialogFragment that displays the error dialog
  public static class ErrorDialogFragment extends DialogFragment
  {
    private Dialog mDialog;

    public ErrorDialogFragment()
    {
      super();
      mDialog = null;
    }

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

  private boolean servicesConnected()
  {
    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable( this );
    if ( ConnectionResult.SUCCESS == resultCode )
    {
      Log.d( "Location Updates", "Google Play services is available." );
      return true;
    }
    else
    {
      showErrorDialog( resultCode );
    }
    return false;
  }

  public android.location.Location getCurrentLocation()
  {
    if ( mLocationClient != null )
    {
      if ( servicesConnected() )
      {
        boolean stuff = mLocationClient.isConnected();
        return mLocationClient.getLastLocation();
      }
      else
      {
        // retry?
      }
    }
    return null;
  }

  public void connectClient()
  {
    mLocationClient.connect();
  }

  public void disconnectClient()
  {
    mLocationClient.disconnect();
  }

  private void showErrorDialog( int errorCode )
  {
    Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog( errorCode, this, CONNECTION_FAILURE_RESOLUTION_REQUEST );
    if ( errorDialog != null ) // googl services can provide a dialog
    {
      ErrorDialogFragment errorFragment = new ErrorDialogFragment();
      errorFragment.setDialog( errorDialog );
      errorFragment.show( getSupportFragmentManager(), "Location Updates" );
    }
  }

  // there's a handler somewhere that will turn off the shouldFindMe flag after 5000 ms
  // this is to prevent the user from searching something, and oh hell all of a sudden camera changes
  @Override
  public void onConnected( Bundle dataBundle )
  {
    Toast.makeText( this, "Connected", Toast.LENGTH_SHORT ).show();
    if ( MyOtherMapFragment.shouldFindMe )
    {
      double lat = mLocationClient.getLastLocation().getLatitude();
      double lng = mLocationClient.getLastLocation().getLongitude();
      LatLng lastKnownLatLng = new LatLng( lat, lng );
      MyOtherMapFragment.updateAndDrawPivot( lastKnownLatLng );
      MyOtherMapFragment.moveCameraToLatLng( lastKnownLatLng );
    }
  }

}
