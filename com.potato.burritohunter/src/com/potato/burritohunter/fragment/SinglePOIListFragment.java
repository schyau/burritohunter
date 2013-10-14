package com.potato.burritohunter.fragment;

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
import com.potato.burritohunter.adapter.SinglePOIListAdapter;
import com.potato.burritohunter.database.DatabaseHelper;
import com.potato.burritohunter.database.DatabaseUtil;
import com.potato.burritohunter.stuff.SearchResult;
import com.potato.burritohunter.stuff.SomeUtil;

public class SinglePOIListFragment extends SherlockListFragment
{
  private List<SearchResult> singlePOIs;
  public static long staticForeignKey;

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
    return vw;
    //return super.onCreateView( inflater, container, savedInstanceState );

    //use this to make a new adapter
    //http://theopentutorials.com/tutorials/android/listview/android-custom-listview-with-image-and-text-using-baseadapter/
  }

  //remove from here and try to set somewhere else
  public void setAdapter()
  {
    SinglePOIListAdapter adapter = new SinglePOIListAdapter( this, singlePOIs );
    setListAdapter( adapter );
  }

  @Override
  public void onListItemClick( ListView parent, View view, int position, long id )
  {
    /* start of shitty code */
    if ( position == 0 )
    {// plz delete this when you can figure out where to put viewin map.  kthx
      MapActivity.instance.viewInMapAction();
      return;
    }
    /* end of shitty code */
    SearchResult searchResult = (SearchResult) parent.getItemAtPosition( position );
    String srId = searchResult.id;
    SomeUtil.launchFourSquareDetail( MapActivity.instance, srId );
  }

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
                    String id = ( (SinglePOIListAdapter.ViewHolder) arg1.getTag() ).id;
                    DatabaseHelper dbHelper = DatabaseUtil.getDatabaseHelper();
                    dbHelper.deleteSingle( id, staticForeignKey ); //id and foreignkey is flipped.
                    List<SearchResult> searchResults = dbHelper.retrievePoints( staticForeignKey + "" );
                    frag.setSinglePOIs( searchResults, staticForeignKey );
                    frag.setAdapter();

                    POIListFragment.listAdapter.notifyDataSetChanged();

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
}
