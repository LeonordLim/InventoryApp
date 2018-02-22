package com.lestariinterna.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by AllinOne on 10/01/2018.
 */

public final class InventoryContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private InventoryContract(){}

    /**
     * Inner class that defines constant values for the inventory database table.
     * Each entry in the table represents a single item.
     */

    public static final class InvEntry implements BaseColumns{


        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;


        /* Item's name for the inventory table
         */
        public static String TABLE_NAME= "inventory";

        /** Unique ide, for the entry, type= Integer */
        public static String _ID = BaseColumns._ID;


        /**
         * Name of the items
         *
         *  Type: TEXT
         *
         * */
        public static String COLUMN_INVENTORY_ITEMS = "items";

        /**
         * Items Price
         * Type: INTEGER
         */
        public static String COLUMN_INVENTORY_PRICE = "price";

        /**
         * Number of items in stock
         *
         * Type: INTEGER
         */
        public static String COLUMN_INVENTORY_QUANTITY = "quantity";

    }
    /*
          Content Authority to identify the provider, which has the same value with the android manifest
        */
    public static final String CONTENT_AUTHORITY = "com.lestariinterna.inventoryapp";


    /*
    create the BASE_CONTENT_URI which will be shared by every URI associated with Inv/contract:
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    /*
    This constants stores the path for each of the tables which will be appended to the base content URI.
     */
    public static final String PATH_INVENTORY = "inventory";


    /*
    reate a full URI for the class as a constant called CONTENT_URI. The Uri.withAppendedPath() method appends the BASE_CONTENT_URI
    (which contains the scheme and the content authority) to the path segment.
     */
    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);



}
