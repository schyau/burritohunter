package com.potato.burritohunter.fragment;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.potato.burritohunter.R;
import com.potato.burritohunter.activity.MapActivity;
import com.potato.burritohunter.adapter.SinglePOIListAdapter;
import com.potato.burritohunter.database.DatabaseHelper;
import com.potato.burritohunter.database.DatabaseUtil;
import com.potato.burritohunter.foursquare.explore.FoursquareDetailSearch;
import com.potato.burritohunter.foursquare.explore.FoursquareExploreService;
import com.potato.burritohunter.foursquare.explore.Item;
import com.potato.burritohunter.foursquare.search.Venue;
import com.potato.burritohunter.stuff.SearchResult;
import com.potato.burritohunter.stuff.SomeUtil;

public class SinglePOIListFragment extends SherlockListFragment
{
  private List<SearchResult> singlePOIs;
  public static long staticForeignKey;
  public static SinglePOIListAdapter  adapter;
  
  public void setSinglePOIs( List<SearchResult> singlePOIs, long foreignKey )
  {
    this.singlePOIs = singlePOIs;
    staticForeignKey = foreignKey;
  }

  @Override
  public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
  {

    /*
     * ArrayAdapter<String> adapter = new ArrayAdapter<String>( inflater.getContext(),
     * android.R.layout.simple_list_item_1, countries );
     */
    setAdapter();
    View vw = inflater.inflate( R.layout.single_list_fragment_layout, container, false );
    vw.findViewById( R.id.view_in_map ).setOnClickListener( new OnClickListener()
      {
        @Override
        public void onClick( View v )
        {
          MapActivity.instance.viewInMapAction();
        }
      } );
    return vw;
    //return super.onCreateView( inflater, container, savedInstanceState );

    //use this to make a new adapter
    //http://theopentutorials.com/tutorials/android/listview/android-custom-listview-with-image-and-text-using-baseadapter/
  }

  //remove from here and try to set somewhere else
  public void setAdapter()
  {
    if ( adapter == null )
    {
      adapter = new SinglePOIListAdapter( this, singlePOIs );
      setListAdapter( adapter );
      }
    else
    {
      adapter.rowItems = singlePOIs;
    }
    
    
    adapter.notifyDataSetChanged();
  }

  @Override
  public void onListItemClick( final ListView parent, View view, int position, long id )
  {
    /* end of shitty code */
    final SearchResult searchResult = (SearchResult) parent.getItemAtPosition( position );
    final SinglePOIListFragment frag = this;
    if (shouldUpdateSearchResult( searchResult))
    {
      // TODO pop up menu asking user 
      (new AsyncTask<Void,Void,Void>()
      {
        List<SearchResult> list;
        @Override
        protected Void doInBackground( Void... params )
        {
          // 
          FoursquareDetailSearch fsqdetailsearch = FoursquareExploreService.searchDetail( searchResult.id );
          Item response = fsqdetailsearch.getResponse();
          Venue venue = response.getVenue();
          SearchResult sr = MapActivity.convertVenueToSearchResult( venue );
          DatabaseUtil.getDatabaseHelper().insertPointInSameThread (sr);
          list = DatabaseUtil.getDatabaseHelper().retrievePoints( staticForeignKey + "" );
          
          Log.d("asdf", venue.getName());
          return null;
        }
        @Override
        protected void onPostExecute(Void nothing )
        {
          frag.setSinglePOIs( list, staticForeignKey );
          frag.setAdapter();
          adapter.notifyDataSetChanged();
        }
        
      }).execute();
      
    } // we can use an updating flag, but I'm ok with multiple updates and notifying adapter of changes.
    else
    {
      String srId = searchResult.id;
      SomeUtil.launchFourSquareDetail( MapActivity.instance, srId );
    }
  }

  public static boolean shouldUpdateSearchResult( SearchResult sr )
  {
    return ( System.currentTimeMillis() - Long.parseLong( sr.time ) ) > TIME_THRESHOLD;

  }

  public static final long TIME_THRESHOLD = 10000;

  @Override
  public void onActivityCreated( Bundle b )
  {
    super.onActivityCreated( b );
    final Context _ctx = getActivity();
    final SinglePOIListFragment frag = this;
    getListView().setOnItemLongClickListener( new OnItemLongClickListener()
      {
        @Override
        public boolean onItemLongClick( AdapterView<?> arg0, final View arg1, int arg2, long arg3 )
        {
          final String title = ( (TextView) arg1.findViewById( R.id.title ) ).getText().toString();
          new AlertDialog.Builder( _ctx ).setMessage( "Are you sure you want to delete " + title + "?" )
              .setPositiveButton( "Yes!", new DialogInterface.OnClickListener()
                {
                  public void onClick( DialogInterface dialog, int whichButton )
                  {
                    (new AsyncTask<Void,Void,Void>()
                    {
                      List<SearchResult> searchResults;
                      @Override
                      protected Void doInBackground( Void... params )
                      {
                        String id = ( (SinglePOIListAdapter.ViewHolder) arg1.getTag() ).id;
                        DatabaseHelper dbHelper = DatabaseUtil.getDatabaseHelper();
                        dbHelper.deleteSingle( id, staticForeignKey ); //id and foreignkey is flipped.
                        searchResults = dbHelper.retrievePoints( staticForeignKey + "" );
                        return null;
                      }
                      
                      @Override
                      protected void onPostExecute( Void param )
                      {
                        frag.setSinglePOIs( searchResults, staticForeignKey );
                        frag.setAdapter();
                        POIListFragment.listAdapter.notifyDataSetChanged();
                        Toast.makeText( _ctx, title + " deleted!", Toast.LENGTH_SHORT ).show();
                      }
                    }).execute();
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
}
