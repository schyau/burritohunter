package com.potato.burritohunter.foursquare.explore;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;

import java.io.File;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonFoo
{
  public static void main(String[] args) throws Exception
  {
    ObjectMapper mapper = new ObjectMapper().setVisibility(FIELD, ANY);
    List<JsonResponse> responses = mapper.readValue(new File("input.json"), new TypeReference<List<JsonResponse>>() {});
    System.out.println(responses);
    System.out.println(mapper.writeValueAsString(responses));
  }
}

class JsonResponse
{
  Venues venue;

  @Override
  public String toString()
  {
    return venue.toString();
  }
}

class Venues
{
  List<VenueSeasons> venue_seasons;
  @JsonProperty("address") String getAdress;
  @JsonProperty("city") String getCity;
  @JsonProperty("name") String getName;
  @JsonProperty("created_at") Date getCreatedAt;
  @JsonProperty("latitude") Double getLatitude;
  @JsonProperty("country") String getCountry;
  @JsonProperty("internal_link_en") String getInternalLinkEN;
  @JsonProperty("internal_link_nl") String getInternalLinkNl;
  @JsonProperty("updated_at") Date getUpdatedAt;
  @JsonProperty("zipcode") String getZipCode;
  @JsonProperty("foursquare_link") String getFoursquareLink;
  @JsonProperty("url") String getURL;
  int id;
  String tip;
  String uid;
  @JsonProperty("phone") String getPhone;
  @JsonProperty("recommended") Boolean getRecommended;
  @JsonProperty("website") String getWebsite;
  List<VenuePhotos> venue_photos;
  @JsonProperty("description") String getDescription;
  @JsonProperty("longitude") Double getLongitude;
  @JsonProperty("thumbnail_location") String getThumbnailLocation;
  List<SubCategories> subcategories;
  @JsonProperty("opening_en") String getOpeningEN;
  @JsonProperty("opening_nl") String getOpeningNL;
  @JsonProperty("hidden") Boolean getHidden;
  @JsonProperty("twitter") String getTwitter;
  List<Themes> themes;
  String tip_en; // not in example JSON

  @Override
  public String toString()
  {
    return String.format("Venues: id=%d", id);
  }
}

class VenuePhotos
{
  @JsonProperty("large") String getLargePhotoURL;
  @JsonProperty("medium") String getMediumPhotoURL;
  @JsonProperty("small") String getSmallPhotoURL;
  @JsonProperty("original") String getOriginalPhotoURL;
  String uid;
  int id;
  int venue_id;
  boolean selected;
  @JsonProperty("created_at") Date getCreatedAt;
  @JsonProperty("updated_at") Date getUpdatedAt;
}

enum VenueSeasons
{
  Spring, Summer, Fall, Winter
}

enum SubCategories
{
  SubCat1, SubCat2, SubCat3, SubCat4 
}

enum Themes
{
  Theme1, Theme2, Theme3, Theme4
}