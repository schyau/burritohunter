package com.potato.burritohunter.stuff;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.potato.burritohunter.R;
import com.potato.burritohunter.activity.MapActivity;
import com.potato.burritohunter.adapter.SavedListAdapter;
import com.potato.burritohunter.database.DatabaseUtil;
import com.potato.burritohunter.database.SavedListItem;
import com.potato.burritohunter.fragment.MyOtherMapFragment;

public class BurritoClickListeners
{
  public static class Saved implements OnClickListener
  {
    ViewPager _viewPager;

    public Saved( )
    {
    }

    @Override
    public void onClick( View v )
    { 
      clearMarkers(); 
    }
  }

  public static void clearMarkers()
  {
    for ( Marker m : MapActivity.currentSearchResults.keySet() )
    {
      m.remove();
    }
    MapActivity.currentSearchResults.clear();
    MapActivity.selectedSearchResults.clear();
    MapActivity.slidingMenuAdapter.clear();
    MyOtherMapFragment.paneMarker = null;
    MyOtherMapFragment.trasnPanel.setVisibility( View.GONE );
  }
  
  public static class Save implements OnClickListener
  {
    private Context _ctx;

    public Save( Context ctx )
    {
      _ctx = ctx;
    }

    @Override
    public void onClick( View arg0 )
    {
      LayoutInflater factory = LayoutInflater.from( _ctx );
      final View textEntryView = factory.inflate( R.layout.save_dialog, null );
      final EditText editText = (EditText) textEntryView.findViewById( R.id.list_edit );
      if ( MapActivity.selectedSearchResults.size() == 0 )
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
                    /*
                     * ArrayList<SearchResult> poiList = new ArrayList<SearchResult>(); for ( Marker key :
                     * MapActivity.selectedSearchResults ) // gotta get this somehow, maybe put this in its own class
                     * data structure { poiList.add( MapActivity.currentSearchResults.get( key ) ); } //add markers into
                     * db DatabaseUtil.addPOIList( name, poiList ); //TODO should be async
                     */

                    //^old way of doing it, new way is to:
                    // add name and foreign_key to list_table
                    // use marker to look up id from currentSearchResults
                    // add id and foreign_key to foreign_key_table
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

  }

  public static class FindMe implements OnClickListener
  {
    private MyLocationHelper myLocationHelper;

    public FindMe( MyLocationHelper myLocationHelper )
    {
      this.myLocationHelper = myLocationHelper;
    }

    @Override
    public void onClick( View v )
    {
      myLocationHelper.setMyLocation();
    }
  }

  public static class SearchViewOnQueryTextListener implements OnQueryTextListener
  {
    private Context context;

    public SearchViewOnQueryTextListener( Context context )
    {
      this.context = context;
    }

    @Override
    public boolean onQueryTextChange( String newText )
    {
      return false;
    }

    @Override
    public boolean onQueryTextSubmit( String query )
    {
      // TODO change this to a fragment loading?
      //new PlacesRequestAsyncTask( 37.798052, -122.406278, query, SomeUtil.getBus() ).execute(); // need to get this info
      double lat = MyOtherMapFragment.PIVOT.latitude;
      double lng = MyOtherMapFragment.PIVOT.longitude;
      new FoursquareRequestAsyncTask( lat, lng, query, SomeUtil.getBus(), context ).execute(); // need to get this info
      // new YelpRequestAsyncTask( 37.798052, -122.406278, query, SomeUtil.getBus() ).execute();
      return false;
    }
  }

  public static class MapOnMarkerClickListener implements OnMarkerClickListener
  {
    @Override
    public boolean onMarkerClick( Marker marker )
    {
      MyOtherMapFragment.changeMarkerState( marker );
      MyOtherMapFragment.paneMarker = marker;
      MyOtherMapFragment.setPanenlText( MapActivity.currentSearchResults.get( marker ) );
      MyOtherMapFragment.map.animateCamera( CameraUpdateFactory.newLatLng( marker.getPosition() ) );
      return true;
    }
  }

  public static class ViewPagerOnPageChangeListener implements OnPageChangeListener
  {
    private SlidingMenu _slidingMenu;

    public ViewPagerOnPageChangeListener( SlidingMenu slidingMenu )
    {
      _slidingMenu = slidingMenu;
    }

    @Override
    public void onPageScrollStateChanged( int arg0 )
    {
    }

    @Override
    public void onPageScrolled( int arg0, float arg1, int arg2 )
    {
    }

    @Override
    public void onPageSelected( int position )
    {
      switch ( position )
      {
        case 0:
          _slidingMenu.setTouchModeAbove( SlidingMenu.LEFT );
          if ( MapActivity.searchView != null )
          {
            MapActivity.searchView.setVisibility( View.VISIBLE );
          }
          if ( MapActivity.viewInMap != null)
          {
            //MapActivity.viewInMap.setVisibility(View.GONE);
          }
          break;
        case 1:

          if ( MapActivity.searchView != null )
          {
            MapActivity.searchView.setVisibility( View.GONE );
          }
          if ( MapActivity.viewInMap != null)
          {
            //MapActivity.viewInMap.setVisibility(View.GONE);
          }
          _slidingMenu.setTouchModeAbove( SlidingMenu.TOUCHMODE_NONE );
          break;
        case 2:

          if ( MapActivity.searchView != null )
          {
            MapActivity.searchView.setVisibility( View.GONE );
          }
          if ( MapActivity.viewInMap != null)
          {
            //MapActivity.viewInMap.setVisibility(View.VISIBLE);
          }
          _slidingMenu.setTouchModeAbove( SlidingMenu.TOUCHMODE_NONE );
          break;
        default:
          break;
      }
    }
  }

  public static void displayDialogs( final Context _ctx )
  {
    LayoutInflater factory = LayoutInflater.from( _ctx );
    final View textEntryView = factory.inflate( R.layout.save_dialog, null );
    final EditText editText = (EditText) textEntryView.findViewById( R.id.list_edit );
    if ( MapActivity.selectedSearchResults.size() == 0 )
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
                  /*
                   * ArrayList<SearchResult> poiList = new ArrayList<SearchResult>(); for ( Marker key :
                   * MapActivity.selectedSearchResults ) // gotta get this somehow, maybe put this in its own class data
                   * structure { poiList.add( MapActivity.currentSearchResults.get( key ) ); } //add markers into db
                   * DatabaseUtil.addPOIList( name, poiList ); //TODO should be async
                   */

                  //^old way of doing it, new way is to:
                  // add name and foreign_key to list_table
                  // use marker to look up id from currentSearchResults
                  // add id and foreign_key to foreign_key_table
                  ArrayList<String> ids = new ArrayList<String>();
                  for ( Marker marker : MapActivity.selectedSearchResults )
                  {
                    SearchResult searchResult = MapActivity.currentSearchResults.get( marker );
                    String id = searchResult.id;
                    ids.add( id );
                  }
                  DatabaseUtil.addList( name, ids );
                  Toast.makeText( _ctx, name + " was saved!", Toast.LENGTH_SHORT ).show();

                  List<SavedListItem> list = DatabaseUtil.getSavedList();
                  SavedListAdapter adapter = new SavedListAdapter( MapActivity._listFragment, list );
                  MapActivity._listFragment.setListAdapter( adapter );

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

  public static class FindMeOnClickListener implements View.OnClickListener
  {
    private MapActivity _mapActivity;
    private MyOtherMapFragment _mapFragment;

    public FindMeOnClickListener( MapActivity mapActivity, MyOtherMapFragment mapFragment )
    {
      _mapActivity = mapActivity;
      _mapFragment = mapFragment;
    }

    @Override
    public void onClick( View v )
    {
      android.location.Location location = _mapActivity.getCurrentLocation();
      if ( location != null )
      {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        Log.d( "FindMe", lat + ", lng: " + lng );
        _mapFragment.updateAndDrawPivot( new LatLng( lat, lng ) );
      }
    }
  }

}
