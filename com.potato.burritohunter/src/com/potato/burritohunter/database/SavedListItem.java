package com.potato.burritohunter.database;
public class SavedListItem
{
  public String _title;
  public int _id;

  public SavedListItem( int id, String title )
  {
    _title = title;
    _id = id;
  }

  public String get_title()
  {
    return _title;
  }

  public void set_title( String _title )
  {
    this._title = _title;
  }

  public int get_id()
  {
    return _id;
  }

  public void set_id( int _id )
  {
    this._id = _id;
  }
}