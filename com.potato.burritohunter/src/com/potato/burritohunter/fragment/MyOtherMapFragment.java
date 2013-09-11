package com.potato.burritohunter.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.potato.burritohunter.R;

public class MyOtherMapFragment extends SherlockFragment
{
  private SupportMapFragment mMapFragment;
  private GoogleMap map;

  @Override
  public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
  {
    View vw = inflater.inflate( R.layout.my_other_map_fragment, container, false );
    mMapFragment = ( (SupportMapFragment) getFragmentManager().findFragmentById( R.id.map_frag ) );
    map = getMap();
    return vw;
  }

  public GoogleMap getMap()
  {
    return mMapFragment.getMap();
  }

  public void onDestroyView() {
    super.onDestroyView();

    try {
        Fragment fragment = (getFragmentManager().findFragmentById(R.id.map_frag));  
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.remove(fragment);
        ft.commit();
    } catch (Exception e) {
        e.printStackTrace();
    }
  }
}
