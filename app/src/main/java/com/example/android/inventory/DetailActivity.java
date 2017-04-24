package com.example.android.inventory;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.inventory.data.InventoryContract;

import static com.example.android.inventory.R.drawable.ic_add_a_photo_black_24dp;

public class DetailActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    private static final int SELECT_PICTURE = 100;

    private static final int INVENTORY_LOADER = 1;
    private static final String mProjection[] = {InventoryContract.MobileEntry._ID, InventoryContract.MobileEntry.COLUMN_NAME_MOBILE_NAME, InventoryContract.MobileEntry.COLUMN_NAME_MOBILE_PRICE, InventoryContract.MobileEntry.COLUMN_NAME_MOBILE_QUANTITY, InventoryContract.MobileEntry.COLUMN_NAME_MOBILE_IMAGE, InventoryContract.MobileEntry.COLUMN_NAME_SUPPLIER_CONTACT};

    private ImageView mEditImageView;
    private TextView mEditNameView;
    private TextView mEditPriceView;
    private TextView mEditQuantityView;
    private TextView mEditContactView;
    private Button mTrackSaleButton;
    private Button mReceiveShipmentButton;

    private Uri mImageUri;
    private Uri mCurrentUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mEditImageView = (ImageView) findViewById(R.id.product_image);
        mEditNameView = (TextView) findViewById(R.id.edit_product_name);
        mEditPriceView = (TextView) findViewById(R.id.edit_product_price);
        mEditQuantityView = (TextView) findViewById(R.id.edit_product_quantity);
        mEditContactView = (TextView) findViewById(R.id.edit_supplier_contact);
        mTrackSaleButton = (Button) findViewById(R.id.track_sale);
        mReceiveShipmentButton = (Button) findViewById(R.id.receive_shipment);

        Intent startingIntent = getIntent();
        mCurrentUri = startingIntent.getData();

        // new entry
        if(mCurrentUri == null){
            setTitle(R.string.add_product);
            invalidateOptionsMenu();

            mTrackSaleButton.setVisibility(View.GONE);
            mReceiveShipmentButton.setVisibility(View.GONE);

            mEditImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, SELECT_PICTURE);
                }
            });
        }
        // old entry
        else {
            setTitle(R.string.display_product);

            mEditNameView.setKeyListener(null);
            mEditPriceView.setKeyListener(null);
            mEditQuantityView.setKeyListener(null);
            mEditContactView.setKeyListener(null);

            getLoaderManager().initLoader(INVENTORY_LOADER, null, DetailActivity.this);
        }

        mTrackSaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showModifyQuantityDialog("decrease");
            }
        });

        mReceiveShipmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showModifyQuantityDialog("increase");
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                // Get the url from data
                mImageUri = data.getData();
                if (null != mImageUri) {
                    Log.d(LOG_TAG, "Image Uri : " + mImageUri);
                    // Set the image in ImageView
                    mEditImageView.setImageURI(mImageUri);
                }
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem addItem = menu.findItem(R.id.action_add);
        MenuItem deleteItem = menu.findItem(R.id.action_delete);
        MenuItem orderMoreItem = menu.findItem(R.id.action_order_more);

        if(mCurrentUri == null) {
            deleteItem.setVisible(false);
            orderMoreItem.setVisible(false);
        }
        else
            addItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_add was selected
            case R.id.action_add:
                if(validateInput())
                    insertMobile();
                return true;
            // action with ID action_delete was selected
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                break;
            // action with ID action_order_more was selected
            case R.id.action_order_more:
                launchPhoneOrEmail();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        switch (loaderId){
            case INVENTORY_LOADER:
                return new CursorLoader(DetailActivity.this, mCurrentUri, mProjection, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(InventoryContract.MobileEntry.COLUMN_NAME_MOBILE_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.MobileEntry.COLUMN_NAME_MOBILE_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.MobileEntry.COLUMN_NAME_MOBILE_QUANTITY);
            int imageColumnIndex = cursor.getColumnIndex(InventoryContract.MobileEntry.COLUMN_NAME_MOBILE_IMAGE);
            int contactColumnIndex = cursor.getColumnIndex(InventoryContract.MobileEntry.COLUMN_NAME_SUPPLIER_CONTACT);

            mEditNameView.setText(cursor.getString(nameColumnIndex));
            mEditPriceView.setText(cursor.getString(priceColumnIndex));
            mEditQuantityView.setText(cursor.getString(quantityColumnIndex));
            mEditContactView.setText(cursor.getString(contactColumnIndex));
            Uri imageUri = Uri.parse(cursor.getString(imageColumnIndex));
            mEditImageView.setImageURI(imageUri);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        int id = getResources().getIdentifier("com.example.android.inventory:drawable/" + ic_add_a_photo_black_24dp, null, null);
        mEditImageView.setImageResource(id);
        mEditNameView.setText("");
        mEditPriceView.setText("");
        mEditQuantityView.setText("");
        mEditContactView.setText("");
    }

    /**
     * Helper method to display dialog to take the sale/shipment quantity
     */
    private void showModifyQuantityDialog(final String modification) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        if(modification.equals("increase"))
            builder.setTitle(R.string.dialog_title_increase_quantity);
        else
            builder.setTitle(R.string.dialog_title_decrease_quantity);
        builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                String text = input.getText().toString();
                dialog.dismiss();
                updateMobileQuantity(modification, Integer.parseInt(text));
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null)
                    dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * Helper method to display delete dialog
     */
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteMobile();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Helper method to saved mobile details
     */
    private void insertMobile(){

        String nameString = mEditNameView.getText().toString().trim();
        String priceString = mEditPriceView.getText().toString().trim();
        String quantityString = mEditQuantityView.getText().toString().trim();
        String contactString = mEditContactView.getText().toString().trim();

        ContentValues contentValues = new ContentValues();
        contentValues.put(InventoryContract.MobileEntry.COLUMN_NAME_MOBILE_NAME, nameString);
        contentValues.put(InventoryContract.MobileEntry.COLUMN_NAME_MOBILE_PRICE, Integer.parseInt(priceString));
        contentValues.put(InventoryContract.MobileEntry.COLUMN_NAME_MOBILE_QUANTITY, Integer.parseInt(quantityString));
        contentValues.put(InventoryContract.MobileEntry.COLUMN_NAME_MOBILE_IMAGE, mImageUri.toString());
        contentValues.put(InventoryContract.MobileEntry.COLUMN_NAME_SUPPLIER_CONTACT, contactString);

        Uri newUri = getContentResolver().insert(InventoryContract.MobileEntry.CONTENT_URI, contentValues);

        if (newUri == null)
            Toast.makeText(DetailActivity.this, R.string.mobile_insert_failure,
                    Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(DetailActivity.this, R.string.mobile_insert_success,
                    Toast.LENGTH_SHORT).show();

        //close the activity
        finish();
    }

    /**
     * Helper method to update mobile quantity via the two buttons
     * @param modification whether the quantity should be "increase"/"decrease"
     * @param count holds the number by how much the quantity should be increased/decreased
     */
    private void updateMobileQuantity(String modification, int count){
        String quantityString = mEditQuantityView.getText().toString().trim();

        ContentValues contentValues = new ContentValues();

        if(modification.equals("increase"))
            contentValues.put(InventoryContract.MobileEntry.COLUMN_NAME_MOBILE_QUANTITY, Integer.parseInt(quantityString) + count);
        else
            contentValues.put(InventoryContract.MobileEntry.COLUMN_NAME_MOBILE_QUANTITY, Integer.parseInt(quantityString) - count);

        int rowsUpdated = getContentResolver().update(mCurrentUri, contentValues, null, null);

        if (rowsUpdated == 0)
            Toast.makeText(DetailActivity.this, R.string.mobile_updated_failure,
                    Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(DetailActivity.this, R.string.mobile_updated_success,
                    Toast.LENGTH_SHORT).show();

        //close the activity
        finish();
    }

    /**
     * Helper method to delete an entry from the database
     */
    private void deleteMobile() {
        int rowsDeleted = getContentResolver().delete(mCurrentUri, null, null);

        if (rowsDeleted == 0)
            Toast.makeText(DetailActivity.this, R.string.mobile_deleted_failure,
                    Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(DetailActivity.this, R.string.mobile_deleted_success,
                    Toast.LENGTH_SHORT).show();

        //close the activity
        finish();
    }

    /**
     * Helper method to validate the input provided before saving to the database
     * @return true if the inputs are fine else false
     */
    private boolean validateInput() {
        if(mImageUri == null) {
            Toast.makeText(DetailActivity.this, R.string.image_missing,
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if(mEditNameView.getText().toString().trim().equals("")) {
            mEditNameView.setError("Name Missing");
            return false;
        }

        if(mEditPriceView.getText().toString().trim().equals("")) {
            mEditPriceView.setError("Price Missing");
            return false;
        }

        if(mEditQuantityView.getText().toString().trim().equals("")) {
            mEditQuantityView.setError("Quantity Missing");
            return false;
        }

        if(mEditContactView.getText().toString().trim().equals("")) {
            mEditContactView.setError("Contact Info Missing");
            return false;
        }

        return true;
    }

    /**
     * Helper method to launch phone app or email app based on the information stored in the database
     */
    private void launchPhoneOrEmail() {
        String contact = mEditContactView.getText().toString().trim();

        if (contact.contains("@") || contact.contains(".com")) {

            //Intent to send email
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:")); // only email apps should handle this
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {contact});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Order Required");
            if (emailIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(emailIntent);
            }
        } else {

            //Intent to make call
            Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
            phoneIntent.setData(Uri.parse("tel:" + contact));
            if (phoneIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(phoneIntent);
            }
        }
    }
}