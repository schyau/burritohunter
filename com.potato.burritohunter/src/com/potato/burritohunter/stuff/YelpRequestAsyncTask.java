package com.potato.burritohunter.stuff;

import android.os.AsyncTask;

import com.potato.burritohunter.yelp.YelpSearchResult;
import com.potato.burritohunter.yelp.YelpService;
import com.squareup.otto.Bus;

// <params, progress type, result
public class YelpRequestAsyncTask extends AsyncTask<Void, Void, YelpSearchResult> {

	Double _lat;
	Double _lng;
	String _query;
	Bus _eventBus;

	public YelpRequestAsyncTask ( Double lat, Double lng, String query, Bus eventBus )
	{ //TODO plz design this better, maybe pass in an object
		_lat = lat;
		_lng = lng;
		_query = query;
		_eventBus = eventBus; //don't include in object
	}
	
	@Override
	protected YelpSearchResult doInBackground(Void... params) 
	{
		return YelpService.search(_lat, _lng, _query);
	}

	@Override
	protected void onPostExecute(YelpSearchResult result) {
		
		//TODO turn this into a SearchResult
		_eventBus.post(result);
		super.onPostExecute(result);
	}
}