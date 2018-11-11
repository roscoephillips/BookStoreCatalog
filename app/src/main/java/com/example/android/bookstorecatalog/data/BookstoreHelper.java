package com.example.android.bookstorecatalog.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookstoreHelper extends SQLiteOpenHelper {
    //DB filename.
    private static final String DB_NAME = "bookstore.db";

    //Database version.
    private static final int DB_VERSION = 1;

    public BookstoreHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Opening SQL statement that generates the bookstore_items table:
        String SQL_CREATE_BOOKSTORE_ITEMS_TBL =
                "CREATE TABLE " + BookstoreContract.BookstoreCatalogEntry.TABLE_NAME
                        + " (" + BookstoreContract.BookstoreCatalogEntry._ID
                        + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_NAME
                        + " TEXT NOT NULL, "
                        + BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_PRICE
                        + " REAL NOT NULL, "
                        + BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_QUANTITY
                        + " INTEGER NOT NULL DEFAULT 1, "
                        + BookstoreContract.BookstoreCatalogEntry.COL_SUPPLIER_NAME
                        + " TEXT NOT NULL, "
                        + BookstoreContract.BookstoreCatalogEntry.COL_SUPPLIER_PHONE_NUMBER
                        + " INTEGER NOT NULL );";
        //Execute SQL stmt
        sqLiteDatabase.execSQL(SQL_CREATE_BOOKSTORE_ITEMS_TBL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int pastVersion, int currentVersion) {
        //When I upgrade this database, I will add some more stuff here.
    }
}
