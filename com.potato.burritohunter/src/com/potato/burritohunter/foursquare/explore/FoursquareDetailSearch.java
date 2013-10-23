package com.potato.burritohunter.foursquare.explore;

import com.potato.burritohunter.foursquare.search.Meta;

public class FoursquareDetailSearch
{
  private Meta meta;
  private Item response;
  public Meta getMeta()
  {
    return meta;
  }
  public void setMeta( Meta meta )
  {
    this.meta = meta;
  }
  public Item getResponse()
  {
    return response;
  }
  public void setResponse( Item venue )
  {
    this.response = venue;
  }
}