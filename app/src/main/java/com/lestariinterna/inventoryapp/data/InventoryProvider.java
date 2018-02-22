package com.lestariinterna.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.lestariinterna.inventoryapp.data.InventoryDbHelper.LOG_TAG;

/**
 * Created by AllinOne on 09/02/2018.
 */

public class InventoryProvider extends ContentProvider {

    private InventoryDbHelper mDbHelper;

    private static final int INVENTORY= 100;
    private static final int INVENTORY_ID= 101;


    // Creates a UriMatcher object.
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {

        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,InventoryContract.PATH_INVENTORY,INVENTORY);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,InventoryContract.PATH_INVENTORY+"/#",INVENTORY_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return insertItems(uri,contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supportedfor "+uri);
            }
    }

    private Uri insertItems(Uri uri,ContentValues contentValues){

        // Check that the item name is not null
        String itemName = contentValues.getAsString(InventoryContract.InvEntry.COLUMN_INVENTORY_ITEMS);
        if (itemName == null) {
            throw new IllegalArgumentException("Item's name not inserted");
        }


        Integer price = contentValues.getAsInteger(InventoryContract.InvEntry.COLUMN_INVENTORY_PRICE);
        if (price!= null && price < 0) {
            throw new IllegalArgumentException("Price is needed");
        }

        Integer quantity = contentValues.getAsInteger(InventoryContract.InvEntry.COLUMN_INVENTORY_QUANTITY);
        if (quantity!= null && quantity < 0) {
            throw new IllegalArgumentException("Need stocks");
        }


//        // Check that the item name is not null
//        int price = contentValues.getAsInteger(InventoryContract.InvEntry.COLUMN_INVENTORY_ITEMS);
//        if (price == 0) {
//            throw new IllegalArgumentException("Item's name not inserted");
//        }
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new item
        long id = database.insert(InventoryContract.InvEntry.TABLE_NAME,null,contentValues);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;}

        return ContentUris.withAppendedId(uri,id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        //Tracking the number of rows being deleted
        int rowsDeleted;
        switch (match) {
            case INVENTORY:

                rowsDeleted = database.delete(InventoryContract.InvEntry.TABLE_NAME,selection, selectionArgs);
               // return rowsDeleted;
                break;
            case INVENTORY_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = INVENTORY_ID+ "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(InventoryContract.InvEntry.TABLE_NAME, selection, selectionArgs);
                //return rowsDeleted;
                break;
            default:
                throw new IllegalArgumentException("Delete is not supported for " + uri);
        }
        if (rowsDeleted != 0){
          getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsDeleted;
    }


    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return updateInventory(uri, contentValues, selection, selectionArgs);
            case INVENTORY_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = INVENTORY_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateInventory(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateInventory(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){
        // Check that the item name is not null
        String itemName = contentValues.getAsString(InventoryContract.InvEntry.COLUMN_INVENTORY_ITEMS);
        Log.v("Test itemnam",itemName);
        if (itemName == null) {
            throw new IllegalArgumentException("Item's name not inserted");
        }


        Integer price = contentValues.getAsInteger(InventoryContract.InvEntry.COLUMN_INVENTORY_PRICE);
        if (price!= null && price < 0) {
            throw new IllegalArgumentException("Price is needed");
        }

        Integer quantity = contentValues.getAsInteger(InventoryContract.InvEntry.COLUMN_INVENTORY_QUANTITY);
        if (quantity!= null && quantity < 0) {
            throw new IllegalArgumentException("Need stocks");
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(InventoryContract.InvEntry.TABLE_NAME,contentValues,selection,selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of database rows affected by the update statement
        return rowsUpdated;



    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return InventoryContract.InvEntry.CONTENT_LIST_TYPE;
            case INVENTORY_ID:
                return InventoryContract.InvEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match){
            case INVENTORY:
                cursor = database.query(InventoryContract.InvEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case INVENTORY_ID:
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = InventoryContract.InvEntry._ID+"=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(InventoryContract.InvEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI "+ uri);

        }
        return cursor;
    }
}
