package com.potato.burritohunter.stuff;

import java.util.ArrayList;
import java.util.List;

import android.R;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.potato.burritohunter.activity.MapActivity;

public class GalleryPoiList
{
  private ImageView imageView;
  private List<String> photoUrls;
  private final Handler _viewFlipperHandler;
  private final Runnable _flipperRunnable;
  private int FLIP_DELAY = 3000;
  public int count;
  private final GalleryPoiList instance;

  public static List<Thread> threads = new ArrayList<Thread>();

  public GalleryPoiList( ImageView imageView, List<String> photoUrls )
  {
    instance = this;
    this.imageView = imageView;
    this.photoUrls = photoUrls;
    _viewFlipperHandler = new Handler();
    // Set up a handler to automatically advance the pages.
    _flipperRunnable = new Runnable()
      {

        @Override
        public void run()
        {
          _viewFlipperHandler.removeCallbacks( this );
          String url = instance.getUrl();
          ImageView iv = instance.getImageView();
          //ImageLoader.getInstance().displayImage( url, iv );
          ImageLoader.getInstance().displayImage( url, iv, SomeUtil.getImageOptions(), new ImageLoadingListener()
          {

            @Override
            public void onLoadingCancelled( String arg0, View arg1 )
            {
              // TODO Auto-generated method stub
              
            }

            @Override
            public void onLoadingComplete( String arg0, View arg1, Bitmap arg2 )
            {
              Animation anim = AnimationUtils.loadAnimation( MapActivity.instance, R. );
              arg1.setAnimation( anim );
              
            }

            @Override
            public void onLoadingFailed( String arg0, View arg1, FailReason arg2 )
            {
              // TODO Auto-generated method stub
              
            }

            @Override
            public void onLoadingStarted( String arg0, View arg1 )
            {
              // TODO Auto-generated method stub
              
            }
            
          });
          _viewFlipperHandler.postDelayed( this, FLIP_DELAY );
        }
      };
  }

  public void setViewsAndUrls( ImageView imageView, List<String> photoUrls )
  {
    this.imageView = imageView;
    this.photoUrls = photoUrls;
  }

  public String getUrl()
  {
    return photoUrls.get( count++ % photoUrls.size() );
  }

  public ImageView getImageView()
  {
    return imageView;
  }
  
  public void start()
  {
    // Start the handler
    _viewFlipperHandler.postDelayed( _flipperRunnable, FLIP_DELAY );
  }

  public void stopFlipping()
  {
    _viewFlipperHandler.removeCallbacks( _flipperRunnable );
  }
}
