package com.potato.burritohunter.foursquare.search;

import java.util.List;

import com.potato.burritohunter.foursquare.search.Venue;

public class Response
{
  private List<Venue> venues;

  public List<Venue> getVenues()
  {
    return venues;
  }

  public void setVenues( List<Venue> venues )
  {
    this.venues = venues;
  }
}