package com.example.android.inventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.android.inventory.data.*;


public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = CatalogActivity.class.getSimpleName();

    private static final int INVENTORY_LOADER = 1;

    private static final String mProjection[] = {InventoryContract.MobileEntry._ID, InventoryContract.MobileEntry.COLUMN_NAME_MOBILE_NAME, InventoryContract.MobileEntry.COLUMN_NAME_MOBILE_PRICE, InventoryContract.MobileEntry.COLUMN_NAME_MOBILE_QUANTITY};

    private InventoryCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        ListView listView = (ListView) findViewById(R.id.list_item);
        View emptyView = findViewById(R.id.emptyView);
        listView.setEmptyView(emptyView);

        getLoaderManager().initLoader(INVENTORY_LOADER, null, CatalogActivity.this);

        mCursorAdapter = new InventoryCursorAdapter(this, null);
        listView.setAdapter(mCursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri contentUri = ContentUris.withAppendedId(InventoryContract.MobileEntry.CONTENT_URI, id);

                Log.d(LOG_TAG, (String.valueOf(id)));

                Intent detailIntent = new Intent(CatalogActivity.this, DetailActivity.class);
                detailIntent.setData(contentUri);
                startActivity(detailIntent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editorIntent = new Intent(CatalogActivity.this, DetailActivity.class);
                startActivity(editorIntent);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        switch (loaderId){
            case INVENTORY_LOADER:
                return new CursorLoader(CatalogActivity.this, InventoryContract.MobileEntry.CONTENT_URI, mProjection, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}