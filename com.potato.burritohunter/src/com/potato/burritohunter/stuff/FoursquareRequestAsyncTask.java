package com.potato.burritohunter.stuff;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.potato.burritohunter.activity.MapActivity;
import com.potato.burritohunter.foursquare.explore.FoursquareExploreResult;
import com.potato.burritohunter.foursquare.explore.FoursquareExploreService;
import com.squareup.otto.Bus;

public class FoursquareRequestAsyncTask extends AsyncTask<Void, Void, FoursquareExploreResult>
{
  private static final String MAX_RESULT_KEY = "MaxSearchResults";
  private static final String RADIUS_KEY = "SearchRadius";
  private static final int MULTIPLIER_TO_KM = 1000;
  //Context _context;
  MapActivity mapActivity;
  Double _lat;
  Double _lng;
  String _query;
  Bus _eventBus;

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
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( mapActivity );
    int maxResults = preferences.getInt( MAX_RESULT_KEY, 10 ) + 1;
    int radius = (preferences.getInt( RADIUS_KEY, 10 ) + 1 ) * MULTIPLIER_TO_KM;
    return FoursquareExploreService.search( _lat, _lng, _query, radius, maxResults );
    // TODO: purge stale shits
    // return FoursquareService.searchVenueDetail( "43e879cbf964a5200f2f1fe3" );
  }

  @Override
  protected void onPostExecute( FoursquareExploreResult result )
  {
    super.onPostExecute( result );
    if ( result == null ) 
      return; _eventBus.post( result );
    mapActivity.subscriberWithASillyName( result );
  }
}
