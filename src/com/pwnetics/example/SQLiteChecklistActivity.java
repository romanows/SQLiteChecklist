package com.pwnetics.example;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SimpleCursorAdapter;

/**
 * An example SQLite-backed checklist.
 * This is not perfect, as all database queries are performed on the UI thread.
 *
 * @author romanows
 */
public class SQLiteChecklistActivity extends ListActivity {
    private static final String DB_NAME = "test";
    private static final String DB_TABLE = "List";

    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;

    /**
     * Example database helper that creates a list with dummy data.
     * @author romanows
     */
    private class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper() {
            super(SQLiteChecklistActivity.this, DB_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + DB_TABLE + " (_id INTEGER PRIMARY KEY, item TEXT, is_checked INTEGER)");
            for(int i=1; i<=20; i++) {
                String text = "Item " + i;
                String isChecked = i % 2 == 0 ? "1" : "0";
                db.execSQL("INSERT INTO " + DB_TABLE + " (item, is_checked) VALUES ('" + text + "', " + isChecked + ")");
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }


    /**
     * Query to populate the list view.
     * Will be called whenever the checkbox checked state changes.
     * @return a new cursor
     */
    private Cursor getCursor() {
        Cursor c = db.rawQuery("SELECT _id, item, is_checked FROM " + DB_TABLE, null);
        startManagingCursor(c);
        return c;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // this.deleteDatabase(DB_NAME);  // Uncomment to drop database
        dbHelper = new DatabaseHelper();
        db = dbHelper.getWritableDatabase();

        Cursor c = getCursor();
        final SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.list_text_checkbox, c,
                new String[] {"item", "is_checked"},
                new int[] {R.id.text, R.id.checkbox});

        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, final Cursor cursor, int columnIndex) {
                if(columnIndex == 2) {
                    CheckBox checkbox = (CheckBox)view;
                    checkbox.setChecked(cursor.getInt(cursor.getColumnIndex("is_checked")) > 0);
                    checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                        final int id = cursor.getInt(cursor.getColumnIndex("_id"));

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            /*
                             * For some reason, onCheckedChanged is called whenever checkboxes scroll
                             * off screen.  Checking isShown() hopefully catches the cases where this
                             * method is called despite the checkbox state not having changed.
                             */
                            if(buttonView.isShown()) {
                                db.execSQL("UPDATE " + DB_TABLE + " SET is_checked=" + (isChecked ? "1" : "0") + " WHERE _id=" + id);
                                adapter.changeCursor(getCursor());
                            }
                        }
                    });
                    return true;
                }
                return false;
            }
        });

        setListAdapter(adapter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(dbHelper != null) {
            dbHelper.close();
        }
    }
}