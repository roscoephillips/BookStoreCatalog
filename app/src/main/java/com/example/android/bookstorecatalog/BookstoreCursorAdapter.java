package com.example.android.bookstorecatalog;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstorecatalog.data.BookstoreContract;

import java.text.NumberFormat;

public class BookstoreCursorAdapter extends CursorAdapter {

    static class ViewHolder{
        TextView productNameTextView;
        TextView productQuantityTextView;
        TextView priceTextView;
    }

    public BookstoreCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        ViewHolder viewHolder = new ViewHolder();

        viewHolder.productNameTextView = view.findViewById(R.id.item_name);
        viewHolder.productQuantityTextView = view.findViewById(R.id.item_quantity);
        viewHolder.priceTextView = view.findViewById(R.id.item_price);
        View saleButton = view.findViewById(R.id.sale_button);

        int idColIndex = cursor.getColumnIndex(BookstoreContract.BookstoreCatalogEntry._ID);
        int nameColIndex = cursor.getColumnIndex(BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_NAME);
        int priceIndex = cursor.getColumnIndex(BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_PRICE);
        int quantityColIndex = cursor.getColumnIndex(BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_QUANTITY);

        String productName = cursor.getString(nameColIndex);
        //Used https://kodejava.org/how-do-i-format-a-number-as-currency-string/ to assist with this.
        String productPrice = NumberFormat.getCurrencyInstance().format(cursor.getDouble(priceIndex));
        final String quantityText = String.valueOf(cursor.getInt(quantityColIndex));
        final int productID = cursor.getInt(idColIndex);

        viewHolder.productNameTextView.setText(productName);
        viewHolder.productQuantityTextView.setText(quantityText);
        viewHolder.priceTextView.setText(productPrice);

        //Subtracts one from the quantity.
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantityToBeUpdated = Integer.parseInt(quantityText);
                if (quantityToBeUpdated > 1) {
                    quantityToBeUpdated = quantityToBeUpdated - 1;
                    Uri uri = ContentUris.withAppendedId(BookstoreContract.BookstoreCatalogEntry.CONTENT_URI, productID);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(BookstoreContract.BookstoreCatalogEntry.COL_PRODUCT_QUANTITY, quantityToBeUpdated);
                    String s = BookstoreContract.BookstoreCatalogEntry._ID + "=?";
                    String[] strings = new String[]{String.valueOf(productID)};

                    int rowsAffected = view.getContext().getContentResolver().update(uri, contentValues, s, strings);
                } else {
                    Toast.makeText(context, "Quantity cannot be less than zero.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

