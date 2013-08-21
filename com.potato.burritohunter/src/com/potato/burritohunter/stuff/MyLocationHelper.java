package com.potato.burritohunter.stuff;

import android.app.Dialog;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.potato.burritohunter.R;

public class MyLocationHelper implements GooglePlayServicesClient.ConnectionCallbacks,
    GooglePlayServicesClient.OnConnectionFailedListener
{
  private FragmentActivity fragmentActivity;
  private GoogleMap map;
  private LocationClient mLocationClient;
  Location mCurrentLocation;
  public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

  private Marker MY_LOCATION;

  public MyLocationHelper( FragmentActivity fragmentActivity )
  {
    this.fragmentActivity = fragmentActivity;
    mLocationClient = new LocationClient( fragmentActivity, this, this );
  }

  @Override
  public void onConnected( Bundle connectionHint )
  {
    // Display the connection status
    Toast.makeText( fragmentActivity, "Connected", Toast.LENGTH_SHORT ).show();
    mCurrentLocation = mLocationClient.getLastLocation();
    if ( mCurrentLocation != null )
    {

      // get rid of map var?  
      MY_LOCATION = map.addMarker( new MarkerOptions()
          .position( new LatLng( mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude() ) ).title( "Kiel" )
          .snippet( "Kiel is cool" ).icon( BitmapDescriptorFactory.fromResource( R.drawable.ic_launcher ) ) );
    }
  }
  
  public void setMyLocation ()
  {
    Location location = getCurrentLocation(); // find some way to get this info
    if ( location != null )
    {
      if ( MY_LOCATION != null)
      {
        MY_LOCATION.remove();
        MY_LOCATION = null;
      }
      MY_LOCATION = map.addMarker( new MarkerOptions().position( new LatLng( location.getLatitude(),
                                                                             location.getLongitude() ) ) );
    }
    else
    {
      Toast.makeText( fragmentActivity, "can't get current location sry", Toast.LENGTH_SHORT ).show();
    }
  }

  public void setMap( GoogleMap map )
  {
    this.map = map;
  }

  @Override
  public void onDisconnected()
  {
    // Display the connection status
    Toast.makeText( fragmentActivity, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT ).show();

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
        connectionResult.startResolutionForResult( fragmentActivity, CONNECTION_FAILURE_RESOLUTION_REQUEST );
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
      Toast.makeText( fragmentActivity, "Connection Failed!", Toast.LENGTH_SHORT ).show();
    }
  }

  public Location getCurrentLocation()
  {
    if ( mLocationClient != null )
    {
      if ( servicesConnected() )
      {
        return mLocationClient.getLastLocation();
      }
    }
    return null;
  }

  private boolean servicesConnected()
  {
    // Check that Google Play services is available
    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable( fragmentActivity );
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
      Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog( resultCode, fragmentActivity,
                                                                  CONNECTION_FAILURE_RESOLUTION_REQUEST );

      // If Google Play services can provide an error dialog
      if ( errorDialog != null )
      {
        // Create a new DialogFragment for the error dialog
        ErrorDialogFragment errorFragment = new ErrorDialogFragment();
        // Set the dialog in the DialogFragment
        errorFragment.setDialog( errorDialog );
        // Show the error dialog in the DialogFragment
        errorFragment.show( fragmentActivity.getSupportFragmentManager(), "Location Updates" );
      }
      return false;
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

  public void connectClient()
  {
    mLocationClient.connect();
  }

  public void disconnectClient()
  {
    mLocationClient.disconnect();
  }
}
