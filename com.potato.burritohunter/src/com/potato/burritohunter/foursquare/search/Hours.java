package com.potato.burritohunter.foursquare.search;

public class Hours
{
  private String status;
  private boolean isOpen;
  private Timeframes timeframes;
  public String getStatus()
  {
    return status;
  }
  public void setStatus( String status )
  {
    this.status = status;
  }
  public boolean isOpen()
  {
    return isOpen;
  }
  public void setOpen( boolean isOpen )
  {
    this.isOpen = isOpen;
  }
  public Timeframes getTimeframes()
  {
    return timeframes;
  }
  public void setTimeframes( Timeframes timeframes )
  {
    this.timeframes = timeframes;
  }
}