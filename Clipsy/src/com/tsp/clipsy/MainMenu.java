package com.tsp.clipsy;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

public class MainMenu extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main_menu);
		
		final Button bnewVid = (Button) findViewById(R.id.bNewVid);
        bnewVid.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
                
				//Initialising alert dialog
                AlertDialog myAlertDialog = new AlertDialog.Builder(MainMenu.this).create();
                //Adding the Title
                myAlertDialog.setTitle("New Project");
                //Adding Message
                myAlertDialog.setMessage("Text thing here");
                //adding an image
                myAlertDialog.setIcon(R.drawable.ic_launcher);
                //setting up OK button
                myAlertDialog.setButton("OK", new DialogInterface.OnClickListener() {
 
                    public void onClick(DialogInterface dialog, int which) {
                        //toast message hown after successful execution of the alert dialog
                        Toast.makeText(getApplicationContext(), "Making Project", Toast.LENGTH_SHORT).show();
                        
                        // TESTING THE FILE CHOOSER
                        
                        Intent openFileChooser = new Intent(MainMenu.this, FileChooser.class);
    					startActivity(openFileChooser);
 
                    }
                });
 
                //displaying the Alert message
                myAlertDialog.show();
 
           
            	
            }
        });
        
        final Button beditVid = (Button) findViewById(R.id.bEditVid);
        beditVid.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                
            	System.out.println("edit button clicked");
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	


}
