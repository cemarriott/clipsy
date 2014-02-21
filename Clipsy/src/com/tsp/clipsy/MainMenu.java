package com.tsp.clipsy;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainMenu extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
		
//		newVid = (Button) findViewById(R.id.bAdd);
//		editVid = (Button) findViewById(R.id.bSub);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
