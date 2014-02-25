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

public class FileChooser extends Activity {
	
	private static final int PICK_RESULT_CODE = 1;
	
	TextView fileName;
	TextView filePath;
	Button selectFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_chooser);
		
		fileName = (TextView) findViewById(R.id.fc_fileName);
		selectFile = (Button) findViewById(R.id.selectFile);
		filePath = (TextView) findViewById(R.id.fc_filePath);
		
		 selectFile.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	
	            	Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
	            	intent.setType("video/*");
	            	intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
	            	startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_RESULT_CODE);
	            	
	            }
	      });
		
		
	}
	
	
	@Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		  switch (requestCode) {
		  case PICK_RESULT_CODE:
			  if(resultCode == RESULT_OK){
				  String path = data.getData().getPath();
				  String videoName = (new File(path)).getName();
				  
				  
				  filePath.setText(path);
				  fileName.setText(videoName);
			  }
			  break; 
		  }
	 }
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.file_chooser, menu);
		return true;
	}

}
