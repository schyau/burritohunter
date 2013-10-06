package com.potato.burritohunter.foursquare.search;

import java.util.List;

public class Specials
{
  private int count;
  private List<Special> items;
  public int getCount()
  {
    return count;
  }
  public void setCount( int count )
  {
    this.count = count;
  }
  public List<Special> getItems()
  {
    return items;
  }
  public void setItems( List<Special> items )
  {
    this.items = items;
  }
}