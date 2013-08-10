package com.potato.burritohunter.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.potato.burritohunter.R;
import com.potato.burritohunter.database.DatabaseUtil;
import com.potato.burritohunter.database.SavedListItem;
import com.potato.burritohunter.fragment.POIListFragment;
import com.potato.burritohunter.places.PlacesSearchResult;
import com.potato.burritohunter.stuff.PlacesRequestAsyncTask;
import com.potato.burritohunter.stuff.SearchResult;
import com.potato.burritohunter.stuff.SomeUtil;
import com.potato.burritohunter.yelp.YelpSearchResult;
import com.squareup.otto.Subscribe;

public class MapActivity extends SherlockFragmentActivity implements GooglePlayServicesClient.ConnectionCallbacks,
    GooglePlayServicesClient.OnConnectionFailedListener
{
  private GoogleMap map;
  private LocationClient mLocationClient;
  Location mCurrentLocation;

  // TODO move this to a file that allows encapsulated global access
  // TODO use a better data structure, what is better?
  private static final HashMap<Marker, SearchResult> currentSearchResults = new HashMap<Marker, SearchResult>();
  private static final ArrayList<Marker> selectedSearchResults = new ArrayList<Marker>();

  private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
  private static final String TAG = MapActivity.class.getName();

  private LatLng PLACE = new LatLng( 37.871744, -122.260963 );
  private static Marker MY_LOCATION; // TODO serialize last location and put
  // it in here on app startup.

  private Context _ctx;

  @Override
  protected void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_map );

    //FragmentManager fm = getSupportFragmentManager();
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    SupportMapFragment supportMapFragment = new SupportMapFragment();

    ft.add( R.id.fragment_container, supportMapFragment, SupportMapFragment.class.getName() );
    ft.setTransition( FragmentTransaction.TRANSIT_FRAGMENT_FADE );
    ft.commit();

    _ctx = this;
    /*
     * Create a new location client, using the enclosing class to handle callbacks.
     */
    mLocationClient = new LocationClient( this, this, this );

    Button findMe = (Button) findViewById( R.id.find_me );
    Button save = (Button) findViewById( R.id.save );
    Button saved = (Button) findViewById( R.id.saved );
    findMe.setOnClickListener( new OnClickListener()
      {

        @Override
        public void onClick( View v )
        {
          getCurrentLocation();
          if ( mCurrentLocation != null )
          {
            clearMyLocationMarker();
            MY_LOCATION = map.addMarker( new MarkerOptions().position( new LatLng( mCurrentLocation.getLatitude(),
                                                                                   mCurrentLocation.getLongitude() ) ) );
          }
          else
          {
            Toast.makeText( _ctx, "can't get current location sry", Toast.LENGTH_SHORT ).show();
          }

        }

      } );
    save.setOnClickListener( new OnClickListener()
      {
        @Override
        public void onClick( View arg0 )
        {
          LayoutInflater factory = LayoutInflater.from( _ctx );
          final View textEntryView = factory.inflate( R.layout.save_dialog, null );
          final EditText editText = (EditText) textEntryView.findViewById( R.id.list_edit );
          if ( selectedSearchResults.size() == 0 )
          {
            Toast.makeText( _ctx, "saving nothing is not allowed!", Toast.LENGTH_SHORT ).show();
            return;
          }
          new AlertDialog.Builder( _ctx )

          .setMessage( "What would you like to name this trip?" ).setView( textEntryView )
              .setPositiveButton( "Continue", new DialogInterface.OnClickListener()
                {
                  public void onClick( DialogInterface dialog, int whichButton )
                  {
                    Editable e = editText.getText();
                    if ( e != null )
                    {
                      String name = e.toString();
                      if ( name == null || name.length() < 3 )
                      {
                        Toast.makeText( _ctx, "name must be greater than 3", Toast.LENGTH_SHORT ).show();
                      }
                      else
                      {
                        ArrayList<SearchResult> poiList = new ArrayList<SearchResult>();
                        for ( Marker key : selectedSearchResults )
                        {
                          poiList.add( currentSearchResults.get( key ) );
                        }
                        //add markers into db
                        DatabaseUtil.addPOIList( name, poiList ); //TODO should be async
                        Toast.makeText( _ctx, name + " was saved!", Toast.LENGTH_SHORT ).show();
                      }
                    }
                    else
                    {
                      Toast.makeText( _ctx, "name must be greater than 3", Toast.LENGTH_SHORT ).show();
                    }
                  }

                } ).setNegativeButton( "Cancel", new DialogInterface.OnClickListener()
                {
                  public void onClick( DialogInterface dialog, int whichButton )
                  {
                    /* User clicked cancel so do some stuff */
                  }
                } ).create().show();
        }
      } );

    saved.setOnClickListener( new OnClickListener()
      {

        @Override
        public void onClick( View v )
        {
          List<SavedListItem> list = DatabaseUtil.getSavedList();
          //for ( SavedListItem s : list ) { Log.w( "asdf SavedListItem", s._id + ", " + s._title ); }
          //List<SearchResult> searchResultList = DatabaseUtil.getSingleSearchResults( "1" );
          //for ( SearchResult s : searchResultList ){Log.w( "asdf searchResult", s._name + ", " + s._latlng.toString() );}

          FragmentManager fm = getSupportFragmentManager();
          FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
          POIListFragment listFragment = new POIListFragment();
          listFragment.setPoiList( list );

          ft.add( R.id.map_layout, listFragment, POIListFragment.class.getName() );
          ft.remove( fm.findFragmentById( R.id.fragment_container) );
          ft.setTransition( FragmentTransaction.TRANSIT_FRAGMENT_FADE );
          ft.commit();
        }

      } );
  }

  /*
   * Called when the Activity becomes visible.
   */
  @Override
  protected void onStart()
  {
    super.onStart();
    // Connect the client.
    mLocationClient.connect();
  }

  /*
   * Called when the Activity is no longer visible.
   */
  @Override
  protected void onStop()
  {
    // Disconnecting the client invalidates it.
    // mLocationClient.disconnect();
    super.onStop();
  }

  @Override
  public boolean onCreateOptionsMenu( Menu menu )
  {
    MenuInflater inflater = this.getSupportMenuInflater();
    inflater.inflate(R.menu.main, menu);


    
    // Add SearchWidget.
    SearchManager searchManager = (SearchManager) getSystemService( Context.SEARCH_SERVICE );
    SearchView searchView = (SearchView) menu.findItem( R.id.action_bar_search ).getActionView();

    searchView.setSearchableInfo( searchManager.getSearchableInfo( getComponentName() ) );
    

    searchView.setSubmitButtonEnabled( true );
    searchView.setOnQueryTextListener( new OnQueryTextListener()
      {

        @Override
        public boolean onQueryTextChange( String newText )
        {
          return false;
        }

        @Override
        public boolean onQueryTextSubmit( String query )
        {
          return false;
          
        }
      } );
    return super.onCreateOptionsMenu(menu);
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
    // new YelpRequestAsyncTask(37.871744, -122.260963, "burritos",
    // SomeUtil.getBus()).execute();
    new PlacesRequestAsyncTask( 37.871744, -122.260963, "museum", SomeUtil.getBus() ).execute();
    map = ( (SupportMapFragment) getSupportFragmentManager().findFragmentByTag( SupportMapFragment.class.getName() ) )
        .getMap();

    // should i store the marker or the pojo?
    map.setOnMarkerClickListener( new OnMarkerClickListener()
      {
        @Override
        public boolean onMarkerClick( Marker marker )
        {
          // TODO Auto-generated method stub
          if ( selectedSearchResults.contains( marker ) )
          {
            marker.setIcon( BitmapDescriptorFactory.fromResource( R.drawable.ic_launcher ) );
            selectedSearchResults.remove( marker );
          }
          else
          {
            marker.setIcon( BitmapDescriptorFactory.fromResource( R.drawable.ic_launcher_clicked ) );
            selectedSearchResults.add( marker );
          }
          return false;
        }
      } );

    // Move the camera instantly to hamburg with a zoom of 15.
    map.moveCamera( CameraUpdateFactory.newLatLngZoom( PLACE, 15 ) );

    // Zoom in, animating the camera.
    map.animateCamera( CameraUpdateFactory.zoomTo( 10 ), 2000, null );
    SomeUtil.getBus().register( this );
  }

  @Subscribe
  public void subscriberWithASillyName( YelpSearchResult y )
  {
    Log.d( "luls", y.toString() );

  }

  @Subscribe
  public void subscriberWithASillyName( PlacesSearchResult y )
  {
    Log.d( "luls", y.getStatus() );
  }

  @Subscribe
  public void subscriberWithASillyName( ArrayList<SearchResult> searcResults )
  {
    // Log.d("luls", y.getStatus());
    for ( SearchResult s : searcResults )
    {
      // TODO make a big ass Marker class with its own onclicklistener
      Marker marker = map.addMarker( new MarkerOptions().position( s._latlng ).title( s._name )
          .snippet( "Kiel is cool" ).icon( BitmapDescriptorFactory.fromResource( R.drawable.ic_launcher ) ) );
      currentSearchResults.put( marker, s );
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

    // Return a Dialog to the DialogFragment.
    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState )
    {
      return mDialog;
    }
  }

  /*
   * Handle results returned to the FragmentActivity by Google Play services
   */
  @Override
  protected void onActivityResult( int requestCode, int resultCode, Intent data )
  {
    // Decide what to do based on the original request code
    switch ( requestCode )
    {
      case CONNECTION_FAILURE_RESOLUTION_REQUEST:
        /*
         * If the result code is Activity.RESULT_OK, try to connect again
         */
        switch ( resultCode )
        {
          case Activity.RESULT_OK:
            /*
             * Try the request again
             */
            break;
        }
    }
  }

  private boolean servicesConnected()
  {
    // Check that Google Play services is available
    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable( this );
    // If Google Play services is available
    if ( ConnectionResult.SUCCESS == resultCode )
    {
      // In debug mode, log the status
      Log.d( "Location Updates", "Google Play services is available." );
      // Continue
      return true;
      // Google Play services was not available for some reason
    }
    else
    {
      // Get the error code
      // int errorCode = connectionResult.getErrorCode(); wtf man?
      // Get the error dialog from Google Play services
      Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog( resultCode, this,
                                                                  CONNECTION_FAILURE_RESOLUTION_REQUEST );

      // If Google Play services can provide an error dialog
      if ( errorDialog != null )
      {
        // Create a new DialogFragment for the error dialog
        ErrorDialogFragment errorFragment = new ErrorDialogFragment();
        // Set the dialog in the DialogFragment
        errorFragment.setDialog( errorDialog );
        // Show the error dialog in the DialogFragment
        errorFragment.show( getSupportFragmentManager(), "Location Updates" );
      }
      return false;
    }
  }

  @Override
  public void onConnectionFailed( ConnectionResult connectionResult )
  {
    /*
     * Google Play services can resolve some errors it detects. If the error has a resolution, try sending an Intent to
     * start a Google Play services activity that can resolve error.
     */
    if ( connectionResult.hasResolution() )
    {
      try
      {
        // Start an Activity that tries to resolve the error
        connectionResult.startResolutionForResult( this, CONNECTION_FAILURE_RESOLUTION_REQUEST );
        /*
         * Thrown if Google Play services canceled the original PendingIntent
         */
      }
      catch ( IntentSender.SendIntentException e )
      {
        // Log the error
        e.printStackTrace();
      }
    }
    else
    {
      /*
       * If no resolution is available, display a dialog to the user with the error.
       */
      // showErrorDialog(connectionResult.getErrorCode());
      Toast.makeText( this, "Connection Failed!", Toast.LENGTH_SHORT ).show();
    }
  }

  @Override
  public void onConnected( Bundle connectionHint )
  {
    // Display the connection status
    Toast.makeText( this, "Connected", Toast.LENGTH_SHORT ).show();
    mCurrentLocation = mLocationClient.getLastLocation();
    if ( mCurrentLocation != null )
    {
      MY_LOCATION = map.addMarker( new MarkerOptions()
          .position( new LatLng( mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude() ) ).title( "Kiel" )
          .snippet( "Kiel is cool" ).icon( BitmapDescriptorFactory.fromResource( R.drawable.ic_launcher ) ) );
    }

  }

  @Override
  public void onDisconnected()
  {
    // Display the connection status
    Toast.makeText( this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT ).show();

  }

  private void clearMyLocationMarker()
  {
    if ( MY_LOCATION != null )
    {
      MY_LOCATION.setVisible( false );
      MY_LOCATION.remove();
    }
  }

  private void getCurrentLocation()
  {
    if ( mLocationClient != null )
    {
      mCurrentLocation = mLocationClient.getLastLocation();
    }
    else
    {
    }
  }

}
