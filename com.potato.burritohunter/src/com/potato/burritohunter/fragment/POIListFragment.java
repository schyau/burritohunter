package com.potato.burritohunter.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import com.potato.burritohunter.foursquare.explore.FoursquareDetailSearch;
import com.potato.burritohunter.foursquare.explore.FoursquareExploreService;
import com.potato.burritohunter.foursquare.search.Venue;
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
    final POIListFragment frag = this;
    ( new AsyncTask<Void, Void, Void>()
      {
        List<SavedListItem> list;

        @Override
        protected Void doInBackground( Void... params )
        {
          list = DatabaseUtil.getSavedList();
          return null;
        }

        @Override
        protected void onPostExecute( Void param )
        {
          listAdapter = new SavedListAdapter( frag, list );
          setListAdapter( listAdapter );
        }
      } ).execute();

    View vw = inflater.inflate( R.layout.poi_list_fragment_layout, container, false );
    //medalsList.setDividerHeight(1);
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
        public boolean onItemLongClick( AdapterView<?> arg0, final View arg1, final int position, long arg3 )
        {
          final String title = ( (TextView) arg1.findViewById( R.id.title ) ).getText().toString();

          //Log.d("asdf", position+", and "+arg3);
          new AlertDialog.Builder( _ctx ).setMessage( "Are you sure you want to delete " + title + "?" )
              .setPositiveButton( "Continue", new DialogInterface.OnClickListener()
                {
                  public void onClick( DialogInterface dialog, int whichButton )
                  {
                    new Thread( new Runnable()
                      {
                        @Override
                        public void run()
                        {
                          long id = ( (SavedListAdapter.ViewHolder) arg1.getTag() ).id;
                          DatabaseUtil.getDatabaseHelper().deleteListRow( id );
                        }

                      } ).start();
                    //List<SavedListItem> list = DatabaseUtil.getSavedList();
                    //SavedListAdapter adapter = new SavedListAdapter( MapActivity._listFragment, list );
                    //MapActivity._listFragment.setListAdapter( adapter );
                    listAdapter.removeItem( position );
                    listAdapter.notifyDataSetChanged();

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

    if ( MapActivity.viewPagerAdapter.getCount() != 3 )
    {
      single.setSinglePOIs( new ArrayList<SearchResult>(), -1 );
      MapActivity.viewPagerAdapter.addFragment( single );
      MapActivity.viewPagerAdapter.notifyDataSetChanged();
    }
    MapActivity.viewPager.setCurrentItem( 2, true );
    SavedListItem savedListItem = (SavedListItem) parent.getItemAtPosition( position );
    final long foreignKey = savedListItem._id;

    ( new AsyncTask<Void, Void, Void>()
      {
        List<SearchResult> list;

        @Override
        protected Void doInBackground( Void... params )
        {
          list = DatabaseUtil.getDatabaseHelper().retrievePoints( foreignKey + "" );

          return null;
        }

        @Override
        protected void onPostExecute( Void param )
        {

          single.setSinglePOIs( list, foreignKey );
          single.setAdapter();
          final List<SearchResult> outOfDateSearchResults = new ArrayList<SearchResult>();
          for ( SearchResult sr : list )
          {
            if ( SinglePOIListFragment.shouldUpdateSearchResult( sr ) )
            {
              outOfDateSearchResults.add( sr );
            }
          }

          if ( outOfDateSearchResults.size() > 0 )
          {

            //Log.d("asdf", position+", and "+arg3);
            new AlertDialog.Builder( MapActivity.instance )
                .setMessage( "You need to update some of the items in this list before being able to view them.  Would you like to do that now?" )
                .setPositiveButton( "Yes", new DialogInterface.OnClickListener()
                  {
                    public void onClick( DialogInterface dialog, int whichButton )
                    {
                      for ( final SearchResult sr : outOfDateSearchResults )
                      {
                        Log.d("asdf","asdf");
                        ( new AsyncTask<Void, Void, Void>()
                          {
                            @Override
                            public Void doInBackground( Void... params )
                            {// do it serially, easier to code

                              FoursquareDetailSearch fsqds = FoursquareExploreService.searchDetail( sr.id );
                              if ( fsqds.getResponse() == null || fsqds.getResponse().getVenue() == null )
                              {
                                return null;
                              }
                              Venue venue = fsqds.getResponse().getVenue();
                              SearchResult newSearchResult = MapActivity.convertVenueToSearchResult( venue );
                              DatabaseUtil.getDatabaseHelper().insertPointInSameThread( newSearchResult );

                              list = DatabaseUtil.getDatabaseHelper().retrievePoints( foreignKey + "" );
                              return null;
                            }

                            @Override
                            public void onPostExecute( Void something )
                            {
                              for ( SearchResult sr : list )
                              {
                                Log.d( "asdf", sr._name );
                              }
                              single.setSinglePOIs( list, foreignKey );
                              single.setAdapter(); // TODO missing last one

                            }
                          } ).execute();
                      }
                    }

                  } ).setNegativeButton( "Cancel", new DialogInterface.OnClickListener()
                  {
                    public void onClick( DialogInterface dialog, int whichButton )
                    {
                      /* User clicked cancel so do some stuff */
                    }
                  } ).create().show();
          }
          else
          {
          }
        }
      } ).execute();

  }

  public void onPause()
  {
    super.onPause();

  }

  public void onDestroy()
  {
    super.onDestroy();
  }
}
