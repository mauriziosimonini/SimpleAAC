package com.example.simpleaac;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "simpleaac.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_ITEMS = "items";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TEXT = "text";
    private static final String COLUMN_IMAGE_PATH = "image_path";

    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        Log.d(TAG, "DatabaseHelper constructed");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating database tables");
        String CREATE_TABLE = "CREATE TABLE " + TABLE_ITEMS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TEXT + " TEXT NOT NULL, "
                + COLUMN_IMAGE_PATH + " TEXT)";
        db.execSQL(CREATE_TABLE);

        // Add some initial data
        addInitialData(db);
    }

    private void addInitialData(SQLiteDatabase db) {
        Log.d(TAG, "Adding initial data");
        String[] texts = {"Yes", "No", "Hello", "Help", "Food", "Drink"};

        for (String text : texts) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_TEXT, text);
            long id = db.insert(TABLE_ITEMS, null, values);
            Log.d(TAG, "Inserted item with id: " + id + " and text: " + text);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    public long addItem(String text, String imagePath) {
        Log.d(TAG, "Adding new item: " + text + " with image: " + imagePath);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TEXT, text);
        values.put(COLUMN_IMAGE_PATH, imagePath);
        long id = db.insert(TABLE_ITEMS, null, values);
        db.close();
        Log.d(TAG, "Added item with id: " + id);
        return id;
    }

    // Add this new method for deleting items
    public boolean deleteItem(int id) {
        Log.d(TAG, "Deleting item with id: " + id);
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_ITEMS, COLUMN_ID + "=?",
                new String[]{String.valueOf(id)});
        db.close();
        Log.d(TAG, "Delete result: " + (result > 0));
        return result > 0;
    }

    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<Item>();
        String selectQuery = "SELECT * FROM " + TABLE_ITEMS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        Log.d(TAG, "Getting all items, cursor count: " + cursor.getCount());

        if (cursor.moveToFirst()) {
            do {
                Item item = new Item();
                item.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                item.setText(cursor.getString(cursor.getColumnIndex(COLUMN_TEXT)));
                item.setImagePath(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_PATH)));
                items.add(item);
                Log.d(TAG, "Retrieved item: " + item.getText());
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return items;
    }
}