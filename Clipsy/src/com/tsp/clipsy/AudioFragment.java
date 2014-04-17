package com.tsp.clipsy;

import java.io.File;
import java.io.IOException;

import com.tsp.clipsy.audio.RingdroidSelectActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class AudioFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

	private MediaPlayer mPlayer;
	private SeekBar mSeek;
	private Handler mHandler = new Handler();
	private int songLength;
	private final int SEEK_MAX = 1000;
	private boolean mWasGetContentIntent;

	private String selectedAudioPath;

	private TextView audioPath;
	private TextView audioName;

	private Button loadRingdroid;
	private Button selectAudio;
	private Button playPause;

	private RelativeLayout mRelativeLayout;

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedState) {

		mRelativeLayout = (RelativeLayout) inflater.inflate(R.layout.activity_audio_fragment, container, false); 

		audioPath = (TextView) mRelativeLayout.findViewById(R.id.audioPath);

		loadRingdroid = (Button) mRelativeLayout.findViewById(R.id.loadRingdroid);
		selectAudio = (Button) mRelativeLayout.findViewById(R.id.loadAudio);
		playPause = (Button) mRelativeLayout.findViewById(R.id.playPause);

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

		//Load Ringdroid
		loadRingdroid.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				/*
				Intent openAudio = new Intent(getActivity(), RingdroidSelectActivity.class);
				openAudio.setAction("android.intent.action.GET_CONTENT");
				startActivity(openAudio);
				*/
				
		        String filename = selectedAudioPath;

	            Intent intent = new Intent(Intent.ACTION_EDIT, Uri.parse(filename));
	            intent.putExtra("was_get_content_intent", mWasGetContentIntent);
	            intent.setClassName("com.tsp.clipsy", "com.tsp.clipsy.audio.RingdroidEditActivity");
	            startActivityForResult(intent, 1);

			}
		});
		
		// Play and pause
		playPause.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				playPause();
			}
		});


		// Audio portion
		mSeek = (SeekBar) mRelativeLayout.findViewById(R.id.seekBar);


		return mRelativeLayout;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if(resultCode == Activity.RESULT_OK){
			Uri path = data.getData();

			selectedAudioPath = getRealPathFromURI(path);
			audioPath.setText(selectedAudioPath);

			//String name = (new File(selectedAudioPath)).getName();

			mPlayer = new MediaPlayer();
			mSeek.setOnSeekBarChangeListener(this); // listen to seekbar touches
			prepareSong();

			//audioName.setText(name);

			//MediaMetadataRetriever retrieve = new MediaMetadataRetriever();
			//retrieve.setDataSource(selectedAudioPath);

			//long durationMs = Long.parseLong(retrieve.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

			//Bitmap thumb = retrieve.getFrameAtTime(durationMs/2, 1);

			//int screenWidth;
			//Display display = getWindowManager().getDefaultDisplay();

			/*if (android.os.Build.VERSION.SDK_INT >= 13) {
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

			 */


		} else {
			Toast.makeText(getActivity().getApplicationContext(), "An error occured. Please try again.",
					Toast.LENGTH_LONG).show();
		}
	}

	public String getRealPathFromURI(Uri input) {

		String [] proj={MediaStore.Video.Media.DATA};
		Cursor cursor = getActivity().getContentResolver().query( input,
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

	private void prepareSong() {
		mPlayer.reset();

		try {
			mPlayer.setDataSource(selectedAudioPath);
			mPlayer.prepare();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		songLength = mPlayer.getDuration();

		mSeek.setMax(SEEK_MAX); // set the seekbar length to song length
		mSeek.setProgress(0); // restart the seekbar

		updateSeekBar(); // start seekbar movement
	}

	public void playPause() {
		if(mPlayer!=null){
			if (mPlayer.isPlaying()) {
				mPlayer.pause();
			}
			else {
				mPlayer.start();
			}
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// stop seekbar from moving
		pauseSeekBar();
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		int progress = mSeek.getProgress();
		mPlayer.seekTo( (progress*songLength) / SEEK_MAX );

		//resume seekbar
		updateSeekBar();
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		// not used
	}

	public void pauseSeekBar() {
		// remove message Handler from updating progress bar
		mHandler.removeCallbacks(mUpdateSeekBar);
	}

	public void updateSeekBar() {
		// begin repeated seekbar updates
		mHandler.postDelayed(mUpdateSeekBar, 100);
	}

	private Runnable mUpdateSeekBar = new Runnable() {
		public void run() {
			//move the seekbar
			int curPosition = mPlayer.getCurrentPosition();
			mSeek.setProgress( (curPosition*SEEK_MAX) / songLength );

			//repeat
			mHandler.postDelayed(this, 100);
		}
	};

}
