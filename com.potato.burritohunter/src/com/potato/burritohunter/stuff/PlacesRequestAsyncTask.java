package com.potato.burritohunter.stuff;

import android.os.AsyncTask;

import com.potato.burritohunter.places.PlacesSearchResult;
import com.potato.burritohunter.places.PlacesService;
import com.potato.burritohunter.yelp.YelpSearchResult;
import com.potato.burritohunter.yelp.YelpService;
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
		_eventBus.post(result);
		super.onPostExecute(result);
	}
}