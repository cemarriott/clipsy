package com.tsp.clipsy;

import java.io.IOException;
import java.nio.ByteBuffer;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.media.*;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaMuxer.OutputFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class VideoFragment extends Fragment implements SurfaceHolder.Callback{

	private String videoPath;
	private PlayerThread mPlayer = null;
	private SeekBar bar = null;
	private boolean pause = false;
	private boolean cutBefore = true;
	private int cutTime = 0;
	//private int seekBarLoc = 0;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
		super.onCreate(savedState);
		View inflated = inflater.inflate(R.layout.activity_video_fragment, container, false);
		
		bar = (SeekBar) inflated.findViewById(R.id.seekBar1);
		
		SurfaceView sv = (SurfaceView) inflated.findViewById(R.id.surfaceView1);
		sv.getHolder().addCallback(this);
		
		
		
		videoPath = getActivity().getIntent().getExtras().getString("video");
		
		
		MediaPlayer mp = MediaPlayer.create(null, Uri.parse(videoPath));
		int duration = mp.getDuration();
		bar.setMax(duration);
		
		final Button pauseButton = (Button) inflated.findViewById(R.id.button1);
		final Button clipButton = (Button) inflated.findViewById(R.id.clipButton);
		
		pauseButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(pause){
					pause = false;
				}else{
					pause = true;
				}
				
			}
		});
		clipButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				cutTime = bar.getProgress();
				Log.d("cut time", cutTime+"");
			}
	
		});
		
		return inflated;
	}


	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.e("test", "created");
		if (mPlayer == null) {
			mPlayer = new PlayerThread(holder.getSurface());
			mPlayer.start();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mPlayer != null) {
			mPlayer.interrupt();
		}
	}
	private class PlayerThread extends Thread {
		private MediaExtractor extractor;
		private MediaCodec decoder;
		private Surface surface;

		public PlayerThread(Surface surface) {
			this.surface = surface;
		}

		@Override
		public void run() {
			//make a new extractor
			extractor = new MediaExtractor();
			try {
				//set the data source to the selected video
				extractor.setDataSource(videoPath);
			} catch (IOException e1) {
				//e1.printStackTrace();
			}
			//figure out which stream is the video stream
			for (int i = 0; i < extractor.getTrackCount(); i++) {
				MediaFormat format = extractor.getTrackFormat(i);
				String mime = format.getString(MediaFormat.KEY_MIME);
				
				if (mime.startsWith("video/")) {
					extractor.selectTrack(i);
					decoder = MediaCodec.createDecoderByType(mime);
					decoder.configure(format, surface, null, 0);
					break;
				}
			}

			if (decoder == null) {
				Log.e("DecodeActivity", "Can't find video info!");
				return;
			}
			//start the decoder
			decoder.start();

			//get the input and output buffers from the decoder
			ByteBuffer[] inputBuffers = decoder.getInputBuffers();
			ByteBuffer[] outputBuffers = decoder.getOutputBuffers();
			//initialize the info buffer, the end of stream flag, and
			//the current time
			BufferInfo info = new BufferInfo();
			boolean isEOS = false;
			long startMs = System.currentTimeMillis();
			
			//continue until the thread is stopped
			while (!Thread.interrupted()) {
				while(pause){};
				//make sure it's not the end of the stream
				if (!isEOS) {
					//the input index is the index of the current input buffer
					Log.d("seek bar", (int) info.presentationTimeUs/1000+"");
					Log.d("bar value", bar.getProgress()+"");
					bar.setProgress((int) info.presentationTimeUs/1000);
					int inIndex = decoder.dequeueInputBuffer(10000);
					if (inIndex >= 0) {
						//look at the current input buffer
						ByteBuffer buffer = inputBuffers[inIndex];
						//get the size of the sample
						int sampleSize = extractor.readSampleData(buffer, 0);
						if (sampleSize < 0) {
							// We shouldn't stop the playback at this point, just pass the EOS
							// flag to decoder, we will get it again from the
							// dequeueOutputBuffer
							Log.d("DecodeActivity", "InputBuffer BUFFER_FLAG_END_OF_STREAM");
							decoder.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
							isEOS = true;
						} else {
							//queue the input buffer so it plays on the surface next
							decoder.queueInputBuffer(inIndex, 0, sampleSize, extractor.getSampleTime(), 0);
							//move to the next buffer
							extractor.advance();
						}
					}
				}
				//save the current index for the output buffer
				int outIndex = decoder.dequeueOutputBuffer(info, 100000);
				//depending on what the index is do one of 4 things
				switch (outIndex) {
				case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
					Log.d("DecodeActivity", "INFO_OUTPUT_BUFFERS_CHANGED");
					outputBuffers = decoder.getOutputBuffers();
					break;
				case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
					Log.d("DecodeActivity", "New format " + decoder.getOutputFormat());
					break;
				case MediaCodec.INFO_TRY_AGAIN_LATER:
					Log.d("DecodeActivity", "dequeueOutputBuffer timed out!");
					break;
				default:
					ByteBuffer buffer = outputBuffers[outIndex];
					Log.v("DecodeActivity", "We can't use this buffer but render it due to the API limit, " + buffer);

					// We use a very simple clock to keep the video FPS, or the video
					// playback will be too fast
					while (info.presentationTimeUs / 1000 > System.currentTimeMillis() - startMs) {
						try {
							sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
							break;
						}
					}
					decoder.releaseOutputBuffer(outIndex, true);
					break;
				}

				// All decoded frames have been rendered, we can stop playing now
				if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
					Log.d("DecodeActivity", "OutputBuffer BUFFER_FLAG_END_OF_STREAM");
					break;
				}
			}
			//stop and release the decoder and extractor
			decoder.stop();
			decoder.release();
			extractor.release();
		}
	}
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	@SuppressLint("NewApi")
	private class clipThread extends Thread{
		@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
		@SuppressLint("NewApi")
		public void run() {
			MediaExtractor extractor = new MediaExtractor();
			MediaMuxer muxer = null;
			int trackIndex = 0;
			ByteBuffer buffer = null;
			BufferInfo info = new BufferInfo();
			boolean isEOS = false;
			
			try {
				muxer = new MediaMuxer("temp.mp4", OutputFormat.MUXER_OUTPUT_MPEG_4);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				//set the data source to the selected video
				extractor.setDataSource(videoPath);
			} catch (IOException e1) {
				//e1.printStackTrace();
			}
			//figure out which stream is the video stream
			for (int i = 0; i < extractor.getTrackCount(); i++) {
				MediaFormat format = extractor.getTrackFormat(i);
				String mime = format.getString(MediaFormat.KEY_MIME);
				
				if (mime.startsWith("video/")) {
					extractor.selectTrack(i);
					trackIndex = muxer.addTrack(format);
					break;
				}
			}
			
			muxer.start();
			//still to do:
			//		figure out the seekto bullshit
			//		mux the fuck out of some shit
			while(!Thread.interrupted()){
				if(!isEOS){
					int sampleSize = extractor.readSampleData(buffer, 0);
					if(sampleSize < 0){
						isEOS = true;
					}else{
						muxer.writeSampleData(trackIndex, buffer, info);
						extractor.advance();
					}
				}
				
				
				
			}
			
			
			
		}
	}
}
