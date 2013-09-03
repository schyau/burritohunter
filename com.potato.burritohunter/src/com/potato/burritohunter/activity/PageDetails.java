package com.potato.burritohunter.activity;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.potato.burritohunter.database.DatabaseUtil;
import com.potato.burritohunter.database.SavedListItem;
import com.potato.burritohunter.fragment.POIListFragment;
import com.potato.burritohunter.fragment.SinglePOIListFragment;
import com.potato.burritohunter.stuff.SearchResult;

public class PageDetails
{
  @Override
  public void onCreate(Bundle b)
  {
    super.onCreate(b);
    SavedListItem savedListItem = poiList.get( position );
    String foreignKey = savedListItem._id+"";
    SinglePOIListFragment singlePOIListFragment = new SinglePOIListFragment();
    //List<SearchResult> singlePOIs = DatabaseUtil.getSingleSearchResults( foreignKey );
    List<SearchResult> singlePOIs = DatabaseUtil.getDatabaseHelper().retrievePoints( foreignKey );
    singlePOIListFragment.setSinglePOIs(singlePOIs);
    FragmentManager fm = getSupportFragmentManager();
    FragmentTransaction ft = fm.beginTransaction();
    
        FrameLayout frame = new FrameLayout(this);
        frame.setId(CONTENT_VIEW_ID);
        setContentView(frame, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));


            Fragment newFragment = new DebugExampleTwoFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(CONTENT_VIEW_ID, newFragment).commit();

    }
  }
}
