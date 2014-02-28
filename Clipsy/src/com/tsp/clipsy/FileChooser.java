package com.tsp.clipsy;

import java.io.File;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FileChooser extends Activity {
	
	TextView audioPath;
	TextView audioName;
	
	TextView videoPath;
	TextView videoName;
	
	Button selectAudio;
	Button selectVideo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_chooser);
		
		videoName = (TextView) findViewById(R.id.fc_videoName);
		videoPath = (TextView) findViewById(R.id.fc_videoPath);
		selectVideo = (Button) findViewById(R.id.fc_selectVideo);
		
		audioName = (TextView) findViewById(R.id.fc_audioName);
		audioPath = (TextView) findViewById(R.id.fc_audioPath);
		selectAudio = (Button) findViewById(R.id.fc_selectAudio);
		
		
		 selectVideo.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	
	            	Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
	            	intent.setType("video/*");
	            	intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
	            	startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1);
	            	
	            }
	      });
		 
		 
		 selectAudio.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	
	            	Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
	            	intent.setType("audio/*");
	            	intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
	            	startActivityForResult(Intent.createChooser(intent, "Complete action using"), 2);
	            	
	            }
	      });
		
		
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == 1) {
			  if(resultCode == RESULT_OK){
				  String path = data.getData().getPath();
				  String name = (new File(path)).getName();
				  
				  videoPath.setText(path);
				  videoName.setText(name);
			  }
			}
			
			if (requestCode == 2) {
				  if(resultCode == RESULT_OK){
					  String path = data.getData().getPath();
					  String name = (new File(path)).getName();
					  
					  audioPath.setText(path);
					  audioName.setText(name);
				  }
			}
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.file_chooser, menu);
		return true;
	}

}
