package com.potato.burritohunter.foursquare;

public class Location
{
  private String address;
  private String crossStreet;
  private String city;
  private double lat = Double.MIN_VALUE;
  private double lng = Double.MIN_VALUE;
  private int distance;
  private int postalCode;
  private String state;
  private String country;
  private String cc;
  public String getAddress()
  {
    return address;
  }
  public void setAddress( String address )
  {
    this.address = address;
  }
  public String getCrossStreet()
  {
    return crossStreet;
  }
  public void setCrossStreet( String crossStreet )
  {
    this.crossStreet = crossStreet;
  }
  public String getCity()
  {
    return city;
  }
  public void setCity( String city )
  {
    this.city = city;
  }
  public double getLat()
  {
    return lat;
  }
  public void setLat( double lat )
  {
    this.lat = lat;
  }
  public double getLng()
  {
    return lng;
  }
  public void setLng( double lng )
  {
    this.lng = lng;
  }
  public int getDistance()
  {
    return distance;
  }
  public void setDistance( int distance )
  {
    this.distance = distance;
  }
  public int getPostalCode()
  {
    return postalCode;
  }
  public void setPostalCode( int postalCode )
  {
    this.postalCode = postalCode;
  }
  public String getState()
  {
    return state;
  }
  public void setState( String state )
  {
    this.state = state;
  }
  public String getCountry()
  {
    return country;
  }
  public void setCountry( String country )
  {
    this.country = country;
  }
  public String getCc()
  {
    return cc;
  }
  public void setCc( String cc )
  {
    this.cc = cc;
  }
}