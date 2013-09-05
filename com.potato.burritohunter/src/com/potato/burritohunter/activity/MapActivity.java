// TODO images, currentlocation and searching pivot markers

package com.potato.burritohunter.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.Button;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
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
import com.potato.burritohunter.fragment.MyMapFragment;
import com.potato.burritohunter.fragment.POIListFragment;
import com.potato.burritohunter.stuff.BurritoClickListeners;
import com.potato.burritohunter.stuff.BurritoClickListeners.SearchViewOnQueryTextListener;
import com.potato.burritohunter.stuff.BurritoClickListeners.ViewPagerOnPageChangeListener;
import com.potato.burritohunter.stuff.SearchResult;
import com.potato.burritohunter.stuff.SomeUtil;
import com.squareup.otto.Subscribe;

public class MapActivity extends BaseActivity
{
  private LatLng PIVOT = new LatLng( 37.798052, -122.406278 );
  public static final HashMap<Marker, String> currentSearchResults = new HashMap<Marker, String>();
  public static final ArrayList<Marker> selectedSearchResults = new ArrayList<Marker>();
  private static final String TAG = MapActivity.class.getName();
  public static ViewPagerAdapter viewPagerAdapter;
  public static ViewPager viewPager;

  MyMapFragment _mapFragment;

  @Override
  public void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_map );

    _mapFragment = MyMapFragment.newInstance( PIVOT );

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
    getSlidingMenu().setTouchModeAbove( SlidingMenu.TOUCHMODE_FULLSCREEN );

    Button findMe = (Button) findViewById( R.id.find_me );
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
    searchView.setOnQueryTextListener( new SearchViewOnQueryTextListener() );

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
      currentSearchResults.put( marker, id );
    }
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
        BurritoClickListeners.displayDialogs(this);
        return true;
      case MENU_DELETE:
        //DatabaseUtil.getDatabaseHelper().retrievePoints( foreignKey );

        return true;
      default:
        return super.onOptionsItemSelected( item );
    }
  }
}
