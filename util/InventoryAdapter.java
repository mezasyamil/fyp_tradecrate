package util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.izzat.syamil.tradecrate.InventoryView;
import com.izzat.syamil.tradecrate.R;
import com.squareup.picasso.Picasso;
import model.InventoryItem;
import model.Tradeable;

import java.util.ArrayList;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> implements View.OnClickListener, ChipGroup.OnCheckedChangeListener {
    private Context context;
    private Activity activity;
    private View viewer;
    private ArrayList<InventoryItem> myInventory = new ArrayList<>();
    private InventoryViewHolder inventoryViewHolder;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users")
            .child(FirebaseAuth.getInstance()
                    .getCurrentUser().getUid())
            .child("inventory");
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("tradeables_upload");

    public InventoryAdapter(Context context, ArrayList<InventoryItem> myTradeables){
        this.context = context;
        this.myInventory = myTradeables;
    }

    public class InventoryViewHolder extends RecyclerView.ViewHolder {
        public TextView textName;
        public ImageView item_image;
        public ImageButton remove_item;
        public ChipGroup statusChip;
        public Chip availableChip, outOfStockChip;
        public CardView cardView;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.disp_name);
            item_image = itemView.findViewById(R.id.i_image);
            remove_item = itemView.findViewById(R.id.remove_inventory);
            statusChip = itemView.findViewById(R.id.chipGroup);
            availableChip = statusChip.findViewById(R.id.available);
            outOfStockChip = statusChip.findViewById(R.id.out_of_stock);
            cardView = itemView.findViewById(R.id.card_inventory);
        }

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCheckedChanged(ChipGroup group, int checkedId) {

    }


    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        viewer = LayoutInflater.from(context).inflate(R.layout.inventory_card, parent, false);
        inventoryViewHolder = new InventoryViewHolder(viewer);
        return inventoryViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final InventoryViewHolder holder, final int position) {
        //final InventoryItem inventoryItem = inventoryItems.get(position);
        final InventoryItem tItem = myInventory.get(position);
        holder.textName.setText(tItem.getItemName());

        holder.remove_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(viewer.getContext()).setTitle("Removing " + tItem.getItemName().toLowerCase() + " from your inventory?")
                        .setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                deleteInventory(position);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        if(tItem.isAvailable()){
            holder.availableChip.setChecked(true);

        }   else  if(!tItem.isAvailable())  {
            holder.outOfStockChip.setChecked(true);
        }

        holder.statusChip.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {

                if(!holder.availableChip.isChecked()){
                    holder.availableChip.setChecked(false);
                    holder.outOfStockChip.setChecked(true);
                }

                statusChange(checkedId, tItem);
            }
        });


        Picasso.with(context)
                .load(tItem.getItemImageURL())
                .placeholder(R.drawable.ic_insert_photo_black_24dp)
                .into(holder.item_image);

        holder.cardView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Tradeable chosenItem = new Tradeable(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                                        FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                                                        FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
                chosenItem.setTradeable(tItem);
                Intent intent = new Intent(context, InventoryView.class);
                intent.putExtra("view selected item",chosenItem );
                context.startActivity(intent);
            }
        });

    }

    private void deleteInventory(final int position){
        final InventoryItem selectedItem = myInventory.get(position);
        final String selectedKey = selectedItem.getKey();

        storageReference = storageReference.getStorage().getReferenceFromUrl(selectedItem.getItemImageURL());
/*        final DatabaseReference databaseReference = new FireDatabase().getInventoryDatabaseRef();
        StorageReference storageReference = new FirebaseStorageUtil().getInventoryStorageRef().getStorage().getReferenceFromUrl(selectedItem.getItemImageURL());*/

        try{
            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    databaseReference.child(selectedKey).removeValue();
                    myInventory.remove(position);
                    notifyItemRemoved(position);
                    Snackbar.make(viewer, selectedItem.getItemName() + " has been removed." , Snackbar.LENGTH_SHORT).setAnchorView(R.id.addItemFAB).show();
                    //Toast.makeText(context, selectedItem.getItemName() + " has been removed.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }

    public void statusChange(int checkedID, InventoryItem i_item){
        if(checkedID == R.id.available){
            i_item.setAvailability(true);
            databaseReference.child(i_item.getKey()).child("availability").setValue(i_item.isAvailable());

        }
        else if(checkedID == R.id.out_of_stock){
            i_item.setAvailability(false);
            databaseReference.child(i_item.getKey()).child("availability").setValue(i_item.isAvailable());

        }

    }

    public InventoryViewHolder getInventoryViewHolder() {
        return inventoryViewHolder;
    }

    @Override
    public int getItemCount() {
        //return myInventory.equals(null)? 0: myInventory.size();
        Log.e("size check", String.valueOf(myInventory.size()));
        return myInventory.size();
    }
}
