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

  // 'POI' and 'POI list' tables
  public static final String TABLE_LIST_POI = "poi_list";
  public static final String TABLE_SINGLE_POI = "poi_single";

  // Table 'poi_list' and 'poi_single' names
  public static final String KEY_ID = "id";
  public static final String KEY_FOREIGN_KEY = "foreign_key";
  public static final String KEY_NAME = "name";
  public static final String KEY_LAT = "latitude";
  public static final String KEY_LNG = "longitude";

  public DatabaseHelper( Context context )
  {
    super( context, DATABASE_NAME, null, DATABASE_VERSION );
  }

  // Creating Tables
  @Override
  public void onCreate( SQLiteDatabase db )
  {
    String CREATE_POI_LIST_TABLE = "CREATE TABLE " + TABLE_LIST_POI + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME
                                   + " TEXT" + ")";

    // CREATE TABLE poi (id INTEGER PRIMARY KEY, name TEXT );

    String CREATE_POI_SINGLE_TABLE = "CREATE TABLE " + TABLE_SINGLE_POI + "(" + KEY_ID
                                     + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_FOREIGN_KEY + " INTEGER, "
                                     + KEY_NAME + " TEXT, " + KEY_LAT + " TEXT, " + KEY_LNG + " TEXT, "
                                     + "FOREIGN KEY(" + KEY_FOREIGN_KEY + ") REFERENCES " + TABLE_LIST_POI + "("
                                     + KEY_ID + "))";
    Log.d( TAG, CREATE_POI_LIST_TABLE );
    Log.d( TAG, CREATE_POI_SINGLE_TABLE );

    db.execSQL( CREATE_POI_LIST_TABLE );
    db.execSQL( CREATE_POI_SINGLE_TABLE );
  }

  // Upgrading database
  @Override
  public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion )
  {
    // Drop older table if existed
    db.execSQL( "DROP TABLE IF EXISTS " + TABLE_LIST_POI );
    db.execSQL( "DROP TABLE IF EXISTS " + TABLE_SINGLE_POI );

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
      LatLng latLng = s._latlng;
      String lat = latLng.latitude + "";
      String lng = latLng.longitude + "";
      String name = s._name;

      ContentValues values = new ContentValues();
      values.put( KEY_LAT, lat );
      values.put( KEY_LNG, lng );
      values.put( KEY_NAME, name );
      values.put( KEY_FOREIGN_KEY, rowid );
      db.insert( TABLE_SINGLE_POI, null, values );
    }
  }

  public Cursor querySinglePOIForeignKey ( String foreignKey )
  {
    List<SearchResult> list = new ArrayList<SearchResult>();
    String selectQuery = "SELECT  * FROM " + TABLE_SINGLE_POI + " WHERE " + TABLE_SINGLE_POI + "." + KEY_FOREIGN_KEY
                         + "=" + foreignKey;
    SQLiteDatabase db = this.getWritableDatabase();
    return db.rawQuery( selectQuery, null );
    
  }

  public Cursor queryAllListPOIs ()
  {
    String selectQuery = "SELECT  * FROM " + TABLE_LIST_POI;
    SQLiteDatabase db = this.getWritableDatabase();
    return db.rawQuery( selectQuery, null );
  }
  
  // return contact return markers; }
  /*
   * //add marker long addMarkerzzz( MarkerzzzObj obj ) { SQLiteDatabase db = this.getWritableDatabase(); ContentValues
   * values = new ContentValues(); values.put( KEY_NAME, obj._name ); // Contact Name db.insert( TABLE_MARKERZZZ, null,
   * values ); SQLiteDatabase dbRead = this.getReadableDatabase(); String query = "SELECT ROWID from " + TABLE_MARKERZZZ
   * + " order by ROWID DESC limit 1"; Cursor c = dbRead.rawQuery( query, null ); if ( c != null && c.moveToFirst() ) {
   * return c.getLong( 0 ); //The 0 is the column index, we only have 1 column, so the index is 0 } return -1; }
   * 
   * void addMarker( MarkerObj obj ) { SQLiteDatabase db = this.getWritableDatabase(); ContentValues values = new
   * ContentValues(); values.put( KEY_NAME, obj._name ); values.put( KEY_LAT, obj._lat ); values.put( KEY_LNG, obj._lng
   * ); values.put( KEY_FOREIGN_KEY, obj._foreignKey ); db.insert( TABLE_MARKER, null, values ); }
   * 
   * MarkerzzzObj getMarkerzzz( int id ) { SQLiteDatabase db = this.getReadableDatabase(); Cursor cursor = db.query(
   * TABLE_MARKERZZZ, new String[] { KEY_ID, KEY_NAME }, KEY_ID + "=?", new String[] { String.valueOf( id ) }, null,
   * null, null, null ); if ( cursor != null ) cursor.moveToFirst();
   * 
   * MarkerzzzObj markers = new MarkerzzzObj(); markers._id = Integer.parseInt( cursor.getString( 0 ) ); markers._name =
   * cursor.getString( 1 );
   * 
   * // return contact return markers; }
   * 
   * // Getting single contact /* Contact getContact(int id) { SQLiteDatabase db = this.getReadableDatabase();
   * 
   * Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID, KEY_NAME, KEY_PH_NO }, KEY_ID + "=?", new String[]
   * { String.valueOf(id) }, null, null, null, null); if (cursor != null) cursor.moveToFirst();
   * 
   * Contact contact = new Contact(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2)); //
   * return contact return contact; }
   * 
   * 
   * public List<MarkerObj> getAllMarkerObjWithForeignKey( String foreignKey ) { List<MarkerObj> list = new
   * ArrayList<MarkerObj>(); String selectQuery = "SELECT  * FROM " + TABLE_MARKER + " WHERE " + TABLE_MARKER + "." +
   * KEY_FOREIGN_KEY + "=" + foreignKey; SQLiteDatabase db = this.getWritableDatabase(); Cursor cursor = db.rawQuery(
   * selectQuery, null );
   * 
   * // looping through all rows and adding to list if ( cursor.moveToFirst() ) { do { MarkerObj marker = new
   * MarkerObj(); marker._id = Integer.parseInt( cursor.getString( 0 ) ); marker._name = cursor.getString( 2 );
   * marker._lat = cursor.getString( 3 ); marker._lng = cursor.getString( 4 ); marker._foreignKey = Integer.parseInt(
   * cursor.getString( 1 ) ); list.add( marker ); } while ( cursor.moveToNext() ); }
   * 
   * // return contact list return list; }
   * 
   * // Getting All Contacts public List<MarkerzzzObj> getAllMarkerzzzObj() { List<MarkerzzzObj> contactList = new
   * ArrayList<MarkerzzzObj>(); // Select All Query String selectQuery = "SELECT  * FROM " + TABLE_MARKERZZZ;
   * 
   * SQLiteDatabase db = this.getWritableDatabase(); Cursor cursor = db.rawQuery( selectQuery, null );
   * 
   * // looping through all rows and adding to list if ( cursor.moveToFirst() ) { do { MarkerzzzObj contact = new
   * MarkerzzzObj(); contact._id = Integer.parseInt( cursor.getString( 0 ) ); contact._name = cursor.getString( 1 );
   * contactList.add( contact ); } while ( cursor.moveToNext() ); }
   * 
   * // return contact list return contactList; }
   */

  // Updating single contact
  /*
   * public int updateContact( Contact contact ) { SQLiteDatabase db = this.getWritableDatabase();
   * 
   * ContentValues values = new ContentValues(); values.put( KEY_NAME, contact.getName() ); values.put( KEY_PH_NO,
   * contact.getPhoneNumber() );
   * 
   * // updating row return db.update( TABLE_CONTACTS, values, KEY_ID + " = ?", new String[] { String.valueOf(
   * contact.getID() ) } ); }
   * 
   * // Deleting single contact public void deleteContact( Contact contact ) { SQLiteDatabase db =
   * this.getWritableDatabase(); db.delete( TABLE_CONTACTS, KEY_ID + " = ?", new String[] { String.valueOf(
   * contact.getID() ) } ); db.close(); }
   * 
   * // Getting contacts Count public int getContactsCount() { String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
   * SQLiteDatabase db = this.getReadableDatabase(); Cursor cursor = db.rawQuery( countQuery, null ); cursor.close();
   * 
   * // return count return cursor.getCount(); }
   */

}
