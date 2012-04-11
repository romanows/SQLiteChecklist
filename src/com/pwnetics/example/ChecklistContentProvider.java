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

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;


/**
 * Provides access to an example database table with dummy checklist information.
 * The URI {@link #URI_LIST} can be queried for all checklist item rows, and the
 * version with a specific row id can be updated so as to toggle the check.
 *
 * @author romanows
 */
public class ChecklistContentProvider extends ContentProvider {
    private static final String LOG_TAG = ChecklistContentProvider.class.getName();

    private static final String DB_NAME = "checklist";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE_LIST = "List";

    public static final String URI_AUTHORITY = "com.pwnetics.example.checklist";
    public static final Uri URI_LIST = new Uri.Builder().scheme("content").authority(URI_AUTHORITY).path(DB_TABLE_LIST).build();
    private static final int URI_LIST_CODE = 1;
    private static final int URI_LIST_ROW_CODE = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
    	uriMatcher.addURI(URI_AUTHORITY, DB_TABLE_LIST, URI_LIST_CODE);
    	uriMatcher.addURI(URI_AUTHORITY, DB_TABLE_LIST + "/#", URI_LIST_ROW_CODE);
    }

    private DatabaseHelper dbHelper;


    /**
     * Example database helper that creates a list with dummy data.
     * @author romanows
     */
    private class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper() {
            super(ChecklistContentProvider.this.getContext(), DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + DB_TABLE_LIST + " (_id INTEGER PRIMARY KEY, item TEXT, is_checked INTEGER)");
            for(int i=1; i<=20; i++) {
                String text = "Item " + i;
                String isChecked = i % 2 == 0 ? "1" : "0";
                db.execSQL("INSERT INTO " + DB_TABLE_LIST + " (item, is_checked) VALUES ('" + text + "', " + isChecked + ")");
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }


    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper();
        return true;
    }


    @Override
    public String getType(Uri uri) {
        Log.v(LOG_TAG, "getType(" + uri + ")");

    	int code = uriMatcher.match(uri);
    	switch (code) {
		case URI_LIST_CODE:
			return new StringBuilder("android.cursor.dir/vnd.").append(URI_AUTHORITY).append(".").append(DB_TABLE_LIST).toString();
		case URI_LIST_ROW_CODE:
			return new StringBuilder("android.cursor.item/vnd.").append(URI_AUTHORITY).append(".").append(DB_TABLE_LIST).toString();
		default:
			Log.e(LOG_TAG, "Unrecognized matching code for uri: " + uri);
			return null;
		}
    }


    @Override
    public Cursor query(Uri uri, String [] projection, String selection, String [] selectionArgs, String sortOrder) {
        Log.v(LOG_TAG, "query(" + uri + ")");

        if(uriMatcher.match(uri) != URI_LIST_CODE) {
        	Log.w(LOG_TAG, "Unrecognized matching code for uri: " + uri);
        	return null;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(false, DB_TABLE_LIST, projection, selection, selectionArgs, null, null, sortOrder, null);
        cursor.setNotificationUri(getContext().getContentResolver(), URI_LIST);
        return cursor;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection, String [] selectionArgs) {
        Log.v(LOG_TAG, "update(" + uri + ")");

        if(uriMatcher.match(uri) != URI_LIST_ROW_CODE) {
        	Log.w(LOG_TAG, "Unrecognized matching code for uri: " + uri);
        	return 0;
        }

    	SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
    	int numRows = 0;
    	if("1".equals(uri.getQueryParameter("toggle"))) {
    		values = new ContentValues();
    		values.put("is_checked", "0");
    		numRows = db.update(DB_TABLE_LIST, values, "_id=? AND is_checked=?", new String[] {uri.getLastPathSegment(), "1"});
    		if(numRows < 1) {
        		values = new ContentValues();
        		values.put("is_checked", "1");
        		numRows = db.update(DB_TABLE_LIST, values, "_id=? AND is_checked=?", new String[] {uri.getLastPathSegment(), "0"});
    		}
    		db.setTransactionSuccessful();
    	} else {
    		numRows = db.update(DB_TABLE_LIST, values, "_id=?", new String[] {uri.getLastPathSegment()});
    		db.setTransactionSuccessful();
    	}
		db.endTransaction();

    	if(numRows > 0) {
    		getContext().getContentResolver().notifyChange(URI_LIST, null);
    	}
    	return numRows;
    }


	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;  // Example only toggles column fields, doesn't support row insertion
	}


	@Override
	public int delete(Uri uri, String selection, String [] selectionArgs) {
		return 0;  // Example only toggles column fields, doesn't support row deletion
	}
}
