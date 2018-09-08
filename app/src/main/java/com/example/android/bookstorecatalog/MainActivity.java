package com.example.android.bookstorecatalog;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.bookstorecatalog.data.BookstoreContract;
import com.example.android.bookstorecatalog.data.BookstoreHelper;

public class MainActivity extends AppCompatActivity {

    private BookstoreHelper mBookstoreHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //FloatingActionButton setup.  Takes user to BookEditor.
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.actionbutton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BookEditor.class);
                startActivity(intent);
            }
        });
        //instantiates SQLiteOpenHelper subclass.
        mBookstoreHelper = new BookstoreHelper(this);
    }

    //This helper method shows the DB info on the MainActivity TextView.
    private void displayDBInfo() {
        SQLiteDatabase sqLiteDatabase = mBookstoreHelper.getReadableDatabase();
        //Specifies the columns used for the query.
        String[] projection = {
                BookstoreContract.BookstoreCatalogEntry._ID,
                BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_NAME,
                BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_PRICE,
                BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_QUANTITY,
                BookstoreContract.BookstoreCatalogEntry.COL_SUPPLIER_NAME,
                BookstoreContract.BookstoreCatalogEntry.COL_SUPPLIER_PHONE_NUMBER};
        //performs query on the bookstore entries table.
        Cursor cursor = sqLiteDatabase.query(
                BookstoreContract.BookstoreCatalogEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        TextView textViewDisplayInfo = (TextView) findViewById(R.id.catalog_text_view);

        try {
            //Creates a header like this example below:
            //_id | Product Name | Price | Quantity | Supplier Name | Supplier Phone Number
            //then, it loops through the rows of the cursor and displays info in the aforementioned header example's order.
            textViewDisplayInfo.setText("This catalog currently has " + cursor.getCount() + " entries.\n\n");
            textViewDisplayInfo.append(BookstoreContract.BookstoreCatalogEntry._ID
                    + " | "
                    + BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_NAME
                    + " | "
                    + BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_PRICE
                    + " | "
                    + BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_QUANTITY
                    + " | "
                    + BookstoreContract.BookstoreCatalogEntry.COL_SUPPLIER_NAME
                    + " | "
                    + BookstoreContract.BookstoreCatalogEntry.COL_SUPPLIER_PHONE_NUMBER
                    + "\n");
            //determines indices
            int idIndex = cursor.getColumnIndex(BookstoreContract.BookstoreCatalogEntry._ID);
            int productNameIndex = cursor.getColumnIndex(BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_NAME);
            int priceIndex = cursor.getColumnIndex(BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_PRICE);
            int quantityIndex = cursor.getColumnIndex(BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_QUANTITY);
            int supplierNameIndex = cursor.getColumnIndex(BookstoreContract.BookstoreCatalogEntry.COL_SUPPLIER_NAME);
            int phoneNumIndex = cursor.getColumnIndex(BookstoreContract.BookstoreCatalogEntry.COL_SUPPLIER_PHONE_NUMBER);
            //loops through all returned rows in cursor
            while (cursor.moveToNext()) {

                int currentId = cursor.getInt(idIndex);
                String currentProduct = cursor.getString(productNameIndex);
                String currentPrice = cursor.getString(priceIndex);
                int currentQuantity = cursor.getInt(quantityIndex);
                String currentSupplier = cursor.getString(supplierNameIndex);
                String currentPhoneNo = cursor.getString(phoneNumIndex);
                //then displays the values from each column of the current row.
                textViewDisplayInfo.append(("\n"
                        + currentId
                        + " | "
                        + currentProduct
                        + " | "
                        + currentPrice
                        + " | "
                        + currentQuantity
                        + " | "
                        + currentSupplier
                        + " | "
                        + currentPhoneNo));

            }
        } finally {
            //Closes cursor.
            cursor.close();
        }
    }

    //Here's a formula for making dummy entries.
    private void insertDummyEntry() {
        SQLiteDatabase sqLiteDatabase = mBookstoreHelper.getWritableDatabase();

        //Creates object where columns are keys and this sample bookstore item contains values.
        ContentValues contentValues = new ContentValues();
        contentValues.put(BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_NAME, "Watership Down");
        contentValues.put(BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_PRICE, "$11.34");
        contentValues.put(BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_QUANTITY, 999);
        contentValues.put(BookstoreContract.BookstoreCatalogEntry.COL_SUPPLIER_NAME, "Phoney Baloney Publishing Co.");
        contentValues.put(BookstoreContract.BookstoreCatalogEntry.COL_SUPPLIER_PHONE_NUMBER, "1-877-767-2637");

        long newEntry = sqLiteDatabase.insert(BookstoreContract.BookstoreCatalogEntry.TABLE_NAME, null, contentValues);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bookstore_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Opens the menu in the main activity.
        switch (item.getItemId()) {
            //User clicked on the "insert test data' option.
            case R.id.test_data:
                insertDummyEntry();
                displayDBInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDBInfo();
    }
}
