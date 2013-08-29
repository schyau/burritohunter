package com.potato.burritohunter.stuff;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.potato.burritohunter.R;
import com.potato.burritohunter.activity.MapActivity;
import com.potato.burritohunter.database.DatabaseUtil;
import com.potato.burritohunter.database.SavedListItem;
import com.potato.burritohunter.fragment.POIListFragment;

public class BurritoClickListeners
{
  public static class Saved implements OnClickListener
  {
    FragmentActivity _activity;

    public Saved( FragmentActivity activity )
    {
      _activity = activity;
    }

    @Override
    public void onClick( View v )
    {
      List<SavedListItem> list = DatabaseUtil.getSavedList();
      //for ( SavedListItem s : list ) { Log.w( "asdf SavedListItem", s._id + ", " + s._title ); }
      //List<SearchResult> searchResultList = DatabaseUtil.getSingleSearchResults( "1" );
      //for ( SearchResult s : searchResultList ){Log.w( "asdf searchResult", s._name + ", " + s._latlng.toString() );}

      FragmentManager fm = _activity.getSupportFragmentManager();
      FragmentTransaction ft = _activity.getSupportFragmentManager().beginTransaction();
      POIListFragment listFragment = new POIListFragment();
      listFragment.setPoiList( list );

      ft.add( R.id.map_layout, listFragment, POIListFragment.class.getName() );
      ft.remove( fm.findFragmentById( R.id.fragment_container ) );
      ft.setTransition( FragmentTransaction.TRANSIT_FRAGMENT_FADE );
      ft.commit();
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
                    ArrayList<SearchResult> poiList = new ArrayList<SearchResult>();
                    for ( Marker key : MapActivity.selectedSearchResults ) // gotta get this somehow, maybe put this in its own class data structure
                    {
                      poiList.add( MapActivity.currentSearchResults.get( key ) );
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
    @Override
    public boolean onQueryTextChange( String newText )
    {
      return false;
    }

    @Override
    public boolean onQueryTextSubmit( String query )
    {
      // TODO change this to a fragment loading?
      new PlacesRequestAsyncTask( 37.798052, -122.406278, query, SomeUtil.getBus() ).execute(); // need to get this info
      
      // new YelpRequestAsyncTask( 37.798052, -122.406278, query, SomeUtil.getBus() ).execute();
      return false;
    }
  }

  public static class MapOnMarkerClickListener implements OnMarkerClickListener
  {
    @Override
    public boolean onMarkerClick( Marker marker )
    {
      // TODO Auto-generated method stub
      if ( MapActivity.selectedSearchResults.contains( marker ) )
      {
        marker.setIcon( BitmapDescriptorFactory.fromResource( R.drawable.ic_launcher ) );
        MapActivity.selectedSearchResults.remove( marker );
      }
      else
      {
        marker.setIcon( BitmapDescriptorFactory.fromResource( R.drawable.ic_launcher_clicked ) );
        MapActivity.selectedSearchResults.add( marker );
      }
      return false;
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
          _slidingMenu.setTouchModeAbove( SlidingMenu.TOUCHMODE_FULLSCREEN );
          break;
        default:
          _slidingMenu.setTouchModeAbove( SlidingMenu.TOUCHMODE_NONE );
          break;
      }
    }
  }
}
