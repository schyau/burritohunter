package com.potato.burritohunter.database;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;
import com.potato.burritohunter.activity.MapActivity;
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

  public static DatabaseHelper getDatabaseHelper()
  {
    return _dbHelper;
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
        long id = cursor.getInt( idIndex );
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

  public static void addList ( String listName, List<String> ids )
  {
    long rowid = _dbHelper.saveList( listName );
    for( String id : ids )
    {
      _dbHelper.saveForeignKey( id, rowid );
    }
  }
}
