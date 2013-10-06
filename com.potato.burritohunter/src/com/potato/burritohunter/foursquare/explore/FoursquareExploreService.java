package com.potato.burritohunter.foursquare.explore;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.potato.burritohunter.stuff.ADS;

public class FoursquareExploreService
{
  private static final String TAG = FoursquareExploreService.class.getName();
  private static Gson gson = new GsonBuilder().setFieldNamingPolicy( FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES )
      .create();

  // use a builder for disssss
  public static FoursquareExploreResult search( Double lat, Double lng, String term, int radius, int maxResults )
  {
    String getURL = "https://api.foursquare.com/v2/venues/explore" + "?ll=" + lat + "," + lng + "&limit="+maxResults
                    + "&client_id=" + ADS.getInstance().getFoursquareClientId() + "&client_secret="
                    + ADS.getInstance().getFoursquareClientSecret() + "&query=" + term
                    + "&v=20130830" + "&radius="+radius;
    Log.d( TAG, "url: " + getURL );
    String response = search( getURL );

    Log.d( TAG, "Response: " + response );
    FoursquareExploreResult foursquareExplorePlaces = null;
    try
    {
      foursquareExplorePlaces = gson.fromJson( response, FoursquareExploreResult.class );
    }
    catch ( JsonSyntaxException ex )
    {
      Log.e( TAG, ex.getCause() + " : " + ex.getLocalizedMessage() );
    }
    return foursquareExplorePlaces;
  }

  private static String search( String getURL )
  {
    String response = "";
    try
    {

      Log.d( TAG, getURL );
      HttpClient client = new DefaultHttpClient();
      HttpGet get = new HttpGet( getURL );
      HttpResponse responseGet = client.execute( get );
      HttpEntity resEntityGet = responseGet.getEntity();
      if ( resEntityGet != null )
      {
        // do something with the response
        response = EntityUtils.toString( resEntityGet );
      }
    }
    catch ( Exception e )
    {

    }
    return response;
  }
}
