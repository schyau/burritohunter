// TODO images, currentlocation and searching pivot markers

package com.potato.burritohunter.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.widget.SearchView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.potato.burritohunter.R;
import com.potato.burritohunter.places.PlacesSearchResult;
import com.potato.burritohunter.stuff.BurritoClickListeners;
import com.potato.burritohunter.stuff.BurritoClickListeners.MapOnMarkerClickListener;
import com.potato.burritohunter.stuff.BurritoClickListeners.SearchViewOnQueryTextListener;
import com.potato.burritohunter.stuff.MyLocationHelper;
import com.potato.burritohunter.stuff.SearchResult;
import com.potato.burritohunter.stuff.SomeUtil;
import com.potato.burritohunter.yelp.YelpSearchResult;
import com.squareup.otto.Subscribe;

public class MapActivity extends SherlockFragmentActivity
{
  private LatLng PIVOT = new LatLng( 37.798052, -122.406278 );
  public static final HashMap<Marker, SearchResult> currentSearchResults = new HashMap<Marker, SearchResult>();
  public static final ArrayList<Marker> selectedSearchResults = new ArrayList<Marker>();
  private static final String TAG = MapActivity.class.getName();

  private GoogleMap map;
  private MyLocationHelper myLocationHelper;

  @Override
  protected void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_map );

    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    SupportMapFragment supportMapFragment = new SupportMapFragment();

    ft.add( R.id.fragment_container, supportMapFragment, SupportMapFragment.class.getName() );
    ft.setTransition( FragmentTransaction.TRANSIT_FRAGMENT_FADE );
    ft.commit();

    myLocationHelper = new MyLocationHelper( this );

    Button findMe = (Button) findViewById( R.id.find_me );
    Button save = (Button) findViewById( R.id.save );
    Button saved = (Button) findViewById( R.id.saved );

    findMe.setOnClickListener( new BurritoClickListeners.FindMe( myLocationHelper ) );
    save.setOnClickListener( new BurritoClickListeners.Save( this ) );
    saved.setOnClickListener( new BurritoClickListeners.Saved( this ) );
  }

  //Called when the Activity becomes visible.
  @Override
  protected void onStart()
  {
    super.onStart();
    // Connect the client.
    myLocationHelper.connectClient();
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
    searchView.setOnQueryTextListener( new SearchViewOnQueryTextListener() );
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
    setUpMapIfNeeded();

    map.moveCamera( CameraUpdateFactory.newLatLngZoom( PIVOT, 15 ) ); //zoom to 15
    map.animateCamera( CameraUpdateFactory.zoomTo( 10 ), 2000, null );

    SomeUtil.getBus().register( this );
  }

  //Handle results returned to the FragmentActivity by Google Play services
  @Override
  protected void onActivityResult( int requestCode, int resultCode, Intent data )
  {
    // Decide what to do based on the original request code
    switch ( requestCode )
    {
      case MyLocationHelper.CONNECTION_FAILURE_RESOLUTION_REQUEST:
        switch ( resultCode )
        // If the result code is Activity.RESULT_OK, try to connect again
        {
          case Activity.RESULT_OK: //Try the request again
            break;
        }
    }
  }

  private void setUpMapIfNeeded()
  {
    if ( map == null )
    {
      map = ( (SupportMapFragment) getSupportFragmentManager().findFragmentByTag( SupportMapFragment.class.getName() ) )
          .getMap();
      if ( map != null )
      {
        map.moveCamera( CameraUpdateFactory.newLatLngZoom( PIVOT, 10 ) );
        map.setOnMarkerClickListener( new MapOnMarkerClickListener() );
        myLocationHelper.setMap( map );
      }
    }
  }

  @Subscribe
  public void subscriberWithASillyName( PlacesSearchResult y )
  {
    Log.d( "luls", y.getStatus() );
  }

  @Subscribe
  public void subscriberWithASillyName( YelpSearchResult y )
  {
    Log.d( "luls", y.toString() );

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
}
