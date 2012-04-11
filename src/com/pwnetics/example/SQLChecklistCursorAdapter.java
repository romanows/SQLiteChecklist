/*
Copyright 2012 Brian Romanowski. All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are
permitted provided that the following conditions are met:

   1. Redistributions of source code must retain the above copyright notice, this list of
      conditions and the following disclaimer.

   2. Redistributions in binary form must reproduce the above copyright notice, this list
      of conditions and the following disclaimer in the documentation and/or other materials
      provided with the distribution.

THIS SOFTWARE IS PROVIDED BY BRIAN ROMANOWSKI ``AS IS'' AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BRIAN ROMANOWSKI OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those of the
authors.
*/


package com.pwnetics.example;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

public class SQLChecklistCursorAdapter extends CursorAdapter {
//    private static final String LOG_TAG = SQLChecklistCursorAdapter.class.getName();

    private final LayoutInflater layoutInflater;


    public SQLChecklistCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, false);
        layoutInflater = LayoutInflater.from(context);
	}


	@Override
	public void bindView(View view, final Context context, final Cursor cursor) {
		((TextView)view.findViewById(R.id.text)).setText(cursor.getString(cursor.getColumnIndex("item")));
		CheckBox checkbox = (CheckBox)view.findViewById(R.id.checkbox);
		checkbox.setChecked(cursor.getInt(cursor.getColumnIndex("is_checked")) > 0);

		// If we're not relying upon the ListView onListItemClick, we might want to set checks as below
		// However, then it does not seem straightforward to allow selection by, say, the trackball, as it doesn't
		// ... seem to be allowed to select the list item elements
		checkbox.setClickable(false);
//    	checkbox.setOnClickListener(new OnClickListener() {
//            final int id = cursor.getInt(cursor.getColumnIndex("_id"));
//
//			public void onClick(View v) {
//		    	Uri toggleUri = ChecklistContentProvider.URI_LIST.buildUpon().appendPath(String.valueOf(id)).appendQueryParameter("toggle", "1").build();
//		    	context.getContentResolver().update(toggleUri, null, null, null);
//			}
//		});

	}


	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return layoutInflater.inflate(R.layout.list_text_checkbox, null);
	}
}
