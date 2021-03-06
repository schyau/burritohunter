package com.potato.burritohunter.foursquare.explore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

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
  public static FoursquareExploreResult search( Double lat, Double lng, String term, double radius, int maxResults )
  {
    try
    {
      term = URLEncoder.encode( term, "UTF-8" );
    }
    catch ( UnsupportedEncodingException e )
    {
      return null; // && http://www.myfacewhen.net/uploads/4286-kill-yourself.jpg
    }
    String getURL = "https://api.foursquare.com/v2/venues/explore" + "?ll=" + lat + "," + lng + "&limit=" + maxResults
                    + "&client_id=" + ADS.getInstance().getFoursquareClientId() + "&client_secret="
                    + ADS.getInstance().getFoursquareClientSecret() + "&query=" + term + "&v=20130830" + "&radius="
                    + radius;
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
      Log.e( "asdf", "wtfmate " + e );
    }
    return response;
  }

  public static FoursquareDetailSearch searchDetail( String id )
  {
    String getURL = "https://api.foursquare.com/v2/venues/" + id + "?" + "&client_id="
                    + ADS.getInstance().getFoursquareClientId() + "&client_secret="
                    + ADS.getInstance().getFoursquareClientSecret() + "&v=20131010";

    Log.d( TAG, "detail url: " + getURL );
    String response = search( getURL );
    //File dir = Environment.getExternalStorageDirectory();
    //File yourFile = new File( dir, "x" );
    //String response ="";

    //   response = readFile( yourFile.getAbsolutePath() );

    Log.d( TAG, "Response: " + response );
    FoursquareDetailSearch foursquareDetailSearch = null;
    try
    {
      foursquareDetailSearch = gson.fromJson( response, FoursquareDetailSearch.class );
    }
    catch ( JsonSyntaxException ex )
    {
      Toast.makeText( ADS.getInstance().getContext(), "There was a problem reaching the network", Toast.LENGTH_SHORT )
          .show();
      Log.e( TAG, ex.getCause() + " : " + ex.getLocalizedMessage() );
    }
    return foursquareDetailSearch;
  }
}
