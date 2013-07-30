package com.potato.burritohunter.yelp;

import java.net.URL;

public class Review {
  private String id;
  private int rating;
  private URL ratingImgUrl;
  private URL ratingImgUrlSmall;
  private long timeCreated;
  private User user;

  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public int getRating() {
    return rating;
  }
  public void setRating(int rating) {
    this.rating = rating;
  }
  public URL getRatingImgUrl() {
    return ratingImgUrl;
  }
  public void setRatingImgUrl(URL ratingImgUrl) {
    this.ratingImgUrl = ratingImgUrl;
  }
  public URL getRatingImgUrlSmall() {
    return ratingImgUrlSmall;
  }
  public void setRatingImgUrlSmall(URL ratingImgUrlSmall) {
    this.ratingImgUrlSmall = ratingImgUrlSmall;
  }
  public long getTimeCreated() {
    return timeCreated;
  }
  public void setTimeCreated(long timeCreated) {
    this.timeCreated = timeCreated;
  }
  public User getUser() {
    return user;
  }
  public void setUser(User user) {
    this.user = user;
  }
}