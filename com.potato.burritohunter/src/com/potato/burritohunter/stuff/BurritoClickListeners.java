package com.potato.burritohunter.stuff;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
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
  public static class ClearUnsaved implements OnClickListener
  {
    @Override
    public void onClick( View v )
    {
      Set<Marker> set = new HashSet<Marker>( MapActivity.currentSearchResults.keySet() );
      for ( Marker m : set )
      {
        if ( MapActivity.selectedSearchResults.contains( m ) )
          continue;
        m.remove();
        MapActivity.currentSearchResults.remove( m );
        MapActivity.selectedSearchResults.remove( m );
        MapActivity.slidingMenuAdapter.remove( m );
      }
      if ( MapActivity.currentSearchResults.get( MyOtherMapFragment.paneMarker ) == null )
      {
        BottomPagerPanel.getInstance().disableMarkerPanel();
      }
    }
  }

  public static class ClearAll implements OnLongClickListener
  {

    @Override
    public boolean onLongClick( View v )
    {
      for ( Marker m : MapActivity.currentSearchResults.keySet() )
      {
        m.remove();
      }
      MapActivity.currentSearchResults.clear();
      MapActivity.selectedSearchResults.clear();
      MapActivity.slidingMenuAdapter.clear();
      MyOtherMapFragment.paneMarker = null;
      //chyauchyauMyOtherMapFragment.trasnPanel.setVisibility( View.GONE );
      BottomPagerPanel.getInstance().disableMarkerPanel();
      return false;
    }
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
      if ( marker.equals( MyOtherMapFragment.pivotMarker ) )
      {
        return true;
      }
      MyOtherMapFragment.changeMarkerState( marker );
      MyOtherMapFragment.paneMarker = marker;
      SearchResult sr = MapActivity.currentSearchResults.get( marker );
      BottomPagerPanel.getInstance().enableMarkerPanel( sr );
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
          if ( MapActivity.viewInMap != null )
          {
            MapActivity.viewInMap.setVisible( false );
          }
          break;
        case 1:

          if ( MapActivity.searchView != null )
          {
            MapActivity.searchView.setVisibility( View.GONE );
          }
          if ( MapActivity.viewInMap != null )
          {
            MapActivity.viewInMap.setVisible( false );
          }
          _slidingMenu.setTouchModeAbove( SlidingMenu.TOUCHMODE_NONE );
          break;
        case 2:

          if ( MapActivity.searchView != null )
          {
            MapActivity.searchView.setVisibility( View.GONE );
          }
          if ( MapActivity.viewInMap != null )
          {
            MapActivity.viewInMap.setVisible( true );
          }
          _slidingMenu.setTouchModeAbove( SlidingMenu.TOUCHMODE_NONE );
          break;
        default:
          break;
      }
    }
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
