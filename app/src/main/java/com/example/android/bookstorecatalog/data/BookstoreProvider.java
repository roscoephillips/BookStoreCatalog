package com.example.android.bookstorecatalog.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import static com.example.android.bookstorecatalog.data.BookstoreContract.BookstoreCatalogEntry.CONTENT_ITEM_TYPE;
import static com.example.android.bookstorecatalog.data.BookstoreContract.BookstoreCatalogEntry.CONTENT_LIST_TYPE;
import static com.example.android.bookstorecatalog.data.BookstoreContract.BookstoreCatalogEntry.TABLE_NAME;
import static com.example.android.bookstorecatalog.data.BookstoreContract.CONTENT_AUTHORITY;
import static com.example.android.bookstorecatalog.data.BookstoreContract.DIRECTORY_BOOKSTORE_ITEMS;

public class BookstoreProvider extends ContentProvider {

    //Log Tag
    private static final String LOG_TAG = BookstoreProvider.class.getSimpleName();
    //URI for whole table
    private static final int CATALOG = 1134;
    //URI for individual items in the table
    private static final int CATALOG_ID = 666;

    private static final UriMatcher newUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        //URI for many rows
        newUriMatcher.addURI(CONTENT_AUTHORITY, DIRECTORY_BOOKSTORE_ITEMS, CATALOG);
        //URI for specific row
        newUriMatcher.addURI(CONTENT_AUTHORITY, DIRECTORY_BOOKSTORE_ITEMS + "/#", CATALOG_ID);
    }

    //Bookstore Helper
    private BookstoreHelper bookstoreHelper;

    @Override
    public boolean onCreate() {
        bookstoreHelper = new BookstoreHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String selection, String[] selectionArgs, String sort) {
        SQLiteDatabase sqLiteDatabase = bookstoreHelper.getReadableDatabase();

        Cursor cursor;

        int match = newUriMatcher.match(uri);
        switch (match) {
            //Query the whole catalog table.
            case CATALOG:
                cursor = sqLiteDatabase.query(TABLE_NAME, strings, selection, selectionArgs, null, null, sort);
                break;
            //Query specific IDs.
            case CATALOG_ID:
                selection = BookstoreContract.BookstoreCatalogEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = sqLiteDatabase.query(TABLE_NAME, strings, selection, selectionArgs, null, null, sort);
                break;
            default:
                throw new IllegalArgumentException("Query URI " + uri + " is invalid.");
        }
        //Set notification URI and return cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = newUriMatcher.match(uri);
        switch (match) {
            case CATALOG:
                return CONTENT_LIST_TYPE;
            case CATALOG_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Match " + match + "is linked to unknown URI " + uri + ".");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = newUriMatcher.match(uri);
        switch (match) {
            case CATALOG:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion not supported for " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues contentValues) {
        String productName = contentValues.getAsString(BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_NAME);
        if (productName == null) {
            throw new IllegalArgumentException("Product name required.");
        }

        Double productPrice = contentValues.getAsDouble(BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_PRICE);
        if (productPrice <= 0) {
            throw new IllegalArgumentException("Product requires a valid price.");
        }

        Integer productQuantity = contentValues.getAsInteger(BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_QUANTITY);
        if (productQuantity != null && productQuantity < 0) {
            throw new IllegalArgumentException("Product quantity is invalid.  Valid quantity required");
        }

        String supplierName = contentValues.getAsString(BookstoreContract.BookstoreCatalogEntry.COL_SUPPLIER_NAME);
        String supplierPhoneNumber = contentValues.getAsString(BookstoreContract.BookstoreCatalogEntry.COL_SUPPLIER_PHONE_NUMBER);

        //Inserting the data
        SQLiteDatabase sqLiteDatabase = bookstoreHelper.getWritableDatabase();
        long id = sqLiteDatabase.insert(BookstoreContract.BookstoreCatalogEntry.TABLE_NAME, null, contentValues);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        //Notify listeners of the change; return URI
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }


    @Override
    public int delete(Uri uri, String s, String[] strings) {
        SQLiteDatabase sqLiteDatabase = bookstoreHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = newUriMatcher.match(uri);
        switch (match) {
            case CATALOG:
                rowsDeleted = sqLiteDatabase.delete(BookstoreContract.BookstoreCatalogEntry.TABLE_NAME, s, strings);
                break;
            case CATALOG_ID:
                s = BookstoreContract.BookstoreCatalogEntry._ID + "=?";
                strings = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = sqLiteDatabase.delete(BookstoreContract.BookstoreCatalogEntry.TABLE_NAME, s, strings);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        final int match = newUriMatcher.match(uri);
        switch (match) {
            case CATALOG:
                return updateProduct(uri, contentValues, s, strings);
            case CATALOG_ID:
                s = BookstoreContract.BookstoreCatalogEntry._ID + "=?";
                strings = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, s, strings);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    //Update an existing record
    private int updateProduct(Uri uri, ContentValues contentValues, String s, String[] strings) {
        if (contentValues.containsKey(BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_NAME)) {
            String productName = contentValues.getAsString(BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_NAME);
            if (productName == null) {
                throw new IllegalArgumentException("Product name required.");
            }
        }

        if (contentValues.containsKey(BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_PRICE)) {
            Double productPrice = contentValues.getAsDouble(BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_PRICE);
            if (productPrice <= 0) {
                throw new IllegalArgumentException("Product requires a valid price.");
            }
        }

        if (contentValues.containsKey(BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_QUANTITY)) {
            Integer productQuantity = contentValues.getAsInteger(BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_QUANTITY);
            if (productQuantity == null || productQuantity < 0) {
                throw new IllegalArgumentException("Product quantity is invalid.  Valid quantity required");
            }
        }

        if (contentValues.containsKey(BookstoreContract.BookstoreCatalogEntry.COL_SUPPLIER_NAME)) {
            String supplierName = contentValues.getAsString(BookstoreContract.BookstoreCatalogEntry.COL_SUPPLIER_NAME);
        }

        if (contentValues.containsKey(BookstoreContract.BookstoreCatalogEntry.COL_SUPPLIER_PHONE_NUMBER)) {
            String supplierPhoneNumber = contentValues.getAsString(BookstoreContract.BookstoreCatalogEntry.COL_SUPPLIER_PHONE_NUMBER);
        }

        if (contentValues.size() == 0) {
            return 0;
        }

        SQLiteDatabase sqLiteDatabase = bookstoreHelper.getWritableDatabase();

        //# of rows updated
        int rowsUpdated = sqLiteDatabase.update(BookstoreContract.BookstoreCatalogEntry.TABLE_NAME, contentValues, s, strings);
        //If any non-zero # of rows were updated, notify listeners & return # of rows.
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

}

