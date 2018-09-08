package com.example.android.bookstorecatalog.data;

import android.provider.BaseColumns;

public final class BookstoreContract {


    //empty constructor
    private BookstoreContract () {}

    public static final class BookstoreCatalogEntry implements BaseColumns {

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
    }
}
