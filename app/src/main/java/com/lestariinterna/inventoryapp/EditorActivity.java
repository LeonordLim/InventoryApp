package com.lestariinterna.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.lestariinterna.inventoryapp.data.InventoryContract;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by AllinOne on 31/01/2018.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mItems;

    private EditText mPrice;

    private EditText mQuantity;

    private Uri currentUri;

    private ImageView mPicture;

    private boolean mItemHasChanged = false;

    private String mCurrentPhotoPath;

    private Uri OutPutUri;





//    // To access our database, we instantiate our subclass of SQLiteOpenHelper
//    // and pass the context, which is the current activity.
//    private InventoryDbHelper mDbHelper;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted
        if(OutPutUri!=null) {
            Log.v("Test Uri", OutPutUri.toString());
            String Uri = OutPutUri.toString();
            outState.putString("OutPutUri", Uri);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


//        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
//            mItemHasChanged = true;
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            mPicture.setImageBitmap(imageBitmap);
//        }

        switch(requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if(resultCode == RESULT_OK){
                    mItemHasChanged = true;
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    mPicture.setImageBitmap(imageBitmap);;

                    // Create an image file name
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String imageFileName = "JPEG_" + timeStamp + "_";
                    File file = new File(Environment.getExternalStorageDirectory()+File.separator + imageFileName+".jpg");
                    try {
                        file.createNewFile();
                        FileOutputStream fo = new FileOutputStream(file);
                        //5
                        fo.write(bytes.toByteArray());
                        fo.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    OutPutUri = Uri.fromFile(file);
                    Log.v("TestUrifromFile",OutPutUri.toString());
                }

                break;
            case REQUEST_IMAGE_GALLERY:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    mPicture.setImageURI(selectedImage);
                }
                break;
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.containsKey("OutPutUri")  && !savedInstanceState.getString("OutPutUri").equals("")){
            OutPutUri = Uri.parse(savedInstanceState.getString("OutPutUri"));
            Log.v("Test Uri",OutPutUri.toString());
            mPicture.setImageURI(OutPutUri);
        };
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        //instantiate edit text components
        mItems = (EditText)findViewById(R.id.editTextNameItem);
        mPrice = (EditText)findViewById(R.id.editTextHarga);
        mQuantity=(EditText)findViewById(R.id.editTextQuantity);
        mPicture = (ImageView)findViewById(R.id.imageViewItem);
        Button mButtonCam = (Button)findViewById(R.id.buttonCamera);
        Button mButtonGall = (Button)findViewById(R.id.buttonGallery);

        Intent intent = getIntent();
        currentUri = intent.getData();
        //if the intent Does not  contain an Item URI then we know we are creating a new item
        if(currentUri== null){
            // This is a new item, so change the app bar to say "Add an item"
            setTitle(R.string.editor_activity_title_Add_item);
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete an item that hasn't been created yet.)
            invalidateOptionsMenu();
        }else {
            //currentUri is not null means we editting item
            setTitle(R.string.editor_activity_title_Edit_item);
            getLoaderManager().initLoader(0,null,this);
        }



        // OnTouchListener that listens for any user touches on a View, implying that they are modifying
        // the view, and we change the mItemHasChanged boolean to true.
        mItems.setOnTouchListener(mTouchListener);
        mPrice.setOnTouchListener(mTouchListener);
        mQuantity.setOnTouchListener(mTouchListener);

        //take picture from camera
        mButtonCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();

            }
        });

        //Take picture from gallery
        mButtonGall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getGalleryIntent();
            }
        });
//        mDbHelper = new InventoryDbHelper(this);

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
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(this);
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
        String photoUriString = OutPutUri.toString();

        String quantityString = mQuantity.getText().toString().trim();
        int quantity = Integer.parseInt(quantityString);

        Log.v("Test data",itemNameString+price+quantityString);
        if (currentUri == null && TextUtils.isEmpty(itemNameString)&& TextUtils.isEmpty(priceString)&& TextUtils.isEmpty(quantityString)&& TextUtils.isEmpty(photoUriString)){
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
        values.put(InventoryContract.InvEntry.COLUMN_INVENTORY_PICTURE,photoUriString);


        if(currentUri == null){

            Uri mUri = getContentResolver().insert(InventoryContract.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful
            if (mUri == null) {
                // If mUri is null then there was an error with insertion.
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
            InventoryContract.InvEntry.COLUMN_INVENTORY_QUANTITY,
            InventoryContract.InvEntry.COLUMN_INVENTORY_PICTURE,

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
            int pictureColumnIndex = cursor.getColumnIndex(InventoryContract.InvEntry.COLUMN_INVENTORY_PICTURE);

            // Extract out the value from the Cursor for the given column index
            String item = cursor.getString(itemColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity= cursor.getInt(quantityColumnIndex);
            String image = cursor.getString(pictureColumnIndex);
            Uri imageUri = Uri.parse(image);

            mItems.setText(item);
            mPrice.setText(Integer.toString(price));
            mQuantity.setText(Integer.toString(quantity));
            mPicture.setImageURI(imageUri);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
    // the view, and we change the mItemHasChanged boolean to true.

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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (currentUri== null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the  item.
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        android.support.v7.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void deleteItem() {
        // Only perform the delete if this is an existing item.
        if (currentUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(currentUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
            // Close the activity
            finish();
        }
    }

    //Method for getting image from camera
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }


//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException e) {
//                // Error occurred while creating the File
//                Log.e("Problem ","Test");
//            }
//            if (photoFile != null) {
//                Uri OutPutUri = FileProvider.getUriForFile(this,
//                        "com.lestariinterna.fileprovider",
//                        photoFile);
//                Log.v("TestUri",OutPutUri.toString());
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, OutPutUri);
//                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//            }
//
//
//        }

    }

    //Method for getting image from gallery
    static final int REQUEST_IMAGE_GALLERY = 0;

    private void getGalleryIntent() {

        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , REQUEST_IMAGE_GALLERY);//one can be replaced with any action code
    }



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.v("TestMCurrentPhotoPath",mCurrentPhotoPath);
  return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
    private void setPic() {
        // Get the dimensions of the View
        int targetW = mPicture.getWidth();
        int targetH = mPicture.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        //bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mPicture.setImageBitmap(bitmap);
    }
}
