package com.example.android.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.example.android.inventory.data.InventoryContract.*;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Jayabrata Dhakai on 12/21/2016.
 */

public class InventoryProvider extends ContentProvider {

    // used for the UriMatcher
    private static final int MOBILES = 10;
    private static final int MOBILE_ID = 11;

    private static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    private InventoryDbHelper mDbHelper;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_MOBILES, MOBILES);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_MOBILES + "/#", MOBILE_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        switch (sUriMatcher.match(uri)){
            case MOBILES:
                cursor = database.query(MobileEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MOBILE_ID:
                selection = MobileEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(MobileEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case MOBILES:
                return MobileEntry.CONTENT_LIST_TYPE;
            case MOBILE_ID:
                return MobileEntry.CONTENT_LIST_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + sUriMatcher.match(uri));
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        switch (sUriMatcher.match(uri)) {
            case MOBILES:
                return insertMobile(uri, values);
            case MOBILE_ID:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
    }

    private Uri insertMobile(Uri uri, ContentValues values){

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        String name = values.getAsString(MobileEntry.COLUMN_NAME_MOBILE_NAME);
        if(name == null)
            throw new IllegalArgumentException("Mobile Name cannot be null");
        String image = values.getAsString(MobileEntry.COLUMN_NAME_MOBILE_IMAGE);
        if(image == null)
            throw new IllegalArgumentException("Mobile must have an image");
        Integer price = values.getAsInteger(MobileEntry.COLUMN_NAME_MOBILE_PRICE);
        if (price == null || price < 0)
            throw new IllegalArgumentException("Mobile requires valid price");
        Integer quantity = values.getAsInteger(MobileEntry.COLUMN_NAME_MOBILE_QUANTITY);
        if (quantity == null && quantity < 0)
            throw new IllegalArgumentException("Mobile requires valid quantity");
        String contact = values.getAsString(MobileEntry.COLUMN_NAME_SUPPLIER_CONTACT);
        if(contact == null)
            throw new IllegalArgumentException("Supplier Contact cannot be null");

        long id = database.insert(MobileEntry.TABLE_NAME, null, values);

        if(id == -1){
            Log.d(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case MOBILES:
                return updateMobile(uri, values, selection, selectionArgs);
            case MOBILE_ID:
                selection = MobileEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateMobile(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
    }

    private int updateMobile(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        if (values.size() == 0) {
            return 0;
        }

        if(values.containsKey(MobileEntry.COLUMN_NAME_MOBILE_NAME)) {
            String name = values.getAsString(MobileEntry.COLUMN_NAME_MOBILE_NAME);
            if (name == null)
                throw new IllegalArgumentException("Mobile Name cannot be null");
        }

        if(values.containsKey(MobileEntry.COLUMN_NAME_MOBILE_IMAGE)) {
            String image = values.getAsString(MobileEntry.COLUMN_NAME_MOBILE_IMAGE);
            if (image == null)
                throw new IllegalArgumentException("Mobile must have an image");
        }

        if(values.containsKey(MobileEntry.COLUMN_NAME_MOBILE_PRICE)) {
            Integer price = values.getAsInteger(MobileEntry.COLUMN_NAME_MOBILE_PRICE);
            if (price == null || price < 0)
                throw new IllegalArgumentException("Mobile requires valid price");
        }

        if(values.containsKey(MobileEntry.COLUMN_NAME_MOBILE_QUANTITY)) {
            Integer quantity = values.getAsInteger(MobileEntry.COLUMN_NAME_MOBILE_QUANTITY);
            if (quantity == null && quantity < 0)
                throw new IllegalArgumentException("Mobile requires valid quantity");
        }

        if(values.containsKey(MobileEntry.COLUMN_NAME_SUPPLIER_CONTACT)) {
            String contact = values.getAsString(MobileEntry.COLUMN_NAME_SUPPLIER_CONTACT);
            if (contact == null)
                throw new IllegalArgumentException("Supplier Contact cannot be null");
        }

        int noOfRows = database.update(MobileEntry.TABLE_NAME, values, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);

        return noOfRows;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int noOfRows;

        switch (sUriMatcher.match(uri)) {
            case MOBILES:
                noOfRows = database.delete(MobileEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOBILE_ID:
                selection = MobileEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                noOfRows = database.delete(MobileEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return noOfRows;
    }
}