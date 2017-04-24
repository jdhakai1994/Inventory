package com.example.android.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.android.inventory.data.InventoryContract.*;

/**
 * Created by Jayabrata Dhakai on 12/21/2016.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = InventoryDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MobileEntry.TABLE_NAME + " (" +
                    MobileEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    MobileEntry.COLUMN_NAME_MOBILE_NAME + " TEXT NOT NULL," +
                    MobileEntry.COLUMN_NAME_MOBILE_PRICE + " INTEGER NOT NULL," +
                    MobileEntry.COLUMN_NAME_MOBILE_QUANTITY + " INTEGER NOT NULL DEFAULT 0," +
                    MobileEntry.COLUMN_NAME_SUPPLIER_CONTACT + " TEXT NOT NULL," +
                    MobileEntry.COLUMN_NAME_MOBILE_IMAGE + " TEXT NOT NULL )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MobileEntry.TABLE_NAME;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.d(LOG_TAG, SQL_CREATE_ENTRIES);
        database.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.d(LOG_TAG, "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        Log.d(LOG_TAG, SQL_DELETE_ENTRIES);
        database.execSQL(SQL_DELETE_ENTRIES);
        onCreate(database);
    }
}
