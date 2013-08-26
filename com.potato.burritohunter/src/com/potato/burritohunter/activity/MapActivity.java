// TODO images, currentlocation and searching pivot markers

package com.potato.burritohunter.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.widget.SearchView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.potato.burritohunter.R;
import com.potato.burritohunter.adapter.ViewPagerAdapter;
import com.potato.burritohunter.database.DatabaseUtil;
import com.potato.burritohunter.database.SavedListItem;
import com.potato.burritohunter.fragment.MyMapFragment;
import com.potato.burritohunter.fragment.POIListFragment;
import com.potato.burritohunter.places.PlacesSearchResult;
import com.potato.burritohunter.stuff.BurritoClickListeners;
import com.potato.burritohunter.stuff.BurritoClickListeners.SearchViewOnQueryTextListener;
import com.potato.burritohunter.stuff.SearchResult;
import com.potato.burritohunter.stuff.SomeUtil;
import com.potato.burritohunter.yelp.YelpSearchResult;
import com.squareup.otto.Subscribe;

public class MapActivity extends BaseActivity
{
  private LatLng PIVOT = new LatLng( 37.798052, -122.406278 );
  public static final HashMap<Marker, SearchResult> currentSearchResults = new HashMap<Marker, SearchResult>();
  public static final ArrayList<Marker> selectedSearchResults = new ArrayList<Marker>();
  private static final String TAG = MapActivity.class.getName();
  ViewPagerAdapter viewPagerAdapter;
  ViewPager viewPager;

  private GoogleMap map;

  @Override
  public void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_map );

    MyMapFragment mapFragment = MyMapFragment.newInstance( PIVOT );

    POIListFragment listFragment = new POIListFragment();
    List<SavedListItem> list = DatabaseUtil.getSavedList();
    listFragment.setPoiList( list );
    
    viewPager=(ViewPager)findViewById(R.id.pager);
    viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
    viewPagerAdapter.addFragment( mapFragment );
    viewPagerAdapter.addFragment( listFragment );
    
    viewPager.setAdapter(viewPagerAdapter);
    /*ft.add( R.id.fragment_container, supportMapFragment, SupportMapFragment.class.getName() );
    ft.setTransition( FragmentTransaction.TRANSIT_FRAGMENT_FADE );
    ft.commit();*/
    viewPager.setOnPageChangeListener(new OnPageChangeListener() {
      @Override
      public void onPageScrollStateChanged(int arg0) { }

      @Override
      public void onPageScrolled(int arg0, float arg1, int arg2) { }

      @Override
      public void onPageSelected(int position) {
          switch (position) {
          case 0:
              getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
              break;
          default:
              getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
              break;
          }
      }

  });
    viewPager.setCurrentItem(0);
    getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

    Button findMe = (Button) findViewById( R.id.find_me );
    Button save = (Button) findViewById( R.id.save );
    Button saved = (Button) findViewById( R.id.saved );

    //findMe.setOnClickListener( new BurritoClickListeners.FindMe( ) );
    save.setOnClickListener( new BurritoClickListeners.Save( this ) );
    saved.setOnClickListener( new BurritoClickListeners.Saved( this ) );
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
  
  @Override
  public void onBackPressed() {
      if (viewPager.getCurrentItem() == 0) {
          // If the user is currently looking at the first step, allow the system to handle the
          // Back button. This calls finish() on this activity and pops the back stack.
          super.onBackPressed();
      } else {
          // Otherwise, select the previous step.
        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
      }
  }

}
