package com.tsp.clipsy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

public class PreviewFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedState) {
		
		View inflated = inflater.inflate(R.layout.activity_preview_fragment, container, false);
		
		Bundle argsInfo = getActivity().getIntent().getExtras();
		String videoPath = argsInfo.getString("video");
		
		VideoView videoView = (VideoView) inflated.findViewById(R.id.previewView);
		videoView.setVideoPath(videoPath);

		MediaController mediaController = new MediaController(this.getActivity());
		mediaController.setAnchorView(videoView);
		videoView.setMediaController(mediaController);
		
		return inflated;
	}

}
