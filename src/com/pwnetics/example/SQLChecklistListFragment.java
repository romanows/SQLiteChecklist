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

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class SQLChecklistListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = SQLChecklistListFragment.class.getName();

    private static final int LOADER_ID = 1;

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onActivityCreated()");
        super.onCreate(savedInstanceState);

        setEmptyText("No Checklist Loaded");
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }


    /*
     * (non-Javadoc)
     * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
     */
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "onCreateLoader() with id: " + id);
        if(id != LOADER_ID) {
			Log.e(LOG_TAG, "unexpected loader requested: " + id);
			return null;
        }
        return new CursorLoader(getActivity(), ChecklistContentProvider.URI_LIST,
        		new String[] {"_id", "item", "is_checked"}, null, null, null);
    }


    /*
     * (non-Javadoc)
     * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoadFinished(android.support.v4.content.Loader, java.lang.Object)
     */
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "onLoadFinished()");

        View topItemView = getListView().getChildAt(0);  // The first item visible in the list, or null if the list is empty
        int selectionFromTop = (topItemView == null) ? 0 : topItemView.getTop();  // May be negative if the first item is partially scrolled up out of view

        int selectedItemPosition = getSelectedItemPosition();
        if(selectedItemPosition < 0) {
        	selectedItemPosition = Math.max(0, getListView().getFirstVisiblePosition());
        } else {
        	View selectedView = getListView().getSelectedView();
        	for(int i=0; i<getListView().getChildCount(); i++) {
                View itemView = getListView().getChildAt(i);  // The first item visible in the list, or null if the list is empty
                if(itemView == selectedView) {
                	break;
                }
                selectionFromTop += (itemView == null) ? 0 : itemView.getHeight() + getListView().getDivider().getBounds().height();  // May be negative if the first item is partially scrolled up out of view
        	}
        }

        setListAdapter(new SQLChecklistCursorAdapter(getActivity(), data));

        if(isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }

    	getListView().setSelectionFromTop(selectedItemPosition, selectionFromTop);
    }


    /*
     * (non-Javadoc)
     * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(android.support.v4.content.Loader)
     */
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, "onLoaderReset()");
        setListAdapter(null);
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    	Log.v(LOG_TAG, "onListItemClick.position: " + position);
    	super.onListItemClick(l, v, position, id);

    	Uri toggleUri = ChecklistContentProvider.URI_LIST.buildUpon().appendPath(String.valueOf(id)).appendQueryParameter("toggle", "1").build();
    	getActivity().getContentResolver().update(toggleUri, null, null, null);
    }
}
