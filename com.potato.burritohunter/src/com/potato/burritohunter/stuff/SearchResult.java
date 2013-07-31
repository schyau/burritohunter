package com.potato.burritohunter.stuff;

import java.net.URI;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

//choosing a pojo over interfacing the searchresults, is this the right thing to do?
public class SearchResult
{
	/* always have a lat/lng and a name, then tack on extras if needed */
	public LatLng _latlng;
	public String _name;
	public Extras extras;
	//rating, description, opening hours, price, photo_urls, icon
	
	public class Extras
	{
		//rating, description, opening hours, price, photo_urls, icon
	}
}

