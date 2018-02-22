package com.lestariinterna.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lestariinterna.inventoryapp.data.InventoryContract;
import com.lestariinterna.inventoryapp.data.InventoryDbHelper;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // To access our database, we instantiate our subclass of SQLiteOpenHelper
    // and pass the context, which is the current activity.
    private InventoryDbHelper mDbHelper;

    private InventoryCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);


        ListView listView = (ListView)findViewById(R.id.list);

        adapter = new InventoryCursorAdapter(this,null);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri currentUri = ContentUris.withAppendedId(InventoryContract.CONTENT_URI,id);
                Intent intent = new Intent(CatalogActivity.this,EditorActivity.class);
                intent.setData(currentUri);
                startActivity(intent);
            }
        });



        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CatalogActivity.this,EditorActivity.class);
                startActivity(intent);
            }
        });
        mDbHelper = new InventoryDbHelper(this);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
       // displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the inventory database.
     */
    private void displayDatabaseInfo() {



        // Create and/or open a database to read from it
       // SQLiteDatabase db = mDbHelper.getReadableDatabase();.0

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[]projection= {
                InventoryContract.InvEntry._ID,
                InventoryContract.InvEntry.COLUMN_INVENTORY_ITEMS,
                InventoryContract.InvEntry.COLUMN_INVENTORY_PRICE,
                InventoryContract.InvEntry.COLUMN_INVENTORY_QUANTITY

        };

        // Perform a query on the pets table
       /** Cursor cursor = db.query(
                InventoryContract.InvEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                     // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );*/

        Log.v("Test CONTENT URI",InventoryContract.CONTENT_URI.toString());

        Cursor cursor = getContentResolver().query(InventoryContract.CONTENT_URI,projection,null,null,null,null);


        ListView listView = (ListView)findViewById(R.id.list);

        InventoryCursorAdapter inventoryCursorAdapter = new InventoryCursorAdapter(this,cursor);

        listView.setAdapter(inventoryCursorAdapter);

        // TextView displayView = (TextView) findViewById(R.id.displayDbInfo);


//        try {
//            // Display the number of rows in the Cursor (which reflects the number of rows
//
//            displayView.setText("Number of rows in inventory database table: " + cursor.getCount()+"\n\n");
//            displayView.append(
//                    "Below is the items in the inventory:"+"\n\n"+
//                    InventoryContract.InvEntry._ID +"-"+
//                    InventoryContract.InvEntry.COLUMN_INVENTORY_ITEMS+"-"+
//                    InventoryContract.InvEntry.COLUMN_INVENTORY_PRICE+"-"+
//                    InventoryContract.InvEntry.COLUMN_INVENTORY_QUANTITY+"\n"
//            );
//            // Figure out the index of each column
//            int idColumnIndex = cursor.getColumnIndex(InventoryContract.InvEntry._ID);
//            int itemsColumnIndex=cursor.getColumnIndex(InventoryContract.InvEntry.COLUMN_INVENTORY_ITEMS);
//            int priceColumnIndex=cursor.getColumnIndex(InventoryContract.InvEntry.COLUMN_INVENTORY_PRICE);
//            int quantityColumnIndex=cursor.getColumnIndex(InventoryContract.InvEntry.COLUMN_INVENTORY_QUANTITY);
//
//            // Iterate through all the returned rows in the cursor
//            while(cursor.moveToNext()){
//                // Use that index to extract the String or Int value of the word
//                // at the current row the cursor is on.
//                int currentId = cursor.getInt(idColumnIndex);
//                String currentItem=cursor.getString(itemsColumnIndex);
//                int currentPrice=cursor.getInt(priceColumnIndex);
//                int currentQuantity=cursor.getInt(quantityColumnIndex);
//                // Display the values from each column of the current row in the cursor in the TextView
//                displayView.append(("\n" + currentId + " - " +
//                        currentItem + " - " +
//                        currentPrice + " - " +
//                        currentQuantity ));
//
//            }
//
//        }finally {
//            // Always close the cursor when you're done reading from it. This releases all its
//            // resources and makes it invalid.
//            cursor.close();
//        }
    }

    /**
     * Creating a dropdown menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        /** Inflating the menu layout*/
        getMenuInflater().inflate(R.menu.menu_catalog,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         switch (item.getItemId()){
             case R.id.insertDummy:
                 insertDummy();
                 displayDatabaseInfo();
                 return true;
             case R.id.deleteItem:
                 return true;

         }
        return super.onOptionsItemSelected(item);
    }

    public CatalogActivity() {
        super();
    }

    String[]projection= {
            InventoryContract.InvEntry._ID,
            InventoryContract.InvEntry.COLUMN_INVENTORY_ITEMS,
            InventoryContract.InvEntry.COLUMN_INVENTORY_PRICE,
            InventoryContract.InvEntry.COLUMN_INVENTORY_QUANTITY

    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,InventoryContract.CONTENT_URI,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    private void insertDummy(){


        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(InventoryContract.InvEntry.COLUMN_INVENTORY_ITEMS,"Samsung J2");
        values.put(InventoryContract.InvEntry.COLUMN_INVENTORY_PRICE, 3000000);
        values.put(InventoryContract.InvEntry.COLUMN_INVENTORY_QUANTITY,100);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(InventoryContract.InvEntry.TABLE_NAME, null, values);



    }
}