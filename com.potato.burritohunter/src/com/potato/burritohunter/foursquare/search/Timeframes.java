package com.potato.burritohunter.foursquare.search;

import java.util.List;

public class Timeframes
{
  private List<String> days;
  private List<String> open;
  private String renderedTime;
  public List<String> getDays()
  {
    return days;
  }
  public void setDays( List<String> days )
  {
    this.days = days;
  }
  public List<String> getOpen()
  {
    return open;
  }
  public void setOpen( List<String> open )
  {
    this.open = open;
  }
  public String getRenderedTime()
  {
    return renderedTime;
  }
  public void setRenderedTime( String renderedTime )
  {
    this.renderedTime = renderedTime;
  }
}