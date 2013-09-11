package com.potato.burritohunter.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.potato.burritohunter.R;
import com.potato.burritohunter.stuff.BurritoClickListeners.MapOnMarkerClickListener;

//this class should contain the map logic now...
public class MyOtherMapFragment extends SherlockFragment
{
  private LatLng PIVOT = new LatLng( 37.798052, -122.406278 );
  private SupportMapFragment mMapFragment;
  private GoogleMap map;

  // solution shamelessly stolen from
  // http://stackoverflow.com/questions/17476089/android-google-maps-fragment-and-viewpager-error-inflating-class-fragment
  @Override
  public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
  {
    View vw = inflater.inflate( R.layout.my_other_map_fragment, container, false );
    mMapFragment = ( (SupportMapFragment) getFragmentManager().findFragmentById( R.id.map_frag ) );
    map = mMapFragment.getMap();
    initMap( map );
    return vw;
  }

  public GoogleMap getMap()
  {
    return map;
  }

  private void initMap(GoogleMap map)
  {
      UiSettings settings = map.getUiSettings();
      settings.setAllGesturesEnabled(true);
      settings.setMyLocationButtonEnabled(true);

      map.moveCamera(CameraUpdateFactory.newLatLngZoom(PIVOT, 16));
      map.addMarker(new MarkerOptions().position(PIVOT).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));
      map.setOnMarkerClickListener( new MapOnMarkerClickListener() );
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
