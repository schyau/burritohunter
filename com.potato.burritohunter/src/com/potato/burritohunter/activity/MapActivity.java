// TODO images, currentlocation and searching pivot markers

package com.potato.burritohunter.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.potato.burritohunter.R;
import com.potato.burritohunter.adapter.ViewPagerAdapter;
import com.potato.burritohunter.database.DatabaseHelper;
import com.potato.burritohunter.database.DatabaseUtil;
import com.potato.burritohunter.database.SavedListItem;
import com.potato.burritohunter.foursquare.FoursquareSearchResult;
import com.potato.burritohunter.foursquare.Location;
import com.potato.burritohunter.foursquare.Response;
import com.potato.burritohunter.foursquare.Venue;
import com.potato.burritohunter.fragment.MyOtherMapFragment;
import com.potato.burritohunter.fragment.POIListFragment;
import com.potato.burritohunter.fragment.SampleListFragment;
import com.potato.burritohunter.stuff.BurritoClickListeners;
import com.potato.burritohunter.stuff.BurritoClickListeners.SearchViewOnQueryTextListener;
import com.potato.burritohunter.stuff.BurritoClickListeners.ViewPagerOnPageChangeListener;
import com.potato.burritohunter.stuff.SearchResult;
import com.potato.burritohunter.stuff.SomeUtil;
import com.squareup.otto.Subscribe;

// should be renamed to something else, like main screen, because it holds a viewpager now instead of a map
public class MapActivity extends BaseActivity implements GooglePlayServicesClient.ConnectionCallbacks,
    GooglePlayServicesClient.OnConnectionFailedListener
{
  private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

  // request the current location or start periodic updates

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

  public static final HashMap<Marker, SearchResult> currentSearchResults = new HashMap<Marker, SearchResult>();
  public static final ArrayList<Marker> selectedSearchResults = new ArrayList<Marker>();
  private static final String TAG = MapActivity.class.getName();
  public static ViewPagerAdapter viewPagerAdapter;
  public static ViewPager viewPager;
  public SampleListFragment.SlidingMenuAdapter slidingMenuAdapter;
  public static LocationClient mLocationClient;
  public static MapActivity instance;

  MyOtherMapFragment _mapFragment;

  @Override
  public void onConnected( Bundle dataBundle )
  {
    Toast.makeText( this, "Connected", Toast.LENGTH_SHORT ).show();
  }

  @Override
  public void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_map );
    mLocationClient = new LocationClient( this, this, this );
    _mapFragment = new MyOtherMapFragment();
    instance = this;
    //_mapFragment = MyMapFragment.newInstance( PIVOT );
    /*
     * _mapFragment = SupportMapFragment.newInstance(); initMap(_mapFragment.getMap());
     */

    POIListFragment listFragment = new POIListFragment();
    List<SavedListItem> list = DatabaseUtil.getSavedList();
    listFragment.setPoiList( list );

    viewPager = (ViewPager) findViewById( R.id.pager );
    viewPagerAdapter = new ViewPagerAdapter( getSupportFragmentManager() );
    viewPagerAdapter.addFragment( _mapFragment );
    viewPagerAdapter.addFragment( listFragment );

    viewPager.setAdapter( viewPagerAdapter );
    viewPager.setOnPageChangeListener( new ViewPagerOnPageChangeListener( getSlidingMenu() ) );
    viewPager.setCurrentItem( 0 );
    getSlidingMenu().setTouchModeAbove( SlidingMenu.LEFT );

    slidingMenuAdapter = new SampleListFragment.SlidingMenuAdapter( _context );
    mFrag.setListAdapter( slidingMenuAdapter );
    /*
     * Button save = (Button) findViewById( R.id.save ); Button saved = (Button) findViewById( R.id.saved );
     * 
     * //findMe.setOnClickListener( new BurritoClickListeners.FindMe( ) ); save.setOnClickListener( new
     * BurritoClickListeners.Save( this ) ); saved.setOnClickListener( new BurritoClickListeners.Saved( viewPager ) );
     */
  }

  //Called when the Activity becomes visible.
  @Override
  protected void onStart()
  {
    super.onStart();
    // Connect the client.
  }

  // Called when the Activity is no longer visible.
  @Override
  protected void onStop()
  {
    super.onStop();
  }

  @Override
  public boolean onCreateOptionsMenu( Menu menu )
  {
    MenuInflater inflater = this.getSupportMenuInflater();
    inflater.inflate( R.menu.main, menu );

    // Add SearchWidget.
    SearchManager searchManager = (SearchManager) getSystemService( Context.SEARCH_SERVICE );
    SearchView searchView = (SearchView) menu.findItem( R.id.action_bar_search ).getActionView();

    searchView.setSearchableInfo( searchManager.getSearchableInfo( getComponentName() ) );
    searchView.setSubmitButtonEnabled( true );
    searchView.setOnQueryTextListener( new SearchViewOnQueryTextListener(this) );

    menu.add( Menu.NONE, MENU_ADD, Menu.NONE, "Save" );
    menu.add( Menu.NONE, MENU_DELETE, Menu.NONE, "Saved" );
    return super.onCreateOptionsMenu( menu );
  }

  @Override
  public void onPause()
  {
    super.onPause();
    SomeUtil.getBus().unregister( this );
  }

  @Override
  public void onResume()
  {
    super.onResume();

    SomeUtil.getBus().register( this );
  }

  @Subscribe
  public void subscriberWithASillyName( FoursquareSearchResult searchResult )
  {
    Response r = searchResult.getResponse();
    List<Venue> venues = r.getVenues();
    slidingMenuAdapter.clear();
    for ( Venue venue : venues )
    {
      Location location = venue.getLocation();
      String id = venue.getId();
      String name = venue.getName();
      if ( location == null || id == null || name == null )
        continue;
      double lat = location.getLat();
      double lng = location.getLng();
      String address = location.getAddress();
      if ( lat == Double.MIN_VALUE || lng == Double.MIN_VALUE )
        continue;
      
      
      SearchResult mySearchResult = new SearchResult();
      mySearchResult._lat = lat;
      mySearchResult._lng = lng;
      mySearchResult._name = name;
      mySearchResult.address = address;
      mySearchResult.id = id;
      mySearchResult._canonicalAddress = venue.getCanonicalUrl();
      DatabaseHelper dbHelper = DatabaseUtil.getDatabaseHelper();
      dbHelper.insertPoint( mySearchResult );
      LatLng pos = new LatLng( mySearchResult._lat, mySearchResult._lng );
      // TODO make a big ass Marker class with its own onclicklistener
      Marker marker = _mapFragment.getMap().addMarker( new MarkerOptions()
                                                           .position( pos )
                                                           .title( mySearchResult._name )
                                                           .snippet( "Kiel is cool" )
                                                           .icon( BitmapDescriptorFactory
                                                                      .fromResource( R.drawable.ic_launcher ) ) );

      currentSearchResults.put( marker, mySearchResult );

      slidingMenuAdapter.add( marker );
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

  public static final int MENU_ADD = Menu.FIRST;
  public static final int MENU_DELETE = Menu.FIRST + 1;

  @Override
  public boolean onOptionsItemSelected( MenuItem item )
  {
    switch ( item.getItemId() )
    {
      case MENU_ADD:
        BurritoClickListeners.displayDialogs( this );
        return true;
      case MENU_DELETE:
        //DatabaseUtil.getDatabaseHelper().retrievePoints( foreignKey );

        return true;
      default:
        return super.onOptionsItemSelected( item );
    }
  }
}
