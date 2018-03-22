package com.lestariinterna.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.lestariinterna.inventoryapp.data.InventoryContract;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 *
 * Created by AllinOne on 20/02/2018.
 * This Cursor Adapter get data from the database and feed it to the list view
 *
 */

public class InventoryCursorAdapter extends CursorAdapter {

    //To store the number in stock
    int quantity;
    Context context;


    public InventoryCursorAdapter(Context context, Cursor c){
        super(context, c,0);
        this.context = context;


    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {


        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView hargaTextView = (TextView) view.findViewById(R.id.harga);
        final TextView stockTextView = (TextView)view.findViewById(R.id.stock);

        int indexItemName = cursor.getColumnIndex(InventoryContract.InvEntry.COLUMN_INVENTORY_ITEMS);
        int indexQuantity = cursor.getColumnIndex(InventoryContract.InvEntry.COLUMN_INVENTORY_QUANTITY);
        int indexPrice    = cursor.getColumnIndex(InventoryContract.InvEntry.COLUMN_INVENTORY_PRICE);

        final String itemName = cursor.getString(indexItemName);
        quantity  = cursor.getInt(indexQuantity);
        int price    = cursor.getInt(indexPrice);

        //Formatting the price to have thousand separator
        final Locale currentLocale = Locale.getDefault();
        DecimalFormatSymbols other = new DecimalFormatSymbols(currentLocale);
        other.setGroupingSeparator('.');
        other.setDecimalSeparator(',');

        String format = "#,###,###";
        DecimalFormat formatNumber = new DecimalFormat(format,other);

        String formattedPrice = formatNumber.format(price);


        //Bind the data with the list view item

        nameTextView.setText(itemName);
        hargaTextView.setText(formattedPrice);
        stockTextView.setText(String.valueOf(quantity));

        //To get the position of item in the listView
        final int id =cursor.getInt(cursor.getColumnIndex(InventoryContract.InvEntry._ID));
                //Get id for the list
                Button button = (Button) view.findViewById(R.id.ButtonSale);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



//                        CatalogActivity catalogActivity = (CatalogActivity) context;
//                        catalogActivity.decreaseCount( id, quantity);
//                        //To decrease quantity by 1 per click
                        quantity = quantity - 1;
                       // stockTextView.setText(String.valueOf(quantity));
//                        int id =(int)v.getTag();
//                        Log.v("Test","Test"+id);
//                        CatalogActivity catalogActivity =
                        Uri currentUri = ContentUris.withAppendedId(InventoryContract.CONTENT_URI,id);
                        ContentValues contentValues = new ContentValues();
//                        String selection = InventoryContract.InvEntry._ID + "=?";
//                        String[] selectionArgs = {String.valueOf(id)};
                        contentValues.put(InventoryContract.InvEntry.COLUMN_INVENTORY_QUANTITY,quantity);
                        context.getContentResolver().update(currentUri,contentValues,null,null);
//                        Intent intent = new Intent(context,EditorActivity.class);
//                        intent.putExtra("saleID",1);
//                        intent.putExtra("Sold",quantity);
//                        intent.setData(currentUri);
//                        context.startActivity(intent);
                        }
                    });
         }

    }