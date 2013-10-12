package com.potato.burritohunter.stuff;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
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

  private boolean isLeft = false;

  public static List<Thread> threads = new ArrayList<Thread>();

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
          if ( iv != null ) // this means that image wasn't finished loading (though try not to use null)
          {
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
          }
          Log.d("asdf", "hey mum.  mum. mum. mum. hey mum. mum.  mum.");
          if(kontinue)
            _viewFlipperHandler.postDelayed( instance._flipperRunnable, FLIP_DELAY );
        }
      };

  }

  public void setViewsAndUrls( List<String> photoUrls )
  {
    this.photoUrls = photoUrls;
  }

  public String getUrl()
  {
    return photoUrls.get( count++ % photoUrls.size() );
  }

  public ImageView getImageView()
  {
    ImageView iv = holder.imageView;
    ImageView iv1 = holder.imageView1;
    ViewFlipper viewFlipper = holder.viewFlipper;
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
    String url = getUrl();
    ImageLoader.getInstance().displayImage( url, iv, SomeUtil.getImageOptions() );

    ahemStartFlippingPlz();
  }

  public void ahemStartFlippingPlz()
  {
    _viewFlipperHandler.postDelayed( _flipperRunnable, FLIP_DELAY );
  }
  
  public void stopFlippingNaoJUSTSTHAPPP()
  {
    _viewFlipperHandler.removeCallbacks( _flipperRunnable );
    kontinue = false;
  }
}
