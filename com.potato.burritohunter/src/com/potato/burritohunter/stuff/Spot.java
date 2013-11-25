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
  public static List<Bitmap> spotsBmpHollow;
  public static List<Bitmap> paneList;
  public static List<Bitmap> paneHollow;

  //try oring
  //RED
  public static final int RED_RING = 0xFFFF0100;
  public static final int RED_CENTER_UNSELECTED = 0x48FF0100;
  public static final int RED_CENTER_SELECTED = 0xDAFF0100;

  //YELLOW
  public static final int YELLOW_RING = 0xFFF5A523;
  public static final int YELLOW_CENTER_UNSELECTED = 0x48F5A523;
  public static final int YELLOW_CENTER_SELECTED = 0xDAF5A523;

  //GREEN
  public static final int GREEN_RING = 0xFF7BCD2F;
  public static final int GREEN_CENTER_UNSELECTED = 0x487BCD2F;
  public static final int GREEN_CENTER_SELECTED = 0xDA7BCD2F;

  public static final int CLEAR = 0x00FFFFFF;
  public static final int GREY_RING = 0xFF545454;

  public static final int SELECTED_RING = 0xFF4A494A;

  public static void initSpots()
  {
    spotsBmpList = new ArrayList<Bitmap>();
    spotsBmpList_unselected = new ArrayList<Bitmap>();
    spotsBmpHollow = new ArrayList<Bitmap>();

    spotsBmpList.add( makeSpotBitmap( RED_RING, RED_CENTER_SELECTED ) );
    spotsBmpList.add( makeSpotBitmap( RED_RING, RED_CENTER_UNSELECTED ) );

    spotsBmpList.add( makeSpotBitmap( YELLOW_RING, YELLOW_CENTER_SELECTED ) );
    spotsBmpList.add( makeSpotBitmap( YELLOW_RING, YELLOW_CENTER_UNSELECTED ) );

    spotsBmpList.add( makeSpotBitmap( GREEN_RING, GREEN_CENTER_SELECTED ) );
    spotsBmpList.add( makeSpotBitmap( GREEN_RING, GREEN_CENTER_UNSELECTED ) );

    spotsBmpHollow.add( makeSpotBitmap( RED_RING, CLEAR ) );
    spotsBmpHollow.add( makeSpotBitmap( YELLOW_RING, CLEAR ) );
    spotsBmpHollow.add( makeSpotBitmap( GREEN_RING, CLEAR ) );
    spotsBmpHollow.add( makeSpotBitmap( GREY_RING, CLEAR ) );

    paneList = new ArrayList<Bitmap>();
    paneHollow = new ArrayList<Bitmap>();;
    paneList.add( makeSpotBitmap( SELECTED_RING, RED_CENTER_SELECTED ) );
    paneList.add( makeSpotBitmap( SELECTED_RING, YELLOW_CENTER_SELECTED ) );
    paneList.add( makeSpotBitmap( SELECTED_RING, GREEN_CENTER_SELECTED ) );
    paneHollow.add( makeSpotBitmap( SELECTED_RING, RED_CENTER_UNSELECTED ) );
    paneHollow.add( makeSpotBitmap( SELECTED_RING, YELLOW_CENTER_UNSELECTED ) );
    paneHollow.add( makeSpotBitmap( SELECTED_RING, GREEN_CENTER_UNSELECTED ) );
  }

  public static Bitmap makeSpotBitmap( int ringColor, int center )
  {
    Context context = ADS.getInstance().getContext();
    GradientDrawable shape = (GradientDrawable) context.getResources().getDrawable( R.drawable.something );
    Bitmap bitmap = Bitmap.createBitmap( shape.getIntrinsicWidth() + 3, shape.getIntrinsicHeight() + 3,
                                         Config.ARGB_8888 );
    Canvas canvas = new Canvas( bitmap );
    shape.setBounds( 1, 1, canvas.getWidth() - 1, canvas.getHeight() - 1 );
    shape.setStroke( 3, ringColor );
    shape.setColor( center );
    shape.draw( canvas );

    return bitmap;
  }

  public static Bitmap ratingToBitmap( double rating, boolean selected, boolean isPaneMarker )
  {

    if ( rating < RED_THRESHOLD )
    {
      if ( selected )
      {
        if ( isPaneMarker )
        {
          return paneList.get(0);
        }
        else
        {
          return spotsBmpList.get( 0 );
        }
      }
      else
      {
        if ( isPaneMarker )
        {
          return paneHollow.get( 0 );
        }
        else
        {
          return spotsBmpList.get( 1 );
        }
      }
    }
    else if ( rating < YELLOW_THRESHOLD )
    {
      if ( selected )
      {
        if ( isPaneMarker )
        {
          return paneList.get(1);
        }
        else
        {
          return spotsBmpList.get( 2 );
        }
      }
      else
      {
        if ( isPaneMarker )
        {
          return paneHollow.get( 1 );
        }
        else
        {
          return spotsBmpList.get( 3 );
        }
      }
    }
    if ( selected )
    {
      if ( isPaneMarker )
      {
        return paneList.get( 2 );
      }
      else
      {
        return spotsBmpList.get( 4 );
      }

    }
    else
    {
      if ( isPaneMarker )
      {
        return paneHollow.get( 2 );
      }
      else
      {
        return spotsBmpList.get( 5 );
      }
    }
  }

  public static Bitmap ratingToHollowBitmap( double rating )
  {
    if ( rating < RED_THRESHOLD )
    {
      return spotsBmpHollow.get( 0 );
    }
    else if ( rating < YELLOW_THRESHOLD )
    {
      return spotsBmpHollow.get( 1 );
    }
    return spotsBmpHollow.get( 2 );
  }

  public static Bitmap getGrayCircle()
  {
    return spotsBmpHollow.get( 3 );
  }
  //should use an interface
}
