package com.potato.burritohunter.stuff;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.potato.burritohunter.R;
import com.potato.burritohunter.activity.MapActivity;
import com.potato.burritohunter.adapter.SavedListAdapter;

public class GalleryPoiList
{
  private List<String> photoUrls;
  private final Handler _viewFlipperHandler;
  private final Runnable _flipperRunnable;
  private int FLIP_DELAY = 3000;
  public int count;
  private final GalleryPoiList instance;
  private final SavedListAdapter.ViewHolder holder;
  public static boolean kontinue = false;

  private static final Random r = new Random();
  private boolean isLeft = false;

  public static List<Thread> threads = new ArrayList<Thread>();

  private int lastIndex = -1;

  public GalleryPoiList( SavedListAdapter.ViewHolder holder, List<String> photoUrls )
  {
    instance = this;
    this.holder = holder;
    this.photoUrls = photoUrls;
    _viewFlipperHandler = new Handler();
    _flipperRunnable = new Runnable()
      {

        @Override
        public void run()
        {
          _viewFlipperHandler.removeCallbacks( this );

          ImageView iv = instance.getImageView();
          String url = instance.getUrl();
          //ImageLoader.getInstance().displayImage( url, iv );

          ImageLoader.getInstance().displayImage( url, iv, SomeUtil.getImageOptions(), new ImageLoadingListener()
            {

              @Override
              public void onLoadingCancelled( String arg0, View arg1 )
              {
              }

              @Override
              public void onLoadingComplete( String arg0, View arg1, Bitmap arg2 )
              {
              }

              @Override
              public void onLoadingFailed( String arg0, View arg1, FailReason arg2 )
              {
              }

              @Override
              public void onLoadingStarted( String arg0, View arg1 )
              {
              }
            } );

          Log.d( "asdf", "hey mum.  mum. mum. mum. hey mum. mum.  mum." );
          if ( instance.photoUrls != null && instance.photoUrls.size() != 1 && kontinue )
            _viewFlipperHandler.postDelayed( instance._flipperRunnable, getDelay() );
        }
      };

  }

  public void setViewsAndUrls( List<String> photoUrls )
  {
    this.photoUrls = photoUrls;
  }

  public String getUrl()
  {
    if ( photoUrls == null || photoUrls.size() == 0 )
    {
      return "";
    }
    if ( photoUrls.size() == 1)
    {
      return photoUrls.get(0);
    }
    int next = r.nextInt( photoUrls.size() );
    if ( lastIndex == next )
    {
      next = ( next + 1 ) % photoUrls.size();
    }
    lastIndex = next;
    return photoUrls.get( next );
  }

  public ImageView getImageView()
  {
    ImageView iv = holder.imageView;
    ImageView iv1 = holder.imageView1;
    ViewFlipper viewFlipper = holder.viewFlipper;
    
    //http://developer.android.com/training/animation/cardflip.html
    viewFlipper.setInAnimation( MapActivity.instance, com.potato.burritohunter.R.anim.in_from_left );
    viewFlipper.setOutAnimation( MapActivity.instance, com.potato.burritohunter.R.anim.out_to_left );
    ImageView retval = null;
    if ( isLeft )
    {
      retval = iv1;
      viewFlipper.showNext();
    }
    else
    {
      retval = iv;
      viewFlipper.showPrevious();
    }
    isLeft = !isLeft;
    return retval;
  }

  public void start()
  {
    ImageView iv = holder.imageView;
    ImageView iv1 = holder.imageView1;
    String url = getUrl();
    if ( "".equals( url ) )// checks if photoUrls size == 0
    {
      if( iv != null)
      {
        iv.setImageResource( R.drawable.rufknkddngme );
      }
      if ( iv1 !=null)
      {
        iv1.setImageResource( R.drawable.rufknkddngme );
      }
    }
    else
    {
      if ( isLeft)
      {
        ImageLoader.getInstance().displayImage( url, iv1, SomeUtil.getImageOptions() );
        
      }else
      {
        ImageLoader.getInstance().displayImage( url, iv, SomeUtil.getImageOptions() );
      }
      if ( photoUrls.size() > 1 )
      {
        
        String url1 = getUrl();
        if( isLeft)
        {
          ImageLoader.getInstance().displayImage( url1, iv, SomeUtil.getImageOptions() );
          
        }
        else
        {
          ImageLoader.getInstance().displayImage( url1, iv1, SomeUtil.getImageOptions() );
        }
        _viewFlipperHandler.postDelayed( _flipperRunnable, getDelay() );
      }
    }
  }

  public static int getDelay()
  {
    return ( r.nextInt( 20 ) + 5 ) * 1000;
  }

  public void stopFlippingNaoJUSTSTHAPPP()
  {
    _viewFlipperHandler.removeCallbacks( _flipperRunnable );
    kontinue = false;
  }
}
