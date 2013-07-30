package com.potato.burritohunter.yelp;

import java.net.URL;

public class User {
  private String id;
  private URL imageUrl;
  private String name;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public URL getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(URL imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}