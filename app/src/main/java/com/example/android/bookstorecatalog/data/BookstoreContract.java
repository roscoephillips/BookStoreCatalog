package com.example.android.bookstorecatalog.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class BookstoreContract {


    //empty constructor
    private BookstoreContract () {}

    public static final String CONTENT_AUTHORITY = "com.example.android.bookstorecatalog";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String DIRECTORY_BOOKSTORE_ITEMS = "bookstore_items";

    public static final class BookstoreCatalogEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, DIRECTORY_BOOKSTORE_ITEMS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + DIRECTORY_BOOKSTORE_ITEMS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + DIRECTORY_BOOKSTORE_ITEMS;

        public final static String TABLE_NAME = "bookstore_items";

        //Unique ID number for catalog entry.  Type INTEGER.
        public final static String _ID = BaseColumns._ID;

        //product name.  Type TEXT.
        public final static String COL_PRODUCT_NAME = "product";

        //price of product, type INTEGER.
        public final static String COL_PRODUCT_PRICE = "price";

        //quantity of stock, type INTEGER.
        public final static String COL_PRODUCT_QUANTITY = "quantity";

        //supplier's name, type TEXT.
        public final static String COL_SUPPLIER_NAME = "supplier";

        //phone number, type INTEGER.
        public final static String COL_SUPPLIER_PHONE_NUMBER = "phoneno";

        public static final int BASE_QUANTITY = 0;

        public static boolean isValidQuantity(int quantity) {
            if (quantity >= 0) {
                return true;
            }
            return false;
        }
    }
}
