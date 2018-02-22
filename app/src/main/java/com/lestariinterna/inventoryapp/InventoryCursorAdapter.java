package com.lestariinterna.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.lestariinterna.inventoryapp.data.InventoryContract;

/**
 *
 * Created by AllinOne on 20/02/2018.
 * This Cursor Adapter get data from the database and feed it to the list view
 *
 */

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context, Cursor c){
        super(context, c,0);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.summary);

        int indexItemName = cursor.getColumnIndex(InventoryContract.InvEntry.COLUMN_INVENTORY_ITEMS);
        int indexQuantity = cursor.getColumnIndex(InventoryContract.InvEntry.COLUMN_INVENTORY_QUANTITY);

        String itemName = cursor.getString(indexItemName);
        String summary  = cursor.getString(indexQuantity);

        nameTextView.setText(itemName);
        summaryTextView.setText(summary);

    }
}
