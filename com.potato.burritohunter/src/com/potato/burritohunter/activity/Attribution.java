package com.potato.burritohunter.activity;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListActivity;
import com.potato.burritohunter.R;

public class Attribution extends SherlockListActivity
{
  @Override
  public void onCreate( Bundle b )
  {
    super.onCreate( b );
    getSupportActionBar().hide();
    setContentView( R.layout.attribution_layout );
    setListAdapter( new AttributionAdapter( this ) );
  }

  public class AttributionAdapter extends BaseAdapter
  {
    private String[] titles;
    private String[] descriptions;

    public AttributionAdapter( Context context )
    {
      Resources res = context.getResources();
      titles = res.getStringArray( R.array.attribution_titles );
      descriptions = res.getStringArray( R.array.attribution_descriptions );
    }

    @Override
    public int getCount()
    {
      return titles.length;
    }

    @Override
    public Object getItem( int position )
    {
      return titles[ position ];
    }

    @Override
    public long getItemId( int position )
    {
      return position;
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent )
    {
      class ViewHolder
      {
        TextView title;
        TextView description;
      }
      ViewHolder holder = null;

      LayoutInflater mInflater = (LayoutInflater) getSystemService( Activity.LAYOUT_INFLATER_SERVICE );
      if ( convertView == null )
      {
        holder = new ViewHolder();
        convertView = mInflater.inflate( R.layout.attribution_row, null );
        holder.title = (TextView) convertView.findViewById( R.id.AttributionTitle );
        holder.description = (TextView) convertView.findViewById( R.id.AttributionDesc );
        convertView.setTag( holder );
      }

      holder = (ViewHolder) convertView.getTag();
      holder.title.setText( titles[ position ] );
      holder.description.setText( descriptions[ position ] );

      return convertView;
    }
  }
}
