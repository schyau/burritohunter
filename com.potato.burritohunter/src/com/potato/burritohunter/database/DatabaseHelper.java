package com.potato.burritohunter.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
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
  public static final String KEY_ID = "_id";
  public static final String KEY_FOREIGN_KEY = "foreign_key";
  public static final String KEY_NAME = "name";
  public static final String KEY_LAT = "latitude";
  public static final String KEY_LNG = "longitude";
  public static final String KEY_ADDRESS = "address";
  public static final String KEY_ICON = "icon";
  public static final String KEY_TIME = "time";
  public static final String KEY_RATING = "rating";

  public DatabaseHelper( Context context )
  {
    super( context, DATABASE_NAME, null, DATABASE_VERSION );
  }

  // Creating Tables
  @Override
  public void onCreate( SQLiteDatabase db )
  {
    // CREATE TABLE poi (id INTEGER PRIMARY KEY UNIQUE AUTOINCREMENT, name TEXT );
    String CREATE_POI_LIST_TABLE = "CREATE TABLE " + TABLE_LIST_POI + "(" + KEY_ID
                                   + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_NAME + " TEXT" + ")";

    // create table foreign_key_table
    // (
    //  id integer not null references poi_list(id), 
    //  foreign_key text not null references poi_single(id),
    //  primary key (id, foreign_key)
    // ) 
    String CREATE_FOREIGN_KEYS_TABLE = "CREATE TABLE " + TABLE_FOREIGN_KEY + "(" + KEY_ID
                                       + " INTEGER NOT NULL REFERENCES " + TABLE_LIST_POI + "(" + KEY_ID + "), "
                                       + KEY_FOREIGN_KEY + " TEXT NOT NULL REFERENCES " + TABLE_SINGLE_POI + "("
                                       + KEY_ID + "), PRIMARY KEY (" + KEY_ID + "," + KEY_FOREIGN_KEY + "))";

    // create table single_poi 
    // (
    //   id text primary key not null unique,
    //   name text, lat text, lng text, address text, icon text, rating text
    // )
    String CREATE_POI_SINGLE_TABLE = "CREATE TABLE " + TABLE_SINGLE_POI + "(" + KEY_ID
                                     + " TEXT PRIMARY KEY NOT NULL UNIQUE, " + KEY_NAME + " TEXT, " + KEY_LAT
                                     + " TEXT, " + KEY_LNG + " TEXT, " + KEY_ADDRESS + " TEXT, " + KEY_ICON + " TEXT, "
                                     + KEY_TIME + " TEXT, " + KEY_RATING + " TEXT )";

    Log.d( TAG, CREATE_POI_LIST_TABLE );
    Log.d( TAG, CREATE_POI_SINGLE_TABLE );
    Log.d( TAG + "XX", CREATE_FOREIGN_KEYS_TABLE );

    db.execSQL( CREATE_POI_LIST_TABLE );
    db.execSQL( CREATE_POI_SINGLE_TABLE );
    db.execSQL( CREATE_FOREIGN_KEYS_TABLE );
  }

  public synchronized void insertPointInSameThread( final SearchResult searchResult )
  {
    insertPointWorker( searchResult, this );
  }

  private static synchronized void insertPointWorker( final SearchResult searchResult, final DatabaseHelper instance )
  {
    String lat = searchResult._lat + "";
    String lng = searchResult._lng + "";
    String name = searchResult._name;
    String address = searchResult.address;
    String id = searchResult.id;
    String photoIcon = searchResult.photoIcon;
    String time = searchResult.time;
    String rating = searchResult.rating + "";

    ContentValues values = new ContentValues();
    values.put( KEY_ID, id );
    values.put( KEY_LAT, lat );
    values.put( KEY_LNG, lng );
    values.put( KEY_NAME, name );
    values.put( KEY_ADDRESS, address );
    values.put( KEY_ICON, photoIcon );
    values.put( KEY_TIME, time );
    values.put( KEY_RATING, rating );

    SQLiteDatabase db = instance.getWritableDatabase();;
    try
    {
      db.insertOrThrow( TABLE_SINGLE_POI, null, values );
      System.out.println( "time: " + time + " was just added for " + id );
    }
    catch ( SQLException e )
    {
      try
      {
        db.replace( TABLE_SINGLE_POI, null, values );
        System.out.println( "time: " + time + " was replaced added for " + id );
      }
      catch ( SQLException replaceE )
      {
        Log.d( "DatabaseHelper", "couldn't insert or update point!" );
        if ( replaceE != null )
        {
          replaceE.printStackTrace();
        }
      }
    }

  }

  public synchronized void insertPoint( final SearchResult searchResult )
  {
    final DatabaseHelper instance = this;
    new Thread( new Runnable()
      {
        @Override
        public void run()
        {
          insertPointWorker( searchResult, instance );
        }

      } ).start();

  }

  public synchronized long saveList( String name )
  {
    ContentValues values = new ContentValues();
    values.put( KEY_NAME, name );
    SQLiteDatabase db = this.getWritableDatabase();
    return db.insert( TABLE_LIST_POI, null, values );
  }

  // should be called after saveList
  public synchronized void saveForeignKey( String id, long foreignKey )
  {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put( KEY_ID, id );
    values.put( KEY_FOREIGN_KEY, foreignKey );
    db.insert( TABLE_FOREIGN_KEY, null, values );
  }

  // pretty sure you can use a left join to make this one sql stmt
  // get all points related to the list foreign key
  public synchronized List<SearchResult> retrievePoints( String foreignKey )
  {
    String selectQuery = "select " + KEY_ID + " from " + TABLE_FOREIGN_KEY + " where " + KEY_FOREIGN_KEY + " = "
                         + foreignKey;
    SQLiteDatabase db = this.getReadableDatabase();

    Cursor cursor = db.rawQuery( selectQuery, null );

    List<SearchResult> list = new ArrayList<SearchResult>();
    // looping through all rows and adding to list
    if ( cursor.moveToFirst() )
    {
      int idIndex = cursor.getColumnIndex( DatabaseHelper.KEY_ID );
      do
      {
        String id = cursor.getString( idIndex );
        Cursor cursorSingle = retrieveSinglePoint( id );
        SearchResult searchResult = getSearchResult( cursorSingle );

        list.add( searchResult );
      } while ( cursor.moveToNext() );
    }
    return list;
  }

  //provide a cursor with a single item and we'll grab its column info
  public synchronized SearchResult getSearchResult( Cursor cursorSingle )
  {
    cursorSingle.moveToFirst();
    SearchResult searchResult = new SearchResult();

    int idKey = cursorSingle.getColumnIndex( KEY_ID );
    int nameKey = cursorSingle.getColumnIndex( KEY_NAME );
    int latKey = cursorSingle.getColumnIndex( KEY_LAT );
    int lngKey = cursorSingle.getColumnIndex( KEY_LNG );
    int addressKey = cursorSingle.getColumnIndex( KEY_ADDRESS );
    int photoIconKey = cursorSingle.getColumnIndex( KEY_ICON );
    int timeKey = cursorSingle.getColumnIndex( KEY_TIME );
    int ratingKey = cursorSingle.getColumnIndex( KEY_RATING );
    searchResult.id = cursorSingle.getString( idKey );
    searchResult._name = cursorSingle.getString( nameKey );
    searchResult._lat = cursorSingle.getDouble( latKey );
    searchResult._lng = cursorSingle.getDouble( lngKey );
    searchResult.address = cursorSingle.getString( addressKey );
    searchResult.photoIcon = cursorSingle.getString( photoIconKey );
    searchResult.time = cursorSingle.getString( timeKey );
    searchResult.rating = Double.parseDouble( cursorSingle.getString( ratingKey ) );
    return searchResult;
  }

  public synchronized Cursor retrieveSinglePoint( String id )
  {
    SQLiteDatabase db = this.getReadableDatabase();
    String selectSingleQuery = "select * from " + TABLE_SINGLE_POI + " where " + TABLE_SINGLE_POI + "." + KEY_ID + "='"
                               + id + "'";

    return db.rawQuery( selectSingleQuery, null );
  }

  public synchronized void writeTimeToId( String id )
  {
    long time = System.currentTimeMillis();
    //update poi_single set time=<time> where id=<id>
    String updateQuery = "UPDATE " + TABLE_SINGLE_POI + " SET " + KEY_TIME + "=" + time + " WHERE " + KEY_ID + "=" + id;
    SQLiteDatabase db = this.getWritableDatabase();
    db.execSQL( updateQuery );
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
  public synchronized void addPOIList( String listName, List<SearchResult> poiList )
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

  public synchronized Cursor queryAllListPOIs()
  {
    String selectQuery = "SELECT  * FROM " + TABLE_LIST_POI;
    SQLiteDatabase db = this.getWritableDatabase();
    return db.rawQuery( selectQuery, null );
  }

  public synchronized void deleteListRow( long id )
  {
    SQLiteDatabase db = this.getWritableDatabase();
    db.delete( TABLE_LIST_POI, KEY_ID + "=" + id, null );
    while ( db.delete( TABLE_FOREIGN_KEY, KEY_ID + "=" + id, null ) > 0 )
    {
    }
  }

  public synchronized void deleteSingle( String id, long foreignKey )
  {
    SQLiteDatabase db = this.getWritableDatabase();
    String WHERE_CLAUSE = KEY_ID + "='" + id + "'" + " AND " + KEY_FOREIGN_KEY + "=" + foreignKey; //oops swapped the foreignkey vs id logic
    boolean b = db.delete( TABLE_FOREIGN_KEY, WHERE_CLAUSE, null ) > 0;
  }

}
