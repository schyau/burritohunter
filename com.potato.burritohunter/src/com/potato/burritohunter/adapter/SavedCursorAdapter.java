package com.potato.burritohunter.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.potato.burritohunter.R;
import com.potato.burritohunter.database.DatabaseHelper;

public class SavedCursorAdapter extends CursorAdapter
{

  public SavedCursorAdapter( Context context, Cursor c, boolean autoRequery )
  {
    super( context, c, autoRequery );
  }

  public View newView( Context context, Cursor cursor, ViewGroup parent )
  {
    View view = LayoutInflater.from( context ).inflate( R.layout.saved_list_item, null );

    TextView txtDesc = (TextView) view.findViewById( R.id.desc );
    TextView txtTitle = (TextView) view.findViewById( R.id.title );
    ImageView iv = (ImageView) view.findViewById( R.id.icon );

    view.setTag( R.id.desc, txtDesc );
    view.setTag( R.id.title, txtTitle );
    view.setTag( R.id.icon, iv );

    return view;
  }

  public void bindView( View view, Context context, Cursor cursor )
  {
    String desc = cursor.getString( cursor.getColumnIndex( DatabaseHelper.KEY_NAME ) );
    String title = cursor.getString( cursor.getColumnIndex( DatabaseHelper.KEY_ADDRESS ) );
    ( (TextView) view.getTag( R.id.desc ) ).setText( desc );
    ( (TextView) view.getTag( R.id.title ) ).setText( title );
    ( (ImageView) view.getTag( R.id.icon ) ).setImageResource( R.drawable.rufknkddngme );
  }
}
