package com.tsp.clipsy;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class NewProjectDialog extends DialogFragment{
	
	public interface NewProjectDialogListener {
        void onFinishEditDialog(String inputText);
    }
	   private EditText mEditText;

	    public NewProjectDialog() {
	        // Empty constructor required for DialogFragment
	    }

	    @Override 
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	        View view = inflater.inflate(R.layout.fragment_new_project, container);
	        mEditText = (EditText) view.findViewById(R.id.project_name);
	        getDialog().setTitle("Hello");
	        
	        // Show soft keyboard automatically
	        mEditText.requestFocus();
	        getDialog().getWindow().setSoftInputMode(
	                LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	        mEditText.setOnEditorActionListener((OnEditorActionListener) this);
	        
	        return view;
	    }
	    
	    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	        if (EditorInfo.IME_ACTION_DONE == actionId) {
	            // Return input text to activity
	            NewProjectDialogListener activity = (NewProjectDialogListener) getActivity();
	            activity.onFinishEditDialog(mEditText.getText().toString());
	            this.dismiss();
	            return true;
	        }
	        return false;
	    }
	}
