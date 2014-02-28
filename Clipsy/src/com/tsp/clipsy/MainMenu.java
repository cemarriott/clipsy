package com.tsp.clipsy;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainMenu extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main_menu);
		
		final Button bnewVid = (Button) findViewById(R.id.bNewVid);
        bnewVid.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
                
				Intent chooseFile = new Intent(v.getContext(), com.tsp.clipsy.FileChooser.class);
				startActivity(chooseFile);
				
				
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
