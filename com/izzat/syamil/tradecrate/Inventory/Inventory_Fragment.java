package com.izzat.syamil.tradecrate.Inventory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.izzat.syamil.tradecrate.MainActivity;
import com.izzat.syamil.tradecrate.R;
import model.InventoryItem;
import model.Tradeable;
import model.Trader;
import util.InventoryAdapter;

import java.util.ArrayList;

public class Inventory_Fragment extends Fragment implements View.OnClickListener{

    private RecyclerView inventoryRecyclerV;
    private ArrayList<Tradeable> myTradeables;
    private DatabaseReference dbRef;
    private InventoryAdapter inventoryAdapter;
    private InventoryAdapter.InventoryViewHolder inventoryViewHolder;
    private TextView i_title;
    private ChipGroup statusChips;
    private ImageButton remove_item;
    private ValueEventListener dbListener;
    private FirebaseUser mAuth;
    private ArrayList<InventoryItem> myInventory = new ArrayList<>();
    private Trader currentTrader;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.inventory_fragment_layout, container, false);

        i_title = view.findViewById(R.id.inventoryTitle);

        inventoryRecyclerV = view.findViewById(R.id.inventory_recyclerV);
        //inventoryRecyclerV.setHasFixedSize(true);
        inventoryRecyclerV.setLayoutManager(new LinearLayoutManager(getActivity()));

        //myInventory = new ArrayList<>();
        inventoryAdapter = new InventoryAdapter(getActivity(), myInventory);
        inventoryRecyclerV.setAdapter(inventoryAdapter);


        mAuth = FirebaseAuth.getInstance().getCurrentUser();

        dbRef = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid()).child("inventory");
        dbListener = dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                myInventory.clear();

                try{
                    if(dataSnapshot.hasChildren()){

                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            InventoryItem i_Item = new InventoryItem(ds.child("item_name").getValue().toString());
                            i_Item.setItemDescription(ds.child("description").getValue().toString());
                            i_Item.setItemImageUrl(ds.child("image").getValue().toString());
                            i_Item.setLocation(ds.child("location").getValue().toString());
                            i_Item.setAvailability(ds.child("availability").getValue().toString().contains("true"));
                            i_Item.setKey(ds.getKey());

                            myInventory.add(i_Item);
                        }

                    }

                }   catch (NullPointerException e){
                    e.printStackTrace();
                }

                ((MainActivity)getActivity()).setMyInventory(myInventory);
                inventoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(getView(), databaseError.getMessage(), Snackbar.LENGTH_SHORT ).setAnchorView(R.id.addItemFAB).show();
            }
        });
        //Log.e("check array", String.valueOf(myInventory.size()));
        return view;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbRef.removeEventListener(dbListener);
    }
}
