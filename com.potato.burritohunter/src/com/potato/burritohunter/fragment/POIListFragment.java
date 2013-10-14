package com.potato.burritohunter.fragment;

import java.lang.ref.WeakReference;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.potato.burritohunter.R;
import com.potato.burritohunter.activity.MapActivity;
import com.potato.burritohunter.adapter.SavedListAdapter;
import com.potato.burritohunter.database.DatabaseUtil;
import com.potato.burritohunter.database.SavedListItem;
import com.potato.burritohunter.stuff.SearchResult;

// funfax: every fragment must have a default ctor so don't override that
// TODO http://marakana.com/s/post/1250/android_fragments_tutorial

// use this to make a new adapter
// http://theopentutorials.com/tutorials/android/listview/android-custom-listview-with-image-and-text-using-baseadapter/
public class POIListFragment extends SherlockListFragment
{
  static SinglePOIListFragment single = new SinglePOIListFragment();
  public static SavedListAdapter listAdapter;

  @Override
  public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
  {
    //Cursor c = DatabaseUtil.getDatabaseHelper().queryAllListPOIs();
    //SavedCursorAdapter adapter = new SavedCursorAdapter(getActivity().getApplicationContext(), c, true);
    List<SavedListItem> list = DatabaseUtil.getSavedList();
    listAdapter = new SavedListAdapter( this, list );
    setListAdapter( listAdapter );
    View vw = super.onCreateView( inflater, container, savedInstanceState );
    //vw.setBackgroundColor( Color.WHITE );
    return vw;
  }

  @Override
  public void onActivityCreated( Bundle b )
  {
    super.onActivityCreated( b );
    final Context _ctx = getActivity();
    getListView().setOnItemLongClickListener( new OnItemLongClickListener()
      {
        @Override
        public boolean onItemLongClick( AdapterView<?> arg0, final View arg1, int arg2, long arg3 )
        {
          final String title = ( (TextView) arg1.findViewById( R.id.title ) ).getText().toString();
          new AlertDialog.Builder( _ctx ).setMessage( "Are you sure you want to delete " + title + "?" )
              .setPositiveButton( "Continue", new DialogInterface.OnClickListener()
                {
                  public void onClick( DialogInterface dialog, int whichButton )
                  {
                    long id = ( (SavedListAdapter.ViewHolder) arg1.getTag() ).id;
                    DatabaseUtil.getDatabaseHelper().deleteListRow( id );
                    List<SavedListItem> list = DatabaseUtil.getSavedList();
                    SavedListAdapter adapter = new SavedListAdapter( MapActivity._listFragment, list );
                    MapActivity._listFragment.setListAdapter( adapter );
                    Toast.makeText( _ctx, title + " deleted!", Toast.LENGTH_SHORT ).show();
                  }

                } ).setNegativeButton( "Cancel", new DialogInterface.OnClickListener()
                {
                  public void onClick( DialogInterface dialog, int whichButton )
                  {
                    /* User clicked cancel so do some stuff */
                  }
                } ).create().show();
          return false;
        }

      } );
  }

  @Override
  public void onListItemClick( ListView parent, View view, int position, long id )
  {
    SavedListItem savedListItem = (SavedListItem) parent.getItemAtPosition( position );
    long foreignKey = savedListItem._id;
    List<SearchResult> list = DatabaseUtil.getDatabaseHelper().retrievePoints( foreignKey + "" );

    single.setSinglePOIs( list, foreignKey );
    single.setAdapter();

    // will have to expand on this when we get to the fourth one
    if ( MapActivity.viewPagerAdapter.getCount() == 4 )
    {
      MapActivity.viewPagerAdapter.removeView( MapActivity.viewPager, 3 );
    }
    if ( MapActivity.viewPagerAdapter.getCount() == 3 )
    {
      MapActivity.viewPagerAdapter.replaceView( MapActivity.viewPager, 2, single );
    }
    else
    {
      MapActivity.viewPagerAdapter.addFragment( single );
    }
    MapActivity.viewPagerAdapter.notifyDataSetChanged();
    MapActivity.viewPager.setCurrentItem( 2 );

  }

  public void onPause()
  {
    super.onPause();

  }

  public void onDestroy()
  {
    //listAdapter.whenFragmentOnStop();
    super.onDestroy();
  }
}
