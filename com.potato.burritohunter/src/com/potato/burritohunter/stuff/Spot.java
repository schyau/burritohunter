package com.potato.burritohunter.stuff;

import java.util.ArrayList;
import java.util.List;

import com.potato.burritohunter.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

public class Spot
{
  public static final int RED_THRESHOLD = 7;
  public static final int YELLOW_THRESHOLD = 8;

  public static Bitmap redBmp;
  public static Bitmap greenBmp;
  public static Bitmap yellowBmp;
  public static List<Bitmap> spotsBmpList;
  public static List<Bitmap> spotsBmpList_unselected;

  //try oring
  //RED
  public static final int RED_RING = 0xFFFF0000;
  public static final int RED_CENTER_UNSELECTED = 0x77FF0000;
  public static final int RED_CENTER_SELECTED = 0xFFFF0000;

  //YELLOW
  public static final int YELLOW_RING = 0xFFFFFF00;
  public static final int YELLOW_CENTER_UNSELECTED = 0x77FFFF00;
  public static final int YELLOW_CENTER_SELECTED = 0xFFFFFF00;

  //GREEN
  public static final int GREEN_RING = 0xFF00FF00;
  public static final int GREEN_CENTER_UNSELECTED = 0x7700FF00;
  public static final int GREEN_CENTER_SELECTED = 0xFF00FF00;

  public static void initSpots()
  {
    spotsBmpList = new ArrayList<Bitmap>();
    spotsBmpList_unselected = new ArrayList<Bitmap>();
    Context context = ADS.getInstance().getContext();
    GradientDrawable shape = (GradientDrawable) context.getResources().getDrawable( R.drawable.something );
    spotsBmpList.add( makeSpotBitmap( shape, RED_RING, RED_CENTER_SELECTED ) );
    spotsBmpList.add( makeSpotBitmap( shape, RED_RING, RED_CENTER_UNSELECTED ) );
    
    spotsBmpList.add( makeSpotBitmap( shape, YELLOW_RING, YELLOW_CENTER_SELECTED ) );
    spotsBmpList.add( makeSpotBitmap( shape, YELLOW_RING, YELLOW_CENTER_UNSELECTED ) );

    spotsBmpList.add( makeSpotBitmap( shape, GREEN_RING, GREEN_CENTER_SELECTED ) );
    spotsBmpList.add( makeSpotBitmap( shape, GREEN_RING, GREEN_CENTER_UNSELECTED ) );
    

  }

  private static Bitmap makeSpotBitmap( GradientDrawable shape, int ringColor, int center)
  {
    Bitmap bitmap = Bitmap.createBitmap( shape.getIntrinsicWidth()+3, shape.getIntrinsicHeight()+3, Config.ARGB_8888 );
    Canvas canvas = new Canvas( bitmap );
    shape.setBounds( 1, 1, canvas.getWidth()-1, canvas.getHeight()-1 );
    shape.setStroke( 3, ringColor );
    shape.setColor( center );
    shape.draw( canvas );

    return bitmap;
  }

  public static Bitmap ratingToBitmap( double rating, boolean selected )
  {

    if( rating < RED_THRESHOLD )
    {
      if( selected )
      {
        return spotsBmpList.get( 0 );
      }
      else
      {
        return spotsBmpList.get( 1 );
      }
    }
    else if( rating < YELLOW_THRESHOLD )
    {
      if( selected )
      {
        return spotsBmpList.get( 2 );
      }
      else
      {
        return spotsBmpList.get( 3 );
      }
    }
    if( selected)
    {
      return spotsBmpList.get(4);
    }
    else
    {
      return spotsBmpList.get(5);
    }
  }

}
