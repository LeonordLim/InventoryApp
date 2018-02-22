package com.lestariinterna.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lestariinterna.inventoryapp.data.InventoryContract;
import com.lestariinterna.inventoryapp.data.InventoryDbHelper;

/**
 * Created by AllinOne on 31/01/2018.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mItems;

    private EditText mPrice;

    private EditText mQuantity;

    private Uri currentUri;

    private boolean mItemHasChanged = false;

    // To access our database, we instantiate our subclass of SQLiteOpenHelper
    // and pass the context, which is the current activity.
    private InventoryDbHelper mDbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        currentUri = intent.getData();

        //if the intent Does not  contain a pet URI then we know we are creating a new item
        if(currentUri== null){
            // This is a new pet, so change the app bar to say "Add an item"
            setTitle(R.string.editor_activity_title_Add_item);

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete an item that hasn't been created yet.)
            invalidateOptionsMenu();


        }else {
            setTitle(R.string.editor_activity_title_Edit_itme);
             getLoaderManager().initLoader(0,null,this);
        }

        mItems = (EditText)findViewById(R.id.editTextNameItem);
        mPrice = (EditText)findViewById(R.id.editTextHarga);
        mQuantity=(EditText)findViewById(R.id.editTextQuantity);


        // OnTouchListener that listens for any user touches on a View, implying that they are modifying
        // the view, and we change the mPetHasChanged boolean to true.
        mItems.setOnTouchListener(mTouchListener);
        mPrice.setOnTouchListener(mTouchListener);
        mQuantity.setOnTouchListener(mTouchListener);


        mDbHelper = new InventoryDbHelper(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.menu_editor,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()){
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                saveItem();
                finish();
                return true;
            case R.id.action_delete:
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    private void saveItem(){

        // Read from input fields
        // Use trim to eliminate leading or trailing white space

        String itemNameString = mItems.getText().toString().trim();
        String priceString = mPrice.getText().toString().trim();
        int price = Integer.parseInt(priceString);

        String quantityString = mQuantity.getText().toString().trim();
        int quantity = Integer.parseInt(quantityString);

        Log.v("Test data",itemNameString+price+quantityString);
        if (currentUri == null && TextUtils.isEmpty(itemNameString)&& TextUtils.isEmpty(priceString)&& TextUtils.isEmpty(quantityString)){
            // Since no fields were modified, we can return early without creating a new item.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and items's  attributes are the values.
        ContentValues values = new ContentValues();
        values.put(InventoryContract.InvEntry.COLUMN_INVENTORY_ITEMS,itemNameString);
        values.put(InventoryContract.InvEntry.COLUMN_INVENTORY_PRICE, price);
        values.put(InventoryContract.InvEntry.COLUMN_INVENTORY_QUANTITY,quantity);


        if(currentUri == null){

            Uri mUri = getContentResolver().insert(InventoryContract.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful
            if (mUri == null) {
                // If the row ID is -1, then there was an error with insertion.
                Toast.makeText(this, "Error with saving Inventory", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Item save with row ", Toast.LENGTH_SHORT).show();
            }


        }else {

            // Otherwise this is an EXISTING iyrm, so update the item with content URI: mCurrentPetUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentPetUri will already identify the correct row in the database that
            // we want to modify.
            Log.v("test uri", currentUri.toString());
            int rowsAffected = getContentResolver().update(currentUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public EditorActivity() {
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

        return  new CursorLoader(this,currentUri,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor==null|| cursor.getCount()<1){
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int itemColumnIndex = cursor.getColumnIndex(InventoryContract.InvEntry.COLUMN_INVENTORY_ITEMS);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InvEntry.COLUMN_INVENTORY_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InvEntry.COLUMN_INVENTORY_QUANTITY);

            // Extract out the value from the Cursor for the given column index
            String item = cursor.getString(itemColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity= cursor.getInt(quantityColumnIndex);

            mItems.setText(item);
            mPrice.setText(Integer.toString(price));
            mQuantity.setText(Integer.toString(quantity));



        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
    // the view, and we change the mPetHasChanged boolean to true.

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    @Override
    public void onBackPressed() {

        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

}
