package com.tsp.clipsy;

import java.io.File;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
		fcContinue = (Button) findViewById(R.id.fc_continue);
		
		
		// Select Video button
		 selectVideo.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	
	            	// Older versions of Android
	            	if (Build.VERSION.SDK_INT <19) {
	            	
	            		// Request that the user pick a video file
		            	Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		            	intent.setType("video/*");
		            	intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
		            	startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1);
	            	
	            	} else {
	            		
	            		// Kitkat-specific
	            		Intent intent = new Intent(Intent.ACTION_PICK);
	            		intent.setType("video/*");
		            	intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
	            		startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1);
	            		
	            	}
	            	
	            }
	      });
		 
		 
		 
		 /*    Not used here anymore, but may still be useful later.
		 // Select Audio button
		 selectAudio.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	
	            	// Older versions of Android
	            	if (Build.VERSION.SDK_INT < 19) {
	            	
	            		// Request that user picks an audio file
		            	Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		            	intent.setType("audio/*");
		            	intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
		            	startActivityForResult(Intent.createChooser(intent, "Complete action using"), 2);
	            	
	            	} else {
	            		
	            		// Kitkat-specific
	            		Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
	            		startActivityForResult(Intent.createChooser(intent, "Complete action using"), 2);
	            		
	            	}
	            	
	            }
	      });
	      */
		 
		 
		 // Continue button
		 fcContinue.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	
            		if (selectedVideoPath != null) {
            			
            			Intent openPreview = new Intent(FileChooser.this, PreviewActivity.class);
            			Bundle view = new Bundle();
            			view.putString("video", selectedVideoPath);
            			openPreview.putExtras(view);
    					startActivity(openPreview);
            			
            		} else {
            			Toast.makeText(getApplicationContext(), "No video selected.", Toast.LENGTH_SHORT).show();
            		}
            		
            	}
	            	
	      });
		
		
	}
	
	
	/*
	 * Built-in apps like Gallery return a content URI that isn't suitable for 
	 * opening files. So we have to take the URI and find the absolute file
	 * path using MediaStore.
	 */
	public String getRealPathFromURI(Uri input) {
			
		String [] proj={MediaStore.Video.Media.DATA};
        Cursor cursor = getContentResolver().query( input,
                        proj, // Which columns to return
                        null,       // WHERE clause; which rows to return (all rows)
                        null,       // WHERE clause selection arguments (none)
                        null); // Order-by clause (ascending by name)
        
        
        // If the cursor is null, then we got the path from a 3rd-party app
        // (not Gallery) so it's already an absolute path, so it isn't 
        // found in MediaStore and the cursor is null.
        if (cursor != null) {
	        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
	        cursor.moveToFirst();
	
	        return cursor.getString(column_index);
        }
        
        return input.getPath();
	       
	} 
	
	
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(resultCode == RESULT_OK){
			Uri path = data.getData();
			  
			selectedVideoPath = getRealPathFromURI(path);
			  
			String name = (new File(selectedVideoPath)).getName();
			  
			videoPath.setText(selectedVideoPath);
			videoName.setText(name);
			
			MediaMetadataRetriever retrieve = new MediaMetadataRetriever();
			retrieve.setDataSource(selectedVideoPath);
			
			long durationMs = Long.parseLong(retrieve.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
			
			Bitmap thumb = retrieve.getFrameAtTime(durationMs/2, 1);
			
			int screenWidth;
			Display display = getWindowManager().getDefaultDisplay();
			
			if (android.os.Build.VERSION.SDK_INT >= 13) {
				Point size = new Point();
				display.getSize(size);
				screenWidth = size.x;
			} else {
				screenWidth = display.getWidth();
			}
			
			int newHeight = (screenWidth * thumb.getHeight()/thumb.getWidth());
			Bitmap newThumb = Bitmap.createScaledBitmap(thumb, screenWidth, newHeight, true);
			
			ImageView thumbView = (ImageView) findViewById(R.id.thumbView);
			thumbView.setImageBitmap(newThumb);
			thumbView.setAdjustViewBounds(false);
			thumbView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			
			
	    } else {
	    	Toast.makeText(getApplicationContext(), "An error occured. Please try again.",
	    			Toast.LENGTH_LONG).show();
	    }
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.file_chooser, menu);
		return true;
	}

}
