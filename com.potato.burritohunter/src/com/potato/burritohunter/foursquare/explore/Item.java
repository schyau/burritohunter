package com.potato.burritohunter.foursquare.explore;

import com.potato.burritohunter.foursquare.search.Venue;

public class Item
{
  //private Reasons reasons;//we don't need this!
  //private Snippets snippet; //we don't need this!
  private Venue venue;

  public Venue getVenue()
  {
    return venue;
  }

  public void setVenue( Venue venue )
  {
    this.venue = venue;
  }
}