package com.potato.burritohunter.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.potato.burritohunter.R;

public class AboutDialog extends SherlockFragmentActivity
{

  @Override
  public void onCreate( Bundle b )
  {
    super.onCreate( b );
    getSupportActionBar().hide();
    Dialog dialog = new Dialog( this );
    dialog.setContentView( R.layout.about_textview );
    dialog.setCancelable( true );
    dialog.setCanceledOnTouchOutside( true );
    dialog.setTitle( "about dialog" );
    TextView text = (TextView) dialog.findViewById( R.id.tv );
    //text.setText(  );

    dialog.show();
    //
    dialog.setOnCancelListener( new DialogInterface.OnCancelListener()
      {

        public void onCancel( DialogInterface arg0 )
        {
          finish();
        }

      } );
  }
}/*
  * public class ShowDialogActivity extends Activity {
  * 
  * @Override protected void onCreate(Bundle savedInstanceState) { // TODO Auto-generated method stub
  * super.onCreate(savedInstanceState);
  * 
  * // //Log.d("DEBUG", "showing dialog!");
  * 
  * Dialog dialog = new Dialog(this); dialog.setContentView(R.layout.select_dialog_singlechoice);
  * dialog.setTitle("Your Widget Name"); dialog.setCancelable(true); dialog.setCanceledOnTouchOutside(true); TextView
  * text = (TextView) dialog.findViewById(R.id.text1); text.setText("Message");
  * 
  * dialog.show(); // dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
  * 
  * public void onCancel(DialogInterface arg0) { finish(); }
  * 
  * }); }
  * 
  * }
  */
