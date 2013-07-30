package com.potato.burritohunter.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.potato.burritohunter.R;
import com.potato.burritohunter.places.PlacesSearchResult;
import com.potato.burritohunter.stuff.PlacesRequestAsyncTask;
import com.potato.burritohunter.stuff.SomeUtil;
import com.potato.burritohunter.yelp.YelpSearchResult;
import com.squareup.otto.Subscribe;

public class MapActivity extends FragmentActivity {
	private GoogleMap map;

	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	private LatLng PLACE = new LatLng(37.871744, -122.260963);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// new YelpRequestAsyncTask(37.871744, -122.260963, "burritos",
		// SomeUtil.getBus()).execute();
		new PlacesRequestAsyncTask(37.871744, -122.260963, "burritos",
				SomeUtil.getBus()).execute();
		map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		/*
		 * Marker hamburg = map.addMarker(new MarkerOptions().position(HAMBURG)
		 * .title("Hamburg"));
		 */
		Marker kiel = map.addMarker(new MarkerOptions()
				.position(PLACE)
				.title("Kiel")
				.snippet("Kiel is cool")
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.ic_launcher)));

		// Move the camera instantly to hamburg with a zoom of 15.
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(PLACE, 15));

		// Zoom in, animating the camera.
		map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onPause() {
		super.onPause();
		SomeUtil.getBus().unregister(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		SomeUtil.getBus().register(this);
	}

	@Subscribe
	public void subscriberWithASillyName(YelpSearchResult y) {
		Log.d("luls", y.toString());

	}

	@Subscribe
	public void subscriberWithASillyName(PlacesSearchResult y) {
		Log.d("luls", y.getStatus());
	}

	// Define a DialogFragment that displays the error dialog
	public static class ErrorDialogFragment extends DialogFragment {
		// Global field to contain the error dialog
		private Dialog mDialog;

		// Default constructor. Sets the dialog field to null
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}

		// Set the dialog to display
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}

		// Return a Dialog to the DialogFragment.
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}
}