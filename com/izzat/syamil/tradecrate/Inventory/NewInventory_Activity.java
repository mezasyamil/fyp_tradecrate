package com.izzat.syamil.tradecrate.Inventory;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.izzat.syamil.tradecrate.R;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import model.InventoryItem;

public class NewInventory_Activity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE_REQUEST = 1;

    private FirebaseUser current_user;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef, inventoryRef;

    private ImageButton close;
    private CircleImageView upload_frame;
    private Uri upload_uri;
    private TextInputLayout name_input_layout;
    private EditText name_input, description_input, location_input;
    private FloatingActionButton place_trade;
    private StorageTask upload_task;
    private InventoryItem new_inventory;
    private ProgressBar uploadStatus;
    private String newToken = "";
    private boolean tokenExist = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_inventory);

       //mStorageRef = FirebaseStorage.getInstance().getReference().child("Inventory").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
       //mDatabaseRef = FirebaseDatabase.getInstance().getReference("Trader").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Inventory");

        current_user = FirebaseAuth.getInstance().getCurrentUser();

        mStorageRef = FirebaseStorage.getInstance().getReference("tradeables_uploads")
                .child(current_user.getUid());

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users")
                .child(current_user.getUid());

        inventoryRef = mDatabaseRef.child("inventory");

        //checkToken

            mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                    if( !dataSnapshot.hasChild("token") ||
                            dataSnapshot.child("token").equals("") && dataSnapshot.hasChild("token" ) ){

                        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                            @Override
                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                newToken = instanceIdResult.getToken();
                                //tokenExist = false;
                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

       close = findViewById(R.id.close_new_inventory);
       close.setOnClickListener(this);

        upload_frame = findViewById(R.id.imageButton);
        upload_frame.setOnClickListener(this);

        name_input_layout = findViewById(R.id.textInputLayout);
        name_input = findViewById(R.id.i_nameInput);
        if(name_input.isActivated()){
            name_input_layout.setError("Item name is compulsory.");
        }
        description_input = findViewById(R.id.i_descriptionInput);
        location_input = findViewById(R.id.i_locationInput);

        uploadStatus = findViewById(R.id.progressBar2);
        uploadStatus.setVisibility(View.INVISIBLE);

        place_trade = findViewById(R.id.acceptInventoryFAB);
        place_trade.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.imageButton:
                openFileImage();
                break;

            case R.id.acceptInventoryFAB:
                if(!name_input.getText().toString().isEmpty()){

                    new_inventory = new InventoryItem(name_input.getText().toString());
                    if(!description_input.equals(null)){
                        new_inventory.setItemDescription(description_input.getText().toString());
                    }
                    if(!location_input.equals(null)){
                        new_inventory.setLocation(location_input.getText().toString());
                    }
                    uploadNewInventory();
                }
                else    {
                    Toast.makeText(this, "Item name is a must.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.close_new_inventory:
                finish();
        }
    }

    private void openFileImage(){
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            upload_uri = data.getData();
            Picasso.with(this)
                    .load(upload_uri)
                    .fit()
                    .centerCrop()
                    .into(upload_frame);
        }
    }

    private String getUrlExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadNewInventory(){

        if(upload_uri != null){

            final StorageReference newRef = mStorageRef.child(new_inventory.getItemName().trim() + "." + getUrlExtension(upload_uri));
            place_trade.setVisibility(View.INVISIBLE);
            uploadStatus.setVisibility(View.VISIBLE);
           /* Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    uploadStatus.setProgress(0);
                }
            }, 0);*/

            newRef.putFile(upload_uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if(!task.isSuccessful()){
                        Toast.makeText(NewInventory_Activity.this, "Unable to add" + new_inventory.getItemName() + " into inventory.", Toast.LENGTH_LONG ).show();
                        throw task.getException();
                    }

                    double progress = 100.0*task.getResult().getBytesTransferred()/ task.getResult().getTotalByteCount();
                    uploadStatus.setProgress((int)progress);
                    return newRef.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        new_inventory.setItemImageUrl(task.getResult().toString());
                        String inventory_ID = inventoryRef.push().getKey();

                        if(!newToken.isEmpty() || !newToken.equals("")){
                            mDatabaseRef.child("token").setValue(newToken);
                        }
                        //mDatabaseRef.child(inventory_ID).child("trader_id").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

                        inventoryRef.child(inventory_ID).child("item_name").setValue(new_inventory.getItemName());
                        inventoryRef.child(inventory_ID).child("description").setValue(new_inventory.getItemDescription());
                        inventoryRef.child(inventory_ID).child("image").setValue(new_inventory.getItemImageURL());
                        inventoryRef.child(inventory_ID).child("location").setValue(new_inventory.getItemLocation());
                        inventoryRef.child(inventory_ID).child("availability").setValue(new_inventory.isAvailable());

                        Toast.makeText(NewInventory_Activity.this, new_inventory.getItemName() + " has been successfully added into inventory.", Toast.LENGTH_SHORT)
                                .show()
                        ;
                        /*Snackbar.make(findViewById(R.id.main_page), new_inventory.getItemName() + "has been successfully added into inventory.", Snackbar.LENGTH_SHORT)
                                .setAnchorView(R.id.acceptInventoryFAB)
                                .show();*/
                        finish();

                    }   else    {

                        //Toast.makeText(NewInventory_Activity.this, new_inventory.getItemName() + "failed to be added into inventory.", Toast.LENGTH_SHORT);
                        Snackbar.make(findViewById(R.id.newInventoryLayout), new_inventory.getItemName() + "failed to be added into inventory.", Snackbar.LENGTH_SHORT)
                                .setAnchorView(R.id.acceptInventoryFAB)
                                .show();
                    }
                }
            });

        }   else {

            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();

        }
    }
}
