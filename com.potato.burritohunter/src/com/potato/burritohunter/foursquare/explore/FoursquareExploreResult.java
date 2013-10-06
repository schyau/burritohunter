package com.potato.burritohunter.foursquare.explore;

import com.potato.burritohunter.foursquare.search.Meta;
import com.potato.burritohunter.foursquare.explore.Response;


public class FoursquareExploreResult
{
  private Meta meta;
  private String numResults;
  private Response response;

  public String getNumResults()
  {
    return numResults;
  }
  public void setNumResults( String numResults )
  {
    this.numResults = numResults;
  }
  public Meta getMeta()
  {
    return meta;
  }
  public void setMeta( Meta meta )
  {
    this.meta = meta;
  }
  public Response getResponse()
  {
    return response;
  }
  public void setResponse( Response response )
  {
    this.response = response;
  }
}