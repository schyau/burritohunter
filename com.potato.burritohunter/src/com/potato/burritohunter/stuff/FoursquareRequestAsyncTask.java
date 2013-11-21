package com.potato.burritohunter.stuff;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.potato.burritohunter.activity.MapActivity;
import com.potato.burritohunter.activity.Settings;
import com.potato.burritohunter.foursquare.explore.FoursquareExploreResult;
import com.potato.burritohunter.foursquare.explore.FoursquareExploreService;
import com.potato.burritohunter.fragment.MyOtherMapFragment;
import com.squareup.otto.Bus;

public class FoursquareRequestAsyncTask extends AsyncTask<Void, Void, FoursquareExploreResult>
{
  //Context _context;
  MapActivity mapActivity;
  Double _lat;
  Double _lng;
  String _query;
  Bus _eventBus;
  public boolean wasCanceled = false;

  public FoursquareRequestAsyncTask( Double lat, Double lng, String query, Bus eventBus, MapActivity mapActivity )//Context context ) //yep, just took a shit here :)
  { // TODO plz design this better, maybe pass in a POJO
    _lat = lat;
    _lng = lng;
    _query = query;
    _eventBus = eventBus; // don't include in object
    //_context = context;
    this.mapActivity = mapActivity;
  }

  @Override
  protected FoursquareExploreResult doInBackground( Void... params )
  {
    SharedPreferences preferences = ADS.getInstance().getSharedPreferences();
    int maxResults = preferences.getInt( Settings.SEARCH_RESULTS_KEY, 10 );
    int radiusProgress = ( preferences.getInt( Settings.DISTANCE_KEY, 50 ) );
    double radiusMiles = Settings.convertProgressToMileage( radiusProgress );
    double radius = Settings.milesToKm( radiusMiles );
    return FoursquareExploreService.search( _lat, _lng, _query, radius, maxResults );
    // TODO: purge stale shits
    // return FoursquareService.searchVenueDetail( "43e879cbf964a5200f2f1fe3" );
  }

  @Override
  protected void onPostExecute( FoursquareExploreResult result )
  {
    if (!wasCanceled)
    {
      MyOtherMapFragment.fsqrat = null; 
      SomeUtil.stopLoadingRotate( MyOtherMapFragment.loadingView );
      //TODO what about error cases?  do we handle that?
      super.onPostExecute( result );
      if ( result == null || result.getResponse() == null || result.getResponse().getGroups() == null )
      {
        if (result == null)
        {
          
        }
        Toast.makeText( mapActivity, "Search failed.  Check connectivity", Toast.LENGTH_SHORT ).show();
        return; //_eventBus.post( result );
      }
      mapActivity.subscriberWithASillyName( result );
    }
  }
}
