package com.potato.burritohunter.database;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;
import com.potato.burritohunter.stuff.SearchResult;

public class DatabaseUtil
{
  private static DatabaseHelper _dbHelper;

  public static void setDatabaseHelper( DatabaseHelper dbHelper )
  {
    if ( _dbHelper == null )
    {
      _dbHelper = dbHelper;
    }
  }

  /* retrieves all search results for screen 3 */
  public static List<SearchResult> getSingleSearchResults(String foreignKey)
  {
    Cursor cursor = _dbHelper.querySinglePOIForeignKey( foreignKey );
    List<SearchResult> list = new ArrayList<SearchResult>();
    // looping through all rows and adding to list
    if ( cursor.moveToFirst() )
    {
      int nameIndex = cursor.getColumnIndex( DatabaseHelper.KEY_NAME );
      int lngIndex = cursor.getColumnIndex( DatabaseHelper.KEY_LNG );
      int latIndex = cursor.getColumnIndex( DatabaseHelper.KEY_LAT );
      do
      {
        SearchResult searchResult = new SearchResult();
        searchResult._name = cursor.getString( nameIndex );
        double lat = Double.parseDouble( cursor.getString( latIndex ) );
        double lng = Double.parseDouble( cursor.getString( lngIndex ) );
        searchResult._latlng = new LatLng( lat, lng );
        list.add( searchResult );
      } while ( cursor.moveToNext() );
    }
    return list;
  }

  public static List<SavedListItem> getSavedList()
  {
    List <SavedListItem> listOfListItems = new ArrayList<SavedListItem>();
    Cursor cursor = _dbHelper.queryAllListPOIs();
    if( cursor.moveToFirst())
    {
      int idIndex = cursor.getColumnIndex( DatabaseHelper.KEY_ID );
      int nameIndex = cursor.getColumnIndex( DatabaseHelper.KEY_NAME );
      do
      {
        int id = cursor.getInt( idIndex );
        String name = cursor.getString( nameIndex );
        listOfListItems.add(new SavedListItem( id, name));
        
      } while ( cursor.moveToNext());
    }
    return listOfListItems;
  }
  
  public static void addPOIList( String listName, List<SearchResult> poiList )
  {
    _dbHelper.addPOIList( listName, poiList );
  }
  

}
