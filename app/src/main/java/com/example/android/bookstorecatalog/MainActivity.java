package com.example.android.bookstorecatalog;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.bookstorecatalog.data.BookstoreContract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;

    BookstoreCursorAdapter mBookstoreCursorAdapter;

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

        //Listview that will show all the bookstore entries
        ListView catalogListView = (ListView) findViewById(R.id.list);

        //When the ListView is empty, this View should display.
        View emptyView = findViewById(R.id.empty_bookshelf);
        catalogListView.setEmptyView(emptyView);

        //Adapter setup for a list item for reach row of data in the cursor
        mBookstoreCursorAdapter = new BookstoreCursorAdapter(this, null);
        catalogListView.setAdapter(mBookstoreCursorAdapter);

        catalogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, BookEditor.class);

                Uri currentProductUri = ContentUris.withAppendedId(BookstoreContract.BookstoreCatalogEntry.CONTENT_URI, l);

                intent.setData(currentProductUri);
                startActivity(intent);
            }
        });
        getSupportLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    private void insertDummyEntry() {
        //Creates object where columns are keys and this sample bookstore item contains values.
        ContentValues contentValues = new ContentValues();
        contentValues.put(BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_NAME, "Watership Down");
        contentValues.put(BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_PRICE, "11.34");
        contentValues.put(BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_QUANTITY, 999);
        contentValues.put(BookstoreContract.BookstoreCatalogEntry.COL_SUPPLIER_NAME, "Phoney Baloney Publishing Co.");
        contentValues.put(BookstoreContract.BookstoreCatalogEntry.COL_SUPPLIER_PHONE_NUMBER, "1-877-767-2637");

        Uri newUri = getContentResolver().insert(BookstoreContract.BookstoreCatalogEntry.CONTENT_URI, contentValues);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bookstore_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.test_data:
                insertDummyEntry();
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
                BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_QUANTITY};
        return new CursorLoader(this,
                BookstoreContract.BookstoreCatalogEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mBookstoreCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mBookstoreCursorAdapter.swapCursor(null);
    }

}
