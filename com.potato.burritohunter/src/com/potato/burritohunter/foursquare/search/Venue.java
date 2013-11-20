package com.potato.burritohunter.foursquare.search;

import java.util.List;

public class Venue
{
  private String id;
  private String name;
  private Contact contact;
  private Location location;
  private String canonicalUrl;
  private List<Category> categories;
  private boolean verified;
  private boolean restricted;
  private double rating;
  
  public double getRating()
  {
    return rating;
  }
  public void setRating( double rating )
  {
    this.rating = rating;
  }
  public String getId()
  {
    return id;
  }
  public void setId( String id )
  {
    this.id = id;
  }
  public String getName()
  {
    return name;
  }
  public void setName( String name )
  {
    this.name = name;
  }
  public Contact getContact()
  {
    return contact;
  }
  public void setContact( Contact contact )
  {
    this.contact = contact;
  }
  public Location getLocation()
  {
    return location;
  }
  public void setLocation( Location location )
  {
    this.location = location;
  }
  public String getCanonicalUrl()
  {
    return canonicalUrl;
  }
  public void setCanonicalUrl( String canonicalUrl )
  {
    this.canonicalUrl = canonicalUrl;
  }
  public List<Category> getCategories()
  {
    return categories;
  }
  public void setCategories( List<Category> categories )
  {
    this.categories = categories;
  }
  public boolean isVerified()
  {
    return verified;
  }
  public void setVerified( boolean verified )
  {
    this.verified = verified;
  }
  public boolean isRestricted()
  {
    return restricted;
  }
  public void setRestricted( boolean restricted )
  {
    this.restricted = restricted;
  }
  public Stats getStats()
  {
    return stats;
  }
  public void setStats( Stats stats )
  {
    this.stats = stats;
  }
  public Menu getMenu()
  {
    return menu;
  }
  public void setMenu( Menu menu )
  {
    this.menu = menu;
  }
  public Specials getSpecials()
  {
    return specials;
  }
  public void setSpecials( Specials specials )
  {
    this.specials = specials;
  }
  public HereNow getHereNow()
  {
    return hereNow;
  }
  public void setHereNow( HereNow hereNow )
  {
    this.hereNow = hereNow;
  }
  public String getReferralId()
  {
    return referralId;
  }
  public void setReferralId( String referralId )
  {
    this.referralId = referralId;
  }
  public String getUrl()
  {
    return url;
  }
  public void setUrl( String url )
  {
    this.url = url;
  }

  public Price getPrice()
  {
    return price;
  }
  public void setPrice( Price price )
  {
    this.price = price;
  }
  public String getStoreId()
  {
    return storeId;
  }
  public void setStoreId( String storeId )
  {
    this.storeId = storeId;
  }
  public String getDescription()
  {
    return description;
  }
  public void setDescription( String description )
  {
    this.description = description;
  }
  public int getCreatedAt()
  {
    return createdAt;
  }
  public void setCreatedAt( int createdAt )
  {
    this.createdAt = createdAt;
  }
  private Stats stats;
  private Menu menu;
  private Specials specials;
  private HereNow hereNow;
  private String referralId;
  private String url;
  //private Hours hours;
  private Price price;
  private String storeId;
  private String description;
  private int createdAt;
  
  // missing mayor, and other classes.
  // Hours class may cause some errors
  // specials nearby
  //https://developer.foursquare.com/docs/responses/venue
}