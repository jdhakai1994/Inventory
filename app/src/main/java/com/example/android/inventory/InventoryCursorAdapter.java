package com.example.android.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.InventoryContract;

/**
 * Created by Jayabrata Dhakai on 12/21/2016.
 */

public class InventoryCursorAdapter extends CursorAdapter {

    private static final String LOG_TAG = InventoryCursorAdapter.class.getSimpleName();

    public InventoryCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.mobileName);
        TextView priceTextView = (TextView) view.findViewById(R.id.mobilePrice);
        TextView quantityTextView = (TextView) view.findViewById(R.id.mobileQuantity);
        ImageView decreaseSign = (ImageView) view.findViewById(R.id.decrease);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(InventoryContract.MobileEntry.COLUMN_NAME_MOBILE_NAME));
        int price = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryContract.MobileEntry.COLUMN_NAME_MOBILE_PRICE));
        final int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryContract.MobileEntry.COLUMN_NAME_MOBILE_QUANTITY));

        nameTextView.setText(name);
        priceTextView.setText(String.valueOf(price));
        quantityTextView.setText(String.valueOf(quantity));
        decreaseSign.setTag(cursor.getInt(cursor.getColumnIndexOrThrow(InventoryContract.MobileEntry._ID)));

        decreaseSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = (Integer) v.getTag();

                Uri uri = ContentUris.withAppendedId(InventoryContract.MobileEntry.CONTENT_URI, id);
                String projection[] = {InventoryContract.MobileEntry.COLUMN_NAME_MOBILE_QUANTITY};

                Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
                cursor.moveToFirst();
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryContract.MobileEntry.COLUMN_NAME_MOBILE_QUANTITY));

                if(quantity < 1)
                    Toast.makeText(context, R.string.mobile_updated_failure,
                            Toast.LENGTH_SHORT).show();
                else{
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(InventoryContract.MobileEntry.COLUMN_NAME_MOBILE_QUANTITY, quantity - 1);

                    int rowsUpdated = context.getContentResolver().update(uri, contentValues, null, null);

                    if (rowsUpdated == 0)
                        Toast.makeText(context, R.string.mobile_updated_failure,
                                Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context, R.string.mobile_updated_success,
                                Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}