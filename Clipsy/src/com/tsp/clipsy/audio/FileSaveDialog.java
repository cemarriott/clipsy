/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tsp.clipsy.audio;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;

import com.tsp.clipsy.R;
import com.tsp.clipsy.R.id;
import com.tsp.clipsy.R.layout;
import com.tsp.clipsy.R.string;

public class FileSaveDialog extends Dialog {

    private EditText mFilename;
    private Message mResponse;
    private String mOriginalName;

    public FileSaveDialog(Context context,
                          Resources resources,
                          String originalName,
                          Message response) {
        super(context);

        // Inflate our UI from its XML layout description.
        setContentView(R.layout.file_save);

        setTitle(resources.getString(R.string.file_save_title));

        mFilename = (EditText)findViewById(R.id.filename);
        mOriginalName = originalName;

        mFilename.setText(mOriginalName + " - Clipsy");

        Button save = (Button)findViewById(R.id.save);
        save.setOnClickListener(saveListener);
        Button cancel = (Button)findViewById(R.id.cancel);
        cancel.setOnClickListener(cancelListener);
        mResponse = response;
    }

    private View.OnClickListener saveListener = new View.OnClickListener() {
            public void onClick(View view) {
                mResponse.obj = mFilename.getText();
                mResponse.sendToTarget();
                dismiss();
            }
        };

    private View.OnClickListener cancelListener = new View.OnClickListener() {
            public void onClick(View view) {
                dismiss();
            }
        };
}
