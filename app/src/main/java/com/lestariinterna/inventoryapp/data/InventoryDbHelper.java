package com.lestariinterna.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.lestariinterna.inventoryapp.data.InventoryContract.InvEntry;

/**
 * Created by AllinOne on 18/01/2018.
 *
 * Database helper for inventory app. Manages database creation and version management.
 */


public class InventoryDbHelper extends SQLiteOpenHelper{

    public static final String LOG_TAG= InventoryDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "inventory.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Construct a new instance (@Link InventoryDbHelper)
     * @param context of the app
     */
    public InventoryDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Execute the sql String to create table
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    /**
     * Creating String to create table
     */
    private  static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + InvEntry.TABLE_NAME + "("
                    +InvEntry._ID +" INTEGER PRIMARY KEY, "
                    +InvEntry.COLUMN_INVENTORY_ITEMS+" TEXT, "
                    +InvEntry.COLUMN_INVENTORY_PRICE+" INTEGER,"
                    +InvEntry.COLUMN_INVENTORY_QUANTITY+" INTEGER);";
}
