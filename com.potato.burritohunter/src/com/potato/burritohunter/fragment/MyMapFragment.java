package com.potato.burritohunter.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.potato.burritohunter.R;
import com.potato.burritohunter.stuff.BurritoClickListeners.MapOnMarkerClickListener;
import com.potato.burritohunter.stuff.MyLocationHelper;

public class MyMapFragment extends SupportMapFragment
{
    private LatLng  latLon;

    public MyMapFragment()
    {
        super();
    }

    public static MyMapFragment newInstance(LatLng position)
    {
        MyMapFragment frag = new MyMapFragment();
        frag.latLon = position;
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        initMap();
        return v;
    }

    private void initMap()
    {
        UiSettings settings = getMap().getUiSettings();
        settings.setAllGesturesEnabled(true);
        settings.setMyLocationButtonEnabled(true);

        getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(latLon, 16));
        getMap().addMarker(new MarkerOptions().position(latLon).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));
        getMap().setOnMarkerClickListener( new MapOnMarkerClickListener() );
    }
}