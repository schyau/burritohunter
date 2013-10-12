package com.potato.burritohunter.stuff;

import com.potato.burritohunter.database.DatabaseHelper;
import com.potato.burritohunter.database.DatabaseUtil;

//choosing a pojo over interfacing the searchresults, is this the right thing to do?
public class SearchResult
{
	/* always have a lat/lng and a name, then tack on extras if needed */
	public Double _lat;
	public Double _lng;
	public String address;
	public String _name;
	public String id;
	public String _canonicalAddress;
	public String photoIcon;
	
	public void serialize ( )
	{
	  DatabaseHelper dbHelper = DatabaseUtil.getDatabaseHelper();
	  //dbHelper.
	}
}


//check to see if it's in db, if not add.
// if yes, check timestamp, if > 30days replace
// logic to grab specific 
