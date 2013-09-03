package com.potato.burritohunter.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.potato.burritohunter.stuff.SearchResult;

public class DatabaseHelper extends SQLiteOpenHelper
{

  private static final String TAG = DatabaseHelper.class.getName();
  // All Static variables
  // Database Version
  private static final int DATABASE_VERSION = 1;

  // Database Name
  private static final String DATABASE_NAME = "datBurrito";

  //list, single, multiforeignkeyrefs tables
  public static final String TABLE_LIST_POI = "poi_list";
  public static final String TABLE_SINGLE_POI = "poi_single";
  public static final String TABLE_FOREIGN_KEY = "foriegn_key_table";

  // Table 'poi_list' and 'poi_single' names
  public static final String KEY_ID = "id";
  public static final String KEY_FOREIGN_KEY = "foreign_key";
  public static final String KEY_NAME = "name";
  public static final String KEY_LAT = "latitude";
  public static final String KEY_LNG = "longitude";
  public static final String KEY_ADDRESS = "address";
  


  public DatabaseHelper( Context context )
  {
    super( context, DATABASE_NAME, null, DATABASE_VERSION );
  }

  // Creating Tables
  @Override
  public void onCreate( SQLiteDatabase db )
  {
    // CREATE TABLE poi (id INTEGER PRIMARY KEY, name TEXT );
    String CREATE_POI_LIST_TABLE = "CREATE TABLE " + TABLE_LIST_POI + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME
                                   + " TEXT" + ")";

    // create table foreign_key_table
    // (
    //  id integer not null references poi_list(id), 
    //  foreign_key integer not null references poi_single(id),
    //  primary key (id, foreign_key)
    // ) 
    String CREATE_FOREIGN_KEYS_TABLE = "CREATE TABLE " + TABLE_FOREIGN_KEY + "(" + KEY_ID
                                       + " INTEGER NOT NULL REFERENCES " + TABLE_LIST_POI + "(" + KEY_ID + "), "
                                       + KEY_FOREIGN_KEY + " TEXT NOT NULL REFERENCES " + TABLE_SINGLE_POI + "("
                                       + KEY_ID + "), PRIMARY KEY (" + KEY_ID + "," + KEY_FOREIGN_KEY + "))";

    // create table single_poi 
    // (
    //   id integer primary key not null unique,
    //   name text, lat text, lng text, address text
    // )
    String CREATE_POI_SINGLE_TABLE = "CREATE TABLE " + TABLE_SINGLE_POI + "(" + KEY_ID
                                     + " TEXT PRIMARY KEY NOT NULL UNIQUE, " + KEY_NAME + " TEXT, " + KEY_LAT
                                     + " TEXT, " + KEY_LNG + " TEXT, " + KEY_ADDRESS + " TEXT)";

    Log.d( TAG + "XX", CREATE_POI_LIST_TABLE );
    Log.d( TAG + "XX", CREATE_POI_SINGLE_TABLE );
    Log.d( TAG + "XX", CREATE_FOREIGN_KEYS_TABLE );

    db.execSQL( CREATE_POI_LIST_TABLE );
    db.execSQL( CREATE_POI_SINGLE_TABLE );
    db.execSQL( CREATE_FOREIGN_KEYS_TABLE );
  }

  public void insertPoint( SearchResult searchResult )
  {
    String lat = searchResult._lat + "";
    String lng = searchResult._lng + "";
    String name = searchResult._name;
    String address = searchResult.address;
    String id = searchResult.id;

    ContentValues values = new ContentValues();
    values.put( KEY_ID, id );
    values.put( KEY_LAT, lat );
    values.put( KEY_LNG, lng );
    values.put( KEY_NAME, name );
    values.put( KEY_ADDRESS, address );

    SQLiteDatabase db = this.getWritableDatabase();;
    db.insert( TABLE_SINGLE_POI, null, values );
  }

  public long saveList( String name )
  {
    ContentValues values = new ContentValues();
    values.put( KEY_NAME, name );
    SQLiteDatabase db = this.getWritableDatabase();
    return db.insert( TABLE_LIST_POI, null, values );
  }
  
  public void saveForeignKey ( String id, long foreignKey )
  {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put( KEY_ID, id );
    values.put( KEY_FOREIGN_KEY, foreignKey );
    db.insert( TABLE_FOREIGN_KEY, null, values );
  }

  // pretty sure you can use a left join to make this one sql stmt
  public List retrievePoints( String foreignKey )
  {
    String selectQuery = "select "+KEY_ID+" from " + TABLE_FOREIGN_KEY + " where " + KEY_FOREIGN_KEY + " = " + foreignKey;
    SQLiteDatabase db = this.getWritableDatabase();
    
    Cursor cursor = db.rawQuery( selectQuery, null );
    
    List<SearchResult> list = new ArrayList<SearchResult>();
    // looping through all rows and adding to list
    if ( cursor.moveToFirst() )
    {
      int idIndex = cursor.getColumnIndex( DatabaseHelper.KEY_ID );
      do
      {
        String id = cursor.getString( idIndex );
        String selectSingleQuery = "select * from " + TABLE_SINGLE_POI + " where " + TABLE_SINGLE_POI+"."+KEY_ID+"='"+id+"'";
        Cursor cursorSingle = db.rawQuery( selectSingleQuery, null );
        cursorSingle.moveToFirst();
        SearchResult searchResult = new SearchResult();

        int idKey = cursorSingle.getColumnIndex( KEY_ID );
        int nameKey = cursorSingle.getColumnIndex( KEY_NAME );
        int latKey = cursorSingle.getColumnIndex( KEY_LAT );
        int lngKey = cursorSingle.getColumnIndex( KEY_LNG );
        int addressKey = cursorSingle.getColumnIndex( KEY_ADDRESS );
        searchResult.id = cursorSingle.getString(idKey);
        searchResult._name = cursorSingle.getString(nameKey);
        searchResult._lat = cursorSingle.getDouble(latKey);
        searchResult._lng = cursorSingle.getDouble(lngKey);
        searchResult.address = cursorSingle.getString(addressKey);
        list.add( searchResult );
      } while ( cursor.moveToNext() );
    }
    return list;
  }

  // Upgrading database
  @Override
  public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion )
  {
    // Drop older table if existed
    db.execSQL( "DROP TABLE IF EXISTS " + TABLE_LIST_POI );
    db.execSQL( "DROP TABLE IF EXISTS " + TABLE_SINGLE_POI );
    db.execSQL( "DROP TABLE IF EXISTS " + TABLE_FOREIGN_KEY );

    // Create tables again
    onCreate( db );
  }

  /* POI list table */
  // add POI list

  //TODO should have a middle layer abstract this out
  public void addPOIList( String listName, List<SearchResult> poiList )
  {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues listValues = new ContentValues();
    listValues.put( KEY_NAME, listName );

    //it just so happens rowid = primary key, but only if integer primary key autoincrement 
    //http://www.sqlabs.com/blog/2010/12/sqlite-and-unique-rowid-something-you-really-need-to-know/
    long rowid = db.insert( TABLE_LIST_POI, null, listValues );

    for ( SearchResult s : poiList )
    {
      String lat = s._lat + "";
      String lng = s._lng + "";
      String name = s._name;
      String address = s.address;

      ContentValues values = new ContentValues();
      values.put( KEY_LAT, lat );
      values.put( KEY_LNG, lng );
      values.put( KEY_NAME, name );
      values.put( KEY_FOREIGN_KEY, rowid );
      db.insert( TABLE_SINGLE_POI, null, values );
    }
  }

  public Cursor querySinglePOIForeignKey( String foreignKey )
  {
    List<SearchResult> list = new ArrayList<SearchResult>();
    String selectQuery = "SELECT  * FROM " + TABLE_SINGLE_POI + " WHERE " + TABLE_SINGLE_POI + "." + KEY_FOREIGN_KEY
                         + "=" + foreignKey;
    SQLiteDatabase db = this.getWritableDatabase();
    return db.rawQuery( selectQuery, null );
  }

  public Cursor queryAllListPOIs()
  {
    String selectQuery = "SELECT  * FROM " + TABLE_LIST_POI;
    SQLiteDatabase db = this.getWritableDatabase();
    return db.rawQuery( selectQuery, null );
  }
}
