package com.potato.burritohunter.foursquare;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.potato.burritohunter.stuff.ADS;

public class FoursquareService
{
  private static final String TAG = FoursquareService.class.getName();
  private static Gson gson = new GsonBuilder().setFieldNamingPolicy( FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES )
      .create();

  // use a builder for disssss
  public static FoursquareSearchResult search( Double lat, Double lng, String term, int radius, int maxResults )
  {
    String getURL = "https://api.foursquare.com/v2/venues/search" + "?ll=" + lat + "," + lng + "&limit="+maxResults
                    + "&client_id=" + ADS.getInstance().getFoursquareClientId() + "&client_secret="
                    + ADS.getInstance().getFoursquareClientSecret() + "&query=" + term + "&intent=browse"
                    + "&v=20130830" + "&radius="+radius;
    Log.d( FoursquareService.class.getName(), "url: " + getURL );
    String response = search( getURL );

    Log.d( FoursquareService.class.getName(), "Response: " + response );
    FoursquareSearchResult foursquarePlaces = null;
    try
    {
      foursquarePlaces = gson.fromJson( response, FoursquareSearchResult.class );
    }
    catch ( JsonSyntaxException ex )
    {
      Log.e( FoursquareService.class.getName(), ex.getCause() + " : " + ex.getLocalizedMessage() );
    }
    return foursquarePlaces;
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

  // used to refresh single business data
  public static FoursquareSearchResult searchVenueDetail( String id )
  {

    String getURL = "https://api.foursquare.com/v2/venues/" + id + "?client_id="
                    + ADS.getInstance().getFoursquareClientId()
                    + "&client_secret=IXGQODD1YZ5AP4Q0UWUMMGYXSWR02KFCNB5ITWUQJPRZUNHG" + "&v=0130830";
    String response = search( getURL );

    Log.d( FoursquareService.class.getName(), "Response: " + response );
    FoursquareSearchResult foursquarePlaces = null;
    try
    {
      foursquarePlaces = gson.fromJson( response, FoursquareSearchResult.class );
    }
    catch ( JsonSyntaxException ex )
    {
      Log.e( FoursquareService.class.getName(), ex.getCause() + " : " + ex.getLocalizedMessage() );
    }
    return foursquarePlaces;
  }
}
