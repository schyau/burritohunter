package com.potato.burritohunter.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.potato.burritohunter.R;
import com.potato.burritohunter.activity.MapActivity;
import com.potato.burritohunter.stuff.BottomPagerPanel;
import com.potato.burritohunter.stuff.BurritoClickListeners;

public class BottomPagerButtonsPanel extends SherlockFragment
{
  private static TextView _numSelectedTextView;
  private static ImageButton _panelLeftArrowButton;
  private static ImageButton _clearAllButton;
  private static ImageButton _findMeButton;
  private static ImageButton _saveButton;
  private static ViewPager _viewPager;

  @SuppressLint( "NewApi" )
  public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
  {
    View vw = inflater.inflate( R.layout.bottom_pager_button_layout, container, false );
    //_clearAllButton = (Button) vw.findViewById( R.id. )
    this._numSelectedTextView = (TextView) vw.findViewById( R.id.num_selected );
    this._panelLeftArrowButton = (ImageButton) vw.findViewById( R.id.panelLeftArrow );
    this._clearAllButton = (ImageButton) vw.findViewById( R.id.clear_all );
    this._findMeButton = (ImageButton) vw.findViewById( R.id.find_me );
    this._saveButton = (ImageButton) vw.findViewById( R.id.save );
    
    _saveButton.setOnClickListener( new BurritoClickListeners.Save( getActivity() ) );
    _clearAllButton.setOnClickListener( new BurritoClickListeners.ClearUnsaved() );
    _clearAllButton.setOnLongClickListener( new BurritoClickListeners.ClearAll() );
    //FindMeOnClickListener
    _findMeButton.setOnClickListener( new BurritoClickListeners.FindMeOnClickListener( MapActivity.instance,
                                                                                       MapActivity._mapFragment ) );
    setNumSelectedTextView( MapActivity.selectedSearchResults.size() + "");
    _panelLeftArrowButton.setOnClickListener( new OnClickListener()
      {

        @Override
        public void onClick( View v )
        {
          _viewPager.setCurrentItem( BottomPagerPanel.PANEL_MARKER, true );
        }

      } );
    
    //chyauchyau: set pane here
    return vw;
  }

  public void setNumSelectedTextView( String text )
  {
    _numSelectedTextView.setText( text );
  }

  public void displayBottomPagerNavigation( boolean b )
  {
    if ( _panelLeftArrowButton != null )
    {
      _panelLeftArrowButton.setVisibility( b ? View.VISIBLE : View.GONE );
    }
  }

  public void setViewPager( ViewPager viewPager )
  {
    this._viewPager = viewPager;
  }
}
