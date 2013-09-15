package com.potato.burritohunter.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;

import com.potato.burritohunter.R;

public class OpenSourceLicenseDialog extends Activity
{

  @Override
  public void onCreate( Bundle b )
  {
    super.onCreate( b );

    Dialog dialog = new Dialog( this );
    dialog.setContentView( R.layout.about_textview );
    dialog.setCancelable( true );
    dialog.setTitle( "opensourcelicensedialog" );
    dialog.setCanceledOnTouchOutside( true );
    TextView text = (TextView) dialog.findViewById( R.id.tv );

    text.setText( "open source licenses" );

    dialog.show();

    dialog.setOnCancelListener( new DialogInterface.OnCancelListener()
      {
        public void onCancel( DialogInterface arg0 )
        {
          finish();
        }
      } );
  }
}
