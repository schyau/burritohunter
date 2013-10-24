package com.potato.burritohunter.stuff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.potato.burritohunter.R;
import com.potato.burritohunter.activity.MapActivity;
import com.potato.burritohunter.database.DatabaseHelper;
import com.potato.burritohunter.database.DatabaseUtil;
import com.potato.burritohunter.fragment.MyOtherMapFragment;
import com.potato.burritohunter.fragment.SinglePOIListFragment;

public class SetupThread extends AsyncTask<Void, Void, Void>
{
  private Activity activity;

  //camera
  float zoom;
  float tilt;
  float bearing;
  String lngStr;
  String latStr;

  // pivot
  double lat;
  double lng;

  List<SearchResult> listOfSearializedSearchResults = new ArrayList<SearchResult>();;
  Map<String, Marker> reverseSearchResultHashMap = new HashMap<String, Marker>();
  String searchResultSelectedSerializedString;

  String paneMarkerId;

  String searchQuery;

  public SetupThread( Activity activity )
  {
    this.activity = activity;
  }

  @Override
  protected Void doInBackground( Void... params )
  {
    SharedPreferences prefs = activity.getSharedPreferences( "com.potato.burritohunter", Context.MODE_PRIVATE );
    // inflate search results
    String searchResultSerializedString = prefs.getString( MyOtherMapFragment.SEARCH_RESULT_SERIALIZED_STRING_KEY, "" );

    searchResultSelectedSerializedString = prefs
        .getString( MyOtherMapFragment.SEARCH_RESULT_SELECTED_SERIALIZED_STRING_KEY, "" );

    for ( int i = 0; i < searchResultSerializedString.length(); i += 24 )
    {
      String id = searchResultSerializedString.substring( i, i + 24 );
      DatabaseHelper dbHelper = DatabaseUtil.getDatabaseHelper();
      Cursor c = dbHelper.retrieveSinglePoint( id );
      SearchResult sr = dbHelper.getSearchResult( c );
      listOfSearializedSearchResults.add( sr );
    }

    lat = Double.parseDouble( prefs.getString( MyOtherMapFragment.PIVOT_LAT_KEY, "181" ) );
    lng = Double.parseDouble( prefs.getString( MyOtherMapFragment.PIVOT_LNG_KEY, "181" ) );

    // update camera to the last known configuration
    zoom = prefs.getFloat( MyOtherMapFragment.CAMERA_ZOOM_KEY, Float.MAX_VALUE );
    tilt = prefs.getFloat( MyOtherMapFragment.CAMERA_TILT_KEY, Float.MAX_VALUE );
    bearing = prefs.getFloat( MyOtherMapFragment.CAMERA_BEARING_KEY, Float.MAX_VALUE );
    latStr = prefs.getString( MyOtherMapFragment.CAMERA_LAT_KEY, MyOtherMapFragment.CAMERA_DEFAULT_VAL );
    lngStr = prefs.getString( MyOtherMapFragment.CAMERA_LNG_KEY, MyOtherMapFragment.CAMERA_DEFAULT_VAL );
    searchQuery = prefs.getString( MyOtherMapFragment.SEARCH_QUERY_KEY, "" );
    paneMarkerId = prefs.getString( MyOtherMapFragment.PANEMARKER_ID_KEY, MyOtherMapFragment.PANEMARKER_ID_CLEAR_VALUE );

    return null;
  }

  @Override
  protected void onPostExecute( Void result )
  {
    Log.d( "asdf", "zoom, tilt, bearing, latStr, lngStr: " + zoom + ", " + tilt + ", " + bearing + ", " + latStr + ", "
                   + lngStr );
    for ( SearchResult sr : listOfSearializedSearchResults )
    {
      if ( SinglePOIListFragment.shouldUpdateSearchResult( sr ) )
      {
        continue;
      }
      // TODO make a big ass Marker class with its own onclicklistener
      Marker marker = MyOtherMapFragment.map.addMarker( new MarkerOptions().position( new LatLng( sr._lat, sr._lng ) )
          .icon( BitmapDescriptorFactory.fromResource( R.drawable.item_unselected ) ) );

      MapActivity.currentSearchResults.put( marker, sr );
      reverseSearchResultHashMap.put( sr.id, marker );

      MapActivity.slidingMenuAdapter.add( marker );
    }

    //inflate selected search results
    for ( int i = 0; i < searchResultSelectedSerializedString.length(); i += 24 )
    {
      String id = searchResultSelectedSerializedString.substring( i, i + 24 );
      Marker marker = reverseSearchResultHashMap.get( id );
      if ( marker == null )
      {
        Log.e( "com.potato.burritohunter",
               "selected marker was inflated without an entry in current search results, maybe it's outtadate" );
        continue; // this is also defensive impl, in case other shit fucks up
      }
      MapActivity.selectedSearchResults.add( marker );
      //change marker state 
      marker.setIcon( BitmapDescriptorFactory.fromResource( R.drawable.item_selected ) );
      SearchResult sr = MapActivity.currentSearchResults.get( marker );
      Log.d( "com.potato.burritohunter", "Here: " + sr._name + ",      id: " + id );
    }
    // first, check to see if shared prefs values exist for pivot
    if ( lat == 181 || lng == 181 )
    { //if no, draw marker to hardcoded place, place camera there.
      MyOtherMapFragment.updateAndDrawPivot( MyOtherMapFragment.PIVOT );

      CameraUpdate cp = CameraUpdateFactory.newCameraPosition( new CameraPosition.Builder()
          .target( MyOtherMapFragment.PIVOT ).build() );
      MyOtherMapFragment.map.animateCamera( cp );

      final Context thisContext = activity;
      new AlertDialog.Builder( activity )

      .setMessage( "Would you like to find your current location?" )
          .setPositiveButton( "Yes", new DialogInterface.OnClickListener()
            {
              public void onClick( DialogInterface dialog, int whichButton )
              {
                if ( MapActivity.mLocationClient.isConnected() )
                {
                  Location loc = MapActivity.mLocationClient.getLastLocation();
                  if ( loc == null )
                  {
                    Toast.makeText( thisContext, "an error occurred, apologies", Toast.LENGTH_SHORT ).show();
                  }
                  else
                  {
                    double lat = MapActivity.mLocationClient.getLastLocation().getLatitude();
                    double lng = MapActivity.mLocationClient.getLastLocation().getLongitude();
                    LatLng lastKnownLatLng = new LatLng( lat, lng );
                    MyOtherMapFragment.updateAndDrawPivot( lastKnownLatLng );
                    MyOtherMapFragment.moveCameraToLatLng( lastKnownLatLng );
                  }
                }
                else
                {
                  //  new Thread(                 //MapActivity.mLocationClient.getLastLocation();)
                  Handler handler = new Handler();
                  handler.postDelayed( new Runnable()
                    {
                      public void run()
                      {
                        MyOtherMapFragment.shouldFindMe = false;
                      }
                    }, 5000 );

                  MyOtherMapFragment.shouldFindMe = true;
                }
              }

            } ).setNegativeButton( "No", new DialogInterface.OnClickListener()
            {
              public void onClick( DialogInterface dialog, int whichButton )
              {
                /* User clicked cancel so do some stuff */
              }
            } ).create().show();
    }
    else
    {
      // else reload values, draw pivot
      MyOtherMapFragment.updateAndDrawPivot( new LatLng( lat, lng ) );
      //retrieve paneMarker if one exists()

      // check if paneMarker should be inflated
      if ( MyOtherMapFragment.PANEMARKER_ID_CLEAR_VALUE.equals( paneMarkerId ) )
      {//no
        MyOtherMapFragment.paneMarker = null;
        BottomPagerPanel.getInstance().disableMarkerPanel();
      }
      else
      {//yes
        MyOtherMapFragment.paneMarker = reverseSearchResultHashMap.get( paneMarkerId );
        if ( MyOtherMapFragment.paneMarker != null )
        { //well now check to see that it exsits in currentSearchResults, it could possibly have been skipped if it's out of date
          SearchResult sr = MapActivity.currentSearchResults.get( MyOtherMapFragment.paneMarker ); //TODO make sure every call to this concurrent map can not be null otherwise shit hits the fan
          BottomPagerPanel.getInstance().enableMarkerPanel( sr );
        }
        else
        {
          BottomPagerPanel.getInstance().disableMarkerPanel();
        }
      }

      CameraPosition currentCP = MyOtherMapFragment.map.getCameraPosition();

      LatLng latlng = MyOtherMapFragment.PIVOT;
      if ( MyOtherMapFragment.CAMERA_DEFAULT_VAL.compareTo( latStr ) != 0
           && MyOtherMapFragment.CAMERA_DEFAULT_VAL.compareTo( lngStr ) != 0 )
      {
        try
        {
          double latDbl = Double.parseDouble( latStr );
          double lngDbl = Double.parseDouble( lngStr );
          latlng = new LatLng( latDbl, lngDbl );
        }
        catch ( Exception e )
        {
          //somewhere, Joshua Bloch is crying.
        }

      }
      CameraPosition.Builder updateCP = new CameraPosition.Builder().target( latlng );
      if ( zoom != Float.MAX_VALUE )
      {
        updateCP = updateCP.zoom( zoom ); // (zoomzoom)
      }
      if ( tilt != Float.MAX_VALUE )
      {
        updateCP = updateCP.tilt( tilt );
      }
      if ( bearing != Float.MAX_VALUE )
      {
        updateCP = updateCP.bearing( bearing );
      }

      CameraUpdate cp = CameraUpdateFactory.newCameraPosition( updateCP.build() );
      MyOtherMapFragment.map.animateCamera( cp );
      BottomPagerPanel.getInstance().setBottomPagerButtonsNumsSelectedTextView( MapActivity.selectedSearchResults
                                                                                    .size() + "" );
    }
  }
}
