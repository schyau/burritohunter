package com.potato.burritohunter.stuff;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.potato.burritohunter.R;
import com.potato.burritohunter.database.DatabaseHelper;
import com.potato.burritohunter.database.DatabaseUtil;

public final class ADS
{
  private static final String TAG = ADS.class.getName();

  private static Context _context;

  /* KEYS KEYS KEYS */
  private static String GOOGLE_MAPS_API_KEY = "";
  private static String GOOGLE_PLACES_API_KEY = "";
  private static String YELP_TOKEN_SECRET = "";
  private static String YELP_CONSUMER_KEY = "";
  private static String YELP_TOKEN = "";
  private static String YELP_CONSUMER_SECRET = "";
  private static String FOURSQUARE_CLIENT_ID;
  private static String FOURSQUARE_CLIENT_SECRET;

  private ADS()
  {
  }

  // thanks Bill Pugh!
  private static class AppDescriptorSingletonHolder
  {
    public static final ADS INSTANCE = new ADS();
  }

  public static ADS getInstance()
  {
    return AppDescriptorSingletonHolder.INSTANCE;
  }

  public void init( Context context )
  {
    _context = context;
    initKeys();
    DatabaseUtil.setDatabaseHelper( new DatabaseHelper( context ) );
  }

  private void initKeys()
  {
    if ( _context == null )
    {
      Log.e( TAG, "well fuck me, context is null" );
      return;
    }

    Resources res = _context.getResources();
    GOOGLE_MAPS_API_KEY = res.getString( R.string.google_maps_api_key );
    GOOGLE_PLACES_API_KEY = res.getString( R.string.google_places_api_key );
    YELP_TOKEN_SECRET = res.getString( R.string.yelp_token_secret );
    YELP_CONSUMER_KEY = res.getString( R.string.yelp_consumer_key );
    YELP_TOKEN = res.getString( R.string.yelp_token );
    YELP_CONSUMER_SECRET = res.getString( R.string.yelp_consumer_secret );
    FOURSQUARE_CLIENT_ID = res.getString( R.string.foursquare_client_id );
    FOURSQUARE_CLIENT_SECRET = res.getString( R.string.foursquare_client_secret );
  }

  public String getGoogleMapsApiKey()
  {
    return GOOGLE_MAPS_API_KEY;
  }

  public String getGooglePlacesApiKey()
  {
    return GOOGLE_PLACES_API_KEY;
  }

  public String getYelpTokenSecret()
  {
    return YELP_TOKEN_SECRET;
  }

  public String getYelpConsumerKey()
  {
    return YELP_CONSUMER_KEY;
  }

  public String getYelpToken()
  {
    return YELP_TOKEN;
  }

  public String getYelpConsumerSecret()
  {
    return YELP_CONSUMER_SECRET;
  }

  public String getFoursquareClientId()
  {
    return FOURSQUARE_CLIENT_ID;
  }

  public String getFoursquareClientSecret()
  {
    return FOURSQUARE_CLIENT_SECRET;
  }
}
