package com.potato.burritohunter.yelp;

import java.net.URL;
import java.util.List;

public class Business
{
  private String id;
  private String name;
  private URL imageUrl;
  private URL url;
  private URL mobileUrl;
  private String phone;
  private String displayPhone;
  private int reviewCount;
  private List<List<String>> categories;
  private double distance;
  private URL ratingImgUrl;
  private URL ratingImgUrlSmall;
  private String snippetText;
  private URL snippetImgUrl;
  private double rating;
  private Location location;

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

  public URL getImageUrl()
  {
    return imageUrl;
  }

  public void setImageUrl( URL imageUrl )
  {
    this.imageUrl = imageUrl;
  }

  public URL getUrl()
  {
    return url;
  }

  public void setUrl( URL url )
  {
    this.url = url;
  }

  public URL getMobileUrl()
  {
    return mobileUrl;
  }

  public void setMobileUrl( URL mobileUrl )
  {
    this.mobileUrl = mobileUrl;
  }

  public String getPhone()
  {
    return phone;
  }

  public void setPhone( String phone )
  {
    this.phone = phone;
  }

  public String getDisplayPhone()
  {
    return displayPhone;
  }

  public void setDisplayPhone( String displayPhone )
  {
    this.displayPhone = displayPhone;
  }

  public int getReviewCount()
  {
    return reviewCount;
  }

  public void setReviewCount( int reviewCount )
  {
    this.reviewCount = reviewCount;
  }

  public List<List<String>> getCategories()
  {
    return categories;
  }

  public void setCategories( List<List<String>> categories )
  {
    this.categories = categories;
  }

  public double getDistance()
  {
    return distance;
  }

  public void setDistance( double distance )
  {
    this.distance = distance;
  }

  public URL getRatingImgUrl()
  {
    return ratingImgUrl;
  }

  public void setRatingImgUrl( URL ratingImgUrl )
  {
    this.ratingImgUrl = ratingImgUrl;
  }

  public URL getRatingImgUrlSmall()
  {
    return ratingImgUrlSmall;
  }

  public void setRatingImgUrlSmall( URL ratingImgUrlSmall )
  {
    this.ratingImgUrlSmall = ratingImgUrlSmall;
  }

  public String getSnippetText()
  {
    return snippetText;
  }

  public void setSnippetText( String snippetText )
  {
    this.snippetText = snippetText;
  }

  public URL getSnippetImgUrl()
  {
    return snippetImgUrl;
  }

  public void setSnippetImgUrl( URL snippetImgUrl )
  {
    this.snippetImgUrl = snippetImgUrl;
  }

  public double getRating (){
    return rating;
  }
  public void setRating (double rating){
    this.rating=rating;
  }

  public Location getLocation()
  {
    return location;
  }

  public void setLocation( Location location )
  {
    this.location = location;
  }

}
