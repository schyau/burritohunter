package com.potato.burritohunter.foursquare.explore;

import java.util.List;

import com.potato.burritohunter.foursquare.explore.Group;
import com.potato.burritohunter.foursquare.explore.Warning;

public class Response
{
  private Warning warning;
  private String headerLocation;
  private String headerFullLocation;
  private String headerLocationGranularity;
  private String query;
  private int totalResults;
  private List<Group> groups;

  public Warning getWarning()
  {
    return warning;
  }

  public void setWarning( Warning warning )
  {
    this.warning = warning;
  }

  public String getHeaderLocation()
  {
    return headerLocation;
  }

  public void setHeaderLocation( String headerLocation )
  {
    this.headerLocation = headerLocation;
  }

  public String getHeaderFullLocation()
  {
    return headerFullLocation;
  }

  public void setHeaderFullLocation( String headerFullLocation )
  {
    this.headerFullLocation = headerFullLocation;
  }

  public String getHeaderLocationGranularity()
  {
    return headerLocationGranularity;
  }

  public void setHeaderLocationGranularity( String headerLocationGranularity )
  {
    this.headerLocationGranularity = headerLocationGranularity;
  }

  public String getQuery()
  {
    return query;
  }

  public void setQuery( String query )
  {
    this.query = query;
  }

  public int getTotalResults()
  {
    return totalResults;
  }

  public void setTotalResults( int totalResults )
  {
    this.totalResults = totalResults;
  }

  public List<Group> getGroups()
  {
    return groups;
  }

  public void setGroups( List<Group> groups )
  {
    this.groups = groups;
  }
}
