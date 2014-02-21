package com.tsp.clipsy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Splash extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		Thread timer = new Thread(){
			public void run(){
				try{
					sleep(3000);
				} 
				catch (InterruptedException e){
					e.printStackTrace();
				}
				finally{
					Intent openMainActivity = new Intent(Splash.this, MainMenu.class);
					startActivity(openMainActivity);
					
				}
			}
		};
		timer.start();
	}

	
}
