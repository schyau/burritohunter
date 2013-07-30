package com.potato.burritohunter.places;

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
import com.potato.burritohunter.yelp.YelpService;

public class PlacesService {
	private static Gson gson = new GsonBuilder().setFieldNamingPolicy(
			FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

	public static PlacesSearchResult search(Double lat, Double lng, String term) {
		String response = "";
		try {
			HttpClient client = new DefaultHttpClient();
			String getURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&types=food&name=harbour&sensor=false&key="
					+ ADS.getInstance().getGooglePlacesApiKey();
			HttpGet get = new HttpGet(getURL);
			HttpResponse responseGet = client.execute(get);
			HttpEntity resEntityGet = responseGet.getEntity();
			if (resEntityGet != null) {
				// do something with the response
				response = EntityUtils.toString(resEntityGet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.d(PlacesService.class.getName(), "Response: " + response);
		PlacesSearchResult places = null;
		try {
			places = gson.fromJson(response, PlacesSearchResult.class);
		} catch (JsonSyntaxException ex) {
			Log.e(YelpService.class.getName(),
					ex.getCause() + " : " + ex.getLocalizedMessage());
		}
		return places;
	}
}