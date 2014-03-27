package com.tsp.clipsy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AudioFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedState) {
		
		return inflater.inflate(R.layout.activity_audio_fragment, container, false); 
	}

}
