package com.potato.burritohunter.stuff;

import java.util.List;

import android.os.AsyncTask;

import com.potato.burritohunter.foursquare.FoursquareSearchResult;
import com.potato.burritohunter.foursquare.FoursquareService;
import com.potato.burritohunter.foursquare.Response;
import com.potato.burritohunter.foursquare.Venue;
import com.squareup.otto.Bus;

public class FoursquareRequestAsyncTask extends AsyncTask<Void, Void, FoursquareSearchResult>
{

  Double _lat;
  Double _lng;
  String _query;
  Bus _eventBus;

  public FoursquareRequestAsyncTask( Double lat, Double lng, String query, Bus eventBus )
  { // TODO plz design this better, maybe pass in a POJO
    _lat = lat;
    _lng = lng;
    _query = query;
    _eventBus = eventBus; // don't include in object
  }

  @Override
  protected FoursquareSearchResult doInBackground( Void... params )
  {
    return FoursquareService.search( _lat, _lng, _query );
    // TODO: purge stale shits
    // return FoursquareService.searchVenueDetail( "43e879cbf964a5200f2f1fe3" );
  }

  @Override
  protected void onPostExecute( FoursquareSearchResult result )
  {
    super.onPostExecute( result );
    if ( result == null )
      return;
    Response response = result.getResponse();
    List<Venue> venues = response.getVenues();
    for (Venue venue : venues )
    {
      SearchResult searchResult = new SearchResult();
      
    }

    
  }
  
}
