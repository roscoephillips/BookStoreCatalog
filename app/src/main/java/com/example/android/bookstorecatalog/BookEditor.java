package com.example.android.bookstorecatalog;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.android.bookstorecatalog.data.BookstoreContract;

import static com.example.android.bookstorecatalog.data.BookstoreContract.BookstoreCatalogEntry.CONTENT_URI;

public class BookEditor extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    //EditTexts for each entry field.
    private static final int EXISTING_BOOKSTORE_LOADER = 0;
    private Uri mCurrentProductUri;
    private EditText mProductNameEdit;
    private EditText mPriceEdit;
    private EditText mQuantityEdit;
    private EditText mSupplierNameEdit;
    private EditText mSupplierPhoneEdit;
    //ImageButtons to alter the quantity of the product.
    private ImageButton mIncreaseQuantity;
    private ImageButton mDecreaseQuantity;
    //ImageButton for contacting the supplier.
    private ImageButton mContactSupplier;
    //Boolean value that detects if product was edited
    private boolean mProductWasEdited = false;

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductWasEdited = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_edit_activity);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        //If the product is missing a content URI, this should be a new product.  As a result,
        //the title should be 'New Product Add.'
        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.add_a_new_product));
            //This removes the options menu.
            invalidateOptionsMenu();
        } else {
            //Title will be 'Edit Existing Product."
            setTitle(getString(R.string.edit_an_existing_product));

            //Bookstore data is read/retrieved from the DB and displayed in the editor.
            getSupportLoaderManager().initLoader(EXISTING_BOOKSTORE_LOADER, null, this);
        }


        //All the fields which take in user input.  If the prevously-saved input
        //has been touched or modified, the corresponding field will alert the user
        //about any unsaved changes.

        mProductNameEdit = (EditText) findViewById(R.id.edit_product_name);
        mProductNameEdit.setOnTouchListener(mOnTouchListener);

        mPriceEdit = (EditText) findViewById(R.id.edit_price);
        mPriceEdit.setOnTouchListener(mOnTouchListener);

        mQuantityEdit = (EditText) findViewById(R.id.edit_quantity);
        mQuantityEdit.setOnTouchListener(mOnTouchListener);

        mIncreaseQuantity = findViewById(R.id.quantity_increase);
        mIncreaseQuantity.setOnTouchListener(mOnTouchListener);
        mIncreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(mQuantityEdit.getText().toString());
                mQuantityEdit.setText(String.valueOf(quantity + 1));
            }
        });

        mDecreaseQuantity = findViewById(R.id.quantity_decrease);
        mDecreaseQuantity.setOnTouchListener(mOnTouchListener);
        mDecreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(mQuantityEdit.getText().toString());
                if (quantity > 0) {
                    mQuantityEdit.setText(String.valueOf(quantity - 1));
                }
            }
        });

        mSupplierNameEdit = (EditText) findViewById(R.id.edit_supplier_name);
        mSupplierNameEdit.setOnTouchListener(mOnTouchListener);

        mSupplierPhoneEdit = (EditText) findViewById(R.id.edit_supplier_phone);
        mSupplierPhoneEdit.setOnTouchListener(mOnTouchListener);
        mContactSupplier = findViewById(R.id.contact_supplier_button);


        mContactSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNo = mSupplierPhoneEdit.getText().toString().trim();
                Intent intentContactSupplier = new Intent(Intent.ACTION_DIAL);
                if (!TextUtils.isEmpty(phoneNo)) {
                    intentContactSupplier.setData(Uri.parse("tel:" + phoneNo));
                    startActivity(intentContactSupplier);
                }
            }
        });
    }

    private void saveProduct() {

        //analyzes input fields - will remove leading/trailing spaces in the process.
        String productNameString = mProductNameEdit.getText().toString().trim();
        String priceString = mPriceEdit.getText().toString().trim();
        String quantityString = mQuantityEdit.getText().toString();
        String supplierNameString = mSupplierNameEdit.getText().toString().trim();
        String supplierPhoneString = PhoneNumberUtils.formatNumber(mSupplierPhoneEdit.getText().toString().trim());

        //If nothing was touched at all, we can return without making any new product.
        if (mCurrentProductUri == null &&
                TextUtils.isEmpty(productNameString) && TextUtils.isEmpty(priceString)
                && TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierNameString)
                && TextUtils.isEmpty(supplierPhoneString)) {
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_NAME,
                productNameString);
        //Price will default as 0.25.
        Double price = 0.25;
        if (!TextUtils.isEmpty(priceString)) {
            price = Double.parseDouble(priceString);
        }
        contentValues.put(BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_PRICE,
                price);
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        contentValues.put(BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_QUANTITY,
                quantity);
        contentValues.put(BookstoreContract.BookstoreCatalogEntry.COL_SUPPLIER_NAME,
                supplierNameString);
        contentValues.put(BookstoreContract.BookstoreCatalogEntry.COL_SUPPLIER_PHONE_NUMBER,
                supplierPhoneString);

        //If this does not have a current product URI, this will count as a new entry.
        if (mCurrentProductUri == null) {
            //New product
            Uri newUri = getContentResolver().insert(CONTENT_URI, contentValues);
            //If the product insertion was successful, this Toast message will appear:
            if (!(newUri == null)) {
                Toast.makeText(this, getString(R.string.editor_successful_insertion), Toast.LENGTH_SHORT).show();
            } //Otherwise, this failure message will appear instead:
            else {
                Toast.makeText(this, getString(R.string.editor_unsuccessful_insertion), Toast.LENGTH_SHORT).show();
            }
        } else {
            //Existing records

            int rowsAffected = getContentResolver().update(mCurrentProductUri, contentValues, null, null);

            //If the product edit to an existing record was successful, this Toast message will appear:
            if (rowsAffected != 0) {
                Toast.makeText(this, getString(R.string.editor_successful_insertion), Toast.LENGTH_SHORT).show();
            } //Otherwise, this failure message will appear instead:
            else {
                Toast.makeText(this, getString(R.string.editor_unsuccessful_insertion), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bookstore_editor_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //Attempts saving product.
            case R.id.action_save:
                saveProduct();
                finish();
                return true;
            case R.id.action_delete:
                confirmDeletion();
                return true;

            case android.R.id.home:

                if (!mProductWasEdited) {
                    NavUtils.navigateUpFromSameTask(BookEditor.this);
                    return true;
                }

                DialogInterface.OnClickListener discardClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(BookEditor.this);
                            }
                        };
                showUnsavedDialogPrompt(discardClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                BookstoreContract.BookstoreCatalogEntry._ID,
                BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_NAME,
                BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_PRICE,
                BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_QUANTITY,
                BookstoreContract.BookstoreCatalogEntry.COL_SUPPLIER_NAME,
                BookstoreContract.BookstoreCatalogEntry.COL_SUPPLIER_PHONE_NUMBER};
        return new CursorLoader(this, mCurrentProductUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }

        if (data.moveToFirst()) {
            int nameColIndex = data.getColumnIndex(BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_NAME);
            int priceColIndex = data.getColumnIndex(BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_PRICE);
            int quantityColIndex = data.getColumnIndex(BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_QUANTITY);
            int supplierNameColIndex = data.getColumnIndex(BookstoreContract.BookstoreCatalogEntry.COL_SUPPLIER_NAME);
            int supplierPhoneColIndex = data.getColumnIndex(BookstoreContract.BookstoreCatalogEntry.COL_SUPPLIER_PHONE_NUMBER);

            String productName = data.getString(nameColIndex);
            Double price = data.getDouble(priceColIndex);
            int quantity = data.getInt(quantityColIndex);
            String supplierName = data.getString(supplierNameColIndex);
            String supplierPhoneNumber = data.getString(supplierPhoneColIndex);

            mProductNameEdit.setText(productName);
            mPriceEdit.setText(Double.toString(price));
            mQuantityEdit.setText(String.valueOf(quantity));
            mSupplierNameEdit.setText(supplierName);
            mSupplierPhoneEdit.setText(supplierPhoneNumber);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mProductNameEdit.setText("");
        mPriceEdit.setText("");
        mQuantityEdit.setText(0);
        mSupplierNameEdit.setText("");
        mSupplierPhoneEdit.setText("");
    }

    private void showUnsavedDialogPrompt(DialogInterface.OnClickListener discardClickListener) {
        //Creates an alert dialog to check if you want to discard your unsaved edits.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_prompt);
        builder.setPositiveButton(R.string.discard_yes, discardClickListener);
        builder.setNegativeButton(R.string.discard_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!(dialogInterface == null)) {
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void confirmDeletion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_record_prompt);
        builder.setPositiveButton(R.string.delete_record_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.delete_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            if (rowsDeleted != 0) {
                //Displays message that the record has been successfully deleted.
                Toast.makeText(this, getString(R.string.record_deleted_successfully), Toast.LENGTH_SHORT).show();
            } else {
                //Otherwise, the record will not be deleted.
                Toast.makeText(this, getString(R.string.unsuccessful_deletion), Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }
}



