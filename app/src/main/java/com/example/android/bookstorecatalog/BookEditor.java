package com.example.android.bookstorecatalog;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.bookstorecatalog.data.BookstoreContract;
import com.example.android.bookstorecatalog.data.BookstoreHelper;

import java.text.NumberFormat;

public class BookEditor extends AppCompatActivity {

    //EditTexts for each entry field.
    private EditText mProductNameEdit;
    private EditText mPriceEdit;
    private EditText mQuantityEdit;
    private EditText mSupplierNameEdit;
    private EditText mSupplierPhoneEdit;

    //ImageView that responds to item selection.
    ImageView addItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_edit_activity);

        //All the fields which take in user input:
        mProductNameEdit = (EditText) findViewById(R.id.edit_product_name);
        mPriceEdit = (EditText) findViewById(R.id.edit_price);
        mQuantityEdit = (EditText) findViewById(R.id.edit_quantity);
        mSupplierNameEdit = (EditText) findViewById(R.id.edit_supplier_name);
        mSupplierPhoneEdit = (EditText) findViewById(R.id.edit_supplier_phone);

        addItem = findViewById(R.id.add_entry);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertCatalogEntry();
                finish();
            }
        });
    }

    //Gets user input from editor, then saves item to DB.
    private void insertCatalogEntry() {
        //Reads all input fields
        String productNameString = mProductNameEdit.getText().toString().trim();
        String priceString = mPriceEdit.getText().toString().trim();
        String price = NumberFormat.getCurrencyInstance().format(Double.parseDouble(priceString));
        String quantityString = mQuantityEdit.getText().toString().trim();
        int quantity = Integer.parseInt(quantityString);
        String supplierNameString = mSupplierNameEdit.getText().toString().trim();
        String supplierPhoneString = PhoneNumberUtils.formatNumber(mSupplierPhoneEdit.getText().toString().trim());


        //Create db helper
        BookstoreHelper bookstoreHelper = new BookstoreHelper(this);
        //DB's write mode:
        SQLiteDatabase sqLiteDatabase = bookstoreHelper.getWritableDatabase();

        //creates ContentValues object with column names as keys.
        ContentValues contentValues = new ContentValues();
        contentValues.put(BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_NAME, productNameString);
        contentValues.put(BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_PRICE, price);
        contentValues.put(BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_QUANTITY, quantity);
        contentValues.put(BookstoreContract.BookstoreCatalogEntry.COL_SUPPLIER_NAME, supplierNameString);
        contentValues.put(BookstoreContract.BookstoreCatalogEntry.COL_SUPPLIER_PHONE_NUMBER, supplierPhoneString);

        //Inserts new entry into the DB, returns ID of new row.
        long entryID = sqLiteDatabase.insert(BookstoreContract.BookstoreCatalogEntry.TABLE_NAME, null, contentValues);


        //Displays message contingent upon new entry insertion.
        if (entryID == -1) {
            //Insertion failed.
            Toast.makeText(this, "Your entry did not save...", Toast.LENGTH_SHORT).show();
        } else {
            //Insertion was successful.
            Toast.makeText(this, "Success!  Your entry is saved under the entry ID: " + entryID, Toast.LENGTH_LONG).show();
        }
    }
}

