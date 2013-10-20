package com.potato.burritohunter.stuff;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.potato.burritohunter.R;
import com.squareup.otto.Bus;

public final class SomeUtil
{
  private static final Bus BUS = new Bus(); // down boy, don't be so eager.
  private static final DisplayImageOptions options = new DisplayImageOptions.Builder().cacheOnDisc( true ).build();

  public static Bus getBus()
  {
    return BUS;
  }

  public static void hideSoftKeyboard( Activity activity, View vw )
  {
    if ( activity != null && vw != null )
    {
      InputMethodManager imm = (InputMethodManager) activity.getSystemService( Context.INPUT_METHOD_SERVICE );
      imm.hideSoftInputFromWindow( vw.getWindowToken(), 0 );
    }
  }

  public static void showSoftKeyboard( Activity activity, View vw )
  {
    if ( activity != null && vw != null )
    {
      InputMethodManager imm = (InputMethodManager) activity.getSystemService( Context.INPUT_METHOD_SERVICE );
      imm.showSoftInput( vw, 0 );
    }
  }


  public static DisplayImageOptions getImageOptions()
  {
    return options;
  }

  private SomeUtil()
  {
  }
  
  public static void startLoadingRotate(View view)
  {
    //should put a null check here before launching into the wild
    if (view == null )
      return;
    Animation a = AnimationUtils.loadAnimation(view.getContext(), R.anim.rotate);

    // Aply animation to image view
    view.startAnimation(a);
    view.setVisibility( View.VISIBLE );
  }
  public static void stopLoadingRotate(View view)
  {
    if (view == null)
      return;

    view.clearAnimation();
    view.setVisibility( View.GONE );
    
  }
  public static void launchFourSquareDetail( Activity activity, String id )
  {

    String url = "https://foursquare.com/v/"+id;
    Intent i = new Intent(Intent.ACTION_VIEW);
    i.setData(Uri.parse(url));
    activity.startActivity(i);
  }
}
