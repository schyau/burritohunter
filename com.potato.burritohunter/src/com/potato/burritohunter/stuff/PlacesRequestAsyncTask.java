package com.potato.burritohunter.stuff;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.potato.burritohunter.places.Geometry;
import com.potato.burritohunter.places.Location;
import com.potato.burritohunter.places.PlacesSearchResult;
import com.potato.burritohunter.places.PlacesService;
import com.potato.burritohunter.places.Stuffffz;
import com.squareup.otto.Bus;

// <params, progress type, result
public class PlacesRequestAsyncTask extends
		AsyncTask<Void, Void, PlacesSearchResult> {

	Double _lat;
	Double _lng;
	String _query;
	Bus _eventBus;

	public PlacesRequestAsyncTask(Double lat, Double lng, String query,
			Bus eventBus) { // TODO plz design this better, maybe pass in a POJO
		_lat = lat;
		_lng = lng;
		_query = query;
		_eventBus = eventBus; // don't include in object
	}

	@Override
	protected PlacesSearchResult doInBackground(Void... params) {
		return PlacesService.search(_lat, _lng, _query);
	}

	@Override
	protected void onPostExecute(PlacesSearchResult result) {
		super.onPostExecute(result);
		if ( result == null ) return;
		Log.d("luls", result.getStatus());
		List<Stuffffz> placesResults = result.getResults();
		
		/* convert to SearchResult */
		ArrayList<SearchResult> placesSearchResults = new ArrayList<SearchResult>();
		for (Stuffffz s : placesResults) {
			if ( s == null)
				continue;
			
			//get latlng
			Geometry geometry = s.getGeometry();
			if (geometry == null)
				continue;
			Location location = geometry.getLocation();
			if (location == null )
				continue;
			
			double lat = location.getLat();
			double lng = location.getLng();

			if (lat == Double.MIN_VALUE || lng == Double.MIN_VALUE)
				continue;
			
			String name = s.getName(); 
			if (name == null )
				continue;
			
			SearchResult searchResult = new SearchResult(); //TODO please use a builder, encapsulate some error checking in there
			searchResult._latlng = new LatLng(lat, lng);
			searchResult._name = name;
			placesSearchResults.add(searchResult);
		}
		_eventBus.post(placesSearchResults);
	}
}