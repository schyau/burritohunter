package com.potato.burritohunter.stuff;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
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

    // Aply animation to image view
    view.startAnimation(ADS.an);
    view.setVisibility( View.VISIBLE );
  }
  public static void stopLoadingRotate(View view)
  {
    if (view == null)
      return;

    view.clearAnimation();
    view.setVisibility( View.GONE );
    
  }
}
