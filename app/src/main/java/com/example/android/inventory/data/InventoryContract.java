package com.example.android.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Jayabrata Dhakai on 12/21/2016.
 */

public final class InventoryContract {

    private InventoryContract(){}

    public static final String CONTENT_AUTHORITY = "com.example.android.inventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOBILES = "mobiles";

    public static class MobileEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(InventoryContract.BASE_CONTENT_URI, PATH_MOBILES);

        public static final String TABLE_NAME = "mobiles";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME_MOBILE_NAME = "name";
        public static final String COLUMN_NAME_MOBILE_PRICE = "price";
        public static final String COLUMN_NAME_MOBILE_QUANTITY = "quantity";
        public static final String COLUMN_NAME_MOBILE_IMAGE = "image";
        public static final String COLUMN_NAME_SUPPLIER_CONTACT = "contact";

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOBILES;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOBILES;
    }

}
