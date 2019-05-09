package com.izzat.syamil.tradecrate.Home;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.*;
import com.izzat.syamil.tradecrate.R;
import model.InventoryItem;
import model.Tradeable;

import java.util.ArrayList;

public class Home_Fragment extends Fragment implements View.OnClickListener {

    private ArrayList<Tradeable> tradeables;
    private RecyclerView hRecyclerV;
    private BrowseAdapter bAdapter;
    private ValueEventListener dbListener;
    private DatabaseReference dbRef;
    private int gridColumns = 2;
    private BrowseAdapter.BrowseViewHolder browseViewHolder;
    private ArrayList<InventoryItem> myInventory;
    private Tradeable readTradeable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home_fragment_layout, container, false);

        tradeables = new ArrayList<>();
        hRecyclerV = v.findViewById(R.id.home_recyclerV);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            gridColumns = 3;
        }


        hRecyclerV.setLayoutManager(new GridLayoutManager(getActivity(), gridColumns));

        bAdapter = new BrowseAdapter(v.getContext(), tradeables);

        hRecyclerV.setAdapter(bAdapter);

        //readTradeable = new Tradeable();

        dbRef = FirebaseDatabase.getInstance().getReference("users");
        dbListener = dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                tradeables.clear();

                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    DataSnapshot inventory = ds.child("inventory");

                    for(DataSnapshot ds2: inventory.getChildren()){

                        try{
                            String checkAvailable = "";
                            checkAvailable = ds2.child("availability").getValue().toString();
                            if(checkAvailable.equals("true")){

                                InventoryItem temp = new InventoryItem();
                                Tradeable tradeable = new Tradeable(ds.getKey().toString(),
                                        ds.child("google_name").getValue().toString(),
                                        ds.child("google_profile_picture").getValue().toString());

                                temp.setAvailability(true);
                                temp.setKey(ds2.getKey());
                                temp.setItemName(ds2.child("item_name").getValue().toString());
                                temp.setLocation(ds2.child("location").getValue().toString());
                                temp.setItemImageUrl(ds2.child("image").getValue().toString());
                                temp.setItemDescription(ds2.child("description").getValue().toString());

                                tradeable.setTradeable(temp);
                                tradeables.add(tradeable);

                            }
                        }
                        catch (NullPointerException e){
                            e.printStackTrace();
                        }
                    }
                }
                //((MainActivity)getActivity()).setAllTradeables(tradeables);
                bAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(getView(), databaseError.getMessage(), Snackbar.LENGTH_SHORT ).setAnchorView(R.id.addItemFAB).show();
            }
        });

        return v;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("tradeables", tradeables);
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
