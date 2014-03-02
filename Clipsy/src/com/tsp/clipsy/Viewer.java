package com.tsp.clipsy;

import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.VideoView;

public class Viewer extends Activity implements SeekBar.OnSeekBarChangeListener {

	private MediaPlayer mPlayer;
	private SeekBar mSeek;
	private Handler mHandler = new Handler();
	private int songLength;
	private final int SEEK_MAX = 1000;

	private String audioPath;
	private String videoPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewer);

		Bundle view = getIntent().getExtras();
		audioPath = view.getString("audio");
		videoPath = view.getString("video");


		// Video portion
		VideoView videoView = (VideoView) findViewById(R.id.videoView1);
		videoView.setVideoPath(videoPath);

		MediaController mediaController = new MediaController(this);
		mediaController.setAnchorView(videoView);
		videoView.setMediaController(mediaController);


		// Audio portion
		mSeek = (SeekBar) findViewById(R.id.seekBar);
		mPlayer = new MediaPlayer();
		mSeek.setOnSeekBarChangeListener(this); // listen to seekbar touches
		prepareSong();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void prepareSong() {
		mPlayer.reset();

		try {
			mPlayer.setDataSource(audioPath);
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

	public void playPause(View view) {
		if (mPlayer.isPlaying()) {
			mPlayer.pause();
		}
		else {
			mPlayer.start();
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
