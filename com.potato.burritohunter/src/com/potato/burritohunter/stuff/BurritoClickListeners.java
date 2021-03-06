package com.potato.burritohunter.stuff;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.potato.burritohunter.R;
import com.potato.burritohunter.activity.MapActivity;
import com.potato.burritohunter.adapter.SavedListAdapter;
import com.potato.burritohunter.database.DatabaseUtil;
import com.potato.burritohunter.database.SavedListItem;
import com.potato.burritohunter.fragment.BottomPagerMarkerPanel;
import com.potato.burritohunter.fragment.MyOtherMapFragment;
import com.potato.burritohunter.fragment.POIListFragment;

public class BurritoClickListeners
{
  public static class LeftButtonNavigation implements OnClickListener
  {
    @Override
    public void onClick( View v )
    {
      BottomPagerPanel._viewPager.setCurrentItem( BottomPagerPanel.PANEL_MARKER, true );
    }
  }

  public static class RightButtonNavigation implements OnClickListener
  {
    @Override
    public void onClick( View v )
    {
      BottomPagerMarkerPanel._viewPager.setCurrentItem( BottomPagerPanel.PANEL_BUTTONS, true );
    }
  }

  //depracted lol
  public static class ClearUnsaved implements OnClickListener
  {
    @Override
    public void onClick( View v )
    {
      clearUnsaved();
    }
  }

  public static void clearUnsaved()
  {
    MyOtherMapFragment.mySearchView.clearFocus();
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
    if ( MyOtherMapFragment.paneMarker != null
         && MapActivity.currentSearchResults.get( MyOtherMapFragment.paneMarker ) == null )
    {
      MyOtherMapFragment.disablePane();
      MyOtherMapFragment.paneMarker = null;
    }
  }

  //deprecated
  public static class ClearAll implements OnLongClickListener
  {
    @Override
    public boolean onLongClick( View v )
    {
      clearAll();
      return false;

    }
  }

  public static void clearAll()
  {
    MyOtherMapFragment.mySearchView.clearFocus();
    for ( Marker m : MapActivity.currentSearchResults.keySet() )
    {
      m.remove();
    }
    MapActivity.currentSearchResults.clear();
    MapActivity.selectedSearchResults.clear();
    MapActivity.slidingMenuAdapter.clear();
    MyOtherMapFragment.paneMarker = null;
    MyOtherMapFragment.disablePane();
    MyOtherMapFragment.setBottomNumSelectedTextView( "0" );
  }

  public static class Save implements OnClickListener
  {
    private Activity activity;

    public Save( Activity activity )
    {
      this.activity = activity;
    }

    @Override
    public void onClick( View arg0 )
    {
      MyOtherMapFragment.mySearchView.clearFocus();
      LayoutInflater factory = LayoutInflater.from( activity );
      final View textEntryView = factory.inflate( R.layout.save_dialog, null );
      final EditText editText = (EditText) textEntryView.findViewById( R.id.list_edit );
      if ( MapActivity.selectedSearchResults.size() == 0 )
      {
        Toast.makeText( activity, "saving nothing is not allowed!", Toast.LENGTH_SHORT ).show();
        return;
      }
      final AlertDialog dialog = new AlertDialog.Builder( activity )

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
                    Toast.makeText( activity, "name must be greater than 3", Toast.LENGTH_SHORT ).show();
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
                    Toast.makeText( activity, name + " was saved!", Toast.LENGTH_SHORT ).show();

                    List<SavedListItem> list = DatabaseUtil.getSavedList();
                    SavedListAdapter adapter = new SavedListAdapter( MapActivity._listFragment, list );
                    MapActivity._listFragment.setListAdapter( adapter );
                  }
                }
                else
                {
                  Toast.makeText( activity, "name must be greater than 3", Toast.LENGTH_SHORT ).show();
                }
              }

            } ).setNegativeButton( "Cancel", new DialogInterface.OnClickListener()
            {
              public void onClick( DialogInterface dialog, int whichButton )
              {
              }
            } ).create();
      editText.setOnFocusChangeListener( new OnFocusChangeListener()
        {

          @Override
          public void onFocusChange( View v, boolean hasFocus )
          {
            if ( hasFocus )
            {
              dialog.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE );
            }

          }

        } );
      dialog.show();
    }

  }

  public static class MapOnMarkerClickListener implements OnMarkerClickListener
  {
    @Override
    public boolean onMarkerClick( Marker marker )
    {
      if ( !marker.equals( MyOtherMapFragment.pivotMarker ) )
      {
        onMarkerClicked( marker );
      }
      return true;
    }
  }

  public static class OnBottomMarkerPanelPictureClickListener implements OnClickListener
  {
    @Override
    public void onClick( View v )
    {
      Marker marker = MyOtherMapFragment.paneMarker;
      if ( marker != null ) //TODO, do something if it is null!
      {
        onMarkerClicked( marker );
      }
    }
  }

  public static void onMarkerClicked( Marker marker )
  {
    MyOtherMapFragment.changeMarkerState( marker );
    MapActivity.setPaneMarkerBitmap( false );
    MyOtherMapFragment.paneMarker = marker;
    SearchResult sr = MapActivity.currentSearchResults.get( marker );
    MyOtherMapFragment.enablePane( sr );

    MyOtherMapFragment.mySearchView.clearFocus();
    //MyOtherMapFragment.map.animateCamera( CameraUpdateFactory.newLatLng( marker.getPosition() ) );
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
          POIListFragment.listAdapter.whenFragmentOnStop();
          _slidingMenu.setTouchModeAbove( SlidingMenu.LEFT );
          GalleryPoiList.kontinue = false;
          break;
        case 1:
          POIListFragment.listAdapter.startFlipping();
          _slidingMenu.setTouchModeAbove( SlidingMenu.TOUCHMODE_NONE );
          GalleryPoiList.kontinue = true;
          SomeUtil.hideSoftKeyboard( MapActivity.instance, MyOtherMapFragment.mySearchView );
          break;
        case 2:
          POIListFragment.listAdapter.whenFragmentOnStop();
          _slidingMenu.setTouchModeAbove( SlidingMenu.TOUCHMODE_NONE );
          GalleryPoiList.kontinue = false;
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
      MyOtherMapFragment.mySearchView.clearFocus();
      android.location.Location location = _mapActivity.getCurrentLocation();
      if ( location != null )
      {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        Log.d( "FindMe", lat + ", lng: " + lng );
        LatLng newPosLatLng = new LatLng( lat, lng );
        _mapFragment.updateAndDrawPivot( newPosLatLng );
        _mapFragment.moveCameraToLatLng( newPosLatLng );
      }
      else
      {
        Toast.makeText( _mapActivity, "Unable to find your location", Toast.LENGTH_SHORT ).show();
      }
    }
  }

}
