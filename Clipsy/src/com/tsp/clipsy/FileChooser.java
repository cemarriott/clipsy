package com.tsp.clipsy;

import java.io.File;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FileChooser extends Activity {
	
	String selectedAudioPath;
	String selectedVideoPath;
	
	TextView audioPath;
	TextView audioName;
	
	TextView videoPath;
	TextView videoName;
	
	Button selectAudio;
	Button selectVideo;
	Button fcContinue;

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
		
		fcContinue = (Button) findViewById(R.id.fc_continue);
		
		
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
		 
		 
		 fcContinue.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	
	            	if (selectedAudioPath != null) {
	            		if (selectedVideoPath != null) {
	            			
	            			Intent openViewer = new Intent(FileChooser.this, Viewer.class);
	            			Bundle view = new Bundle();
	            			view.putString("audio", selectedAudioPath);
	            			view.putString("video", selectedVideoPath);
	            			openViewer.putExtras(view);
	    					startActivity(openViewer);
	            			
	            		} else {
	            			Toast.makeText(getApplicationContext(), "No video selected.", Toast.LENGTH_SHORT).show();
	            		}
	            		
	            	} else {
	            		Toast.makeText(getApplicationContext(), "No audio selected.", Toast.LENGTH_SHORT).show();
	            	}
	            	
	            }
	      });
		
		
	}
	
	
	public String getRealPathFromURI(Uri input, int type) {

		if (type == 1) {
			
	        String [] proj={MediaStore.Video.Media.DATA};
	        Cursor cursor = getContentResolver().query( input,
	                        proj, // Which columns to return
	                        null,       // WHERE clause; which rows to return (all rows)
	                        null,       // WHERE clause selection arguments (none)
	                        null); // Order-by clause (ascending by name)
	        
	        if (cursor != null) {
		        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
		        cursor.moveToFirst();
		
		        return cursor.getString(column_index);
	        }
	        
	        return input.getPath();
	       
		} else {
			
			String [] proj={MediaStore.Audio.Media.DATA};
	        Cursor cursor = getContentResolver().query( input,
	                        proj, // Which columns to return
	                        null,       // WHERE clause; which rows to return (all rows)
	                        null,       // WHERE clause selection arguments (none)
	                        null); // Order-by clause (ascending by name)
	        
	        if (cursor != null) {
		        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
		        cursor.moveToFirst();
		
		        return cursor.getString(column_index);
	        }
	        
	        return input.getPath();
	       
		}
}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == 1) {
			  if(resultCode == RESULT_OK){
				  Uri path = data.getData();
				  
				  selectedVideoPath = getRealPathFromURI(path, 1);
				  
				  String name = (new File(selectedVideoPath)).getName();
				  
				  videoPath.setText(selectedVideoPath);
				  videoName.setText(name);
			  }
			}
			
			if (requestCode == 2) {
				  if(resultCode == RESULT_OK){
					  Uri path = data.getData();
					  
					  selectedAudioPath = getRealPathFromURI(path, 2);
					  
					  String name = (new File(selectedAudioPath)).getName();
					  
					  audioPath.setText(selectedAudioPath);
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
