package com.potato.burritohunter.stuff;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.potato.burritohunter.foursquare.FoursquareSearchResult;
import com.potato.burritohunter.foursquare.FoursquareService;
import com.squareup.otto.Bus;

public class FoursquareRequestAsyncTask extends AsyncTask<Void, Void, FoursquareSearchResult>
{
  private static final String MAX_RESULT_KEY = "MaxSearchResults";
  private static final String RADIUS_KEY = "SearchRadius";
  private static final int MULTIPLIER_TO_KM = 1000;
  Context _context;
  Double _lat;
  Double _lng;
  String _query;
  Bus _eventBus;

  public FoursquareRequestAsyncTask( Double lat, Double lng, String query, Bus eventBus, Context context )
  { // TODO plz design this better, maybe pass in a POJO
    _lat = lat;
    _lng = lng;
    _query = query;
    _eventBus = eventBus; // don't include in object
    _context = context;
  }

  @Override
  protected FoursquareSearchResult doInBackground( Void... params )
  {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(_context);
    int maxResults = preferences.getInt( MAX_RESULT_KEY, 10 );
    int radius = preferences.getInt( RADIUS_KEY, 10 ) * MULTIPLIER_TO_KM;
    return FoursquareService.search( _lat, _lng, _query, radius, maxResults );
    // TODO: purge stale shits
    // return FoursquareService.searchVenueDetail( "43e879cbf964a5200f2f1fe3" );
  }

  @Override
  protected void onPostExecute( FoursquareSearchResult result )
  {
    super.onPostExecute( result );
    if ( result == null )
      return;
    _eventBus.post( result );
  }
}
