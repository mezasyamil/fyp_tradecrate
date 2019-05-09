package com.izzat.syamil.tradecrate.Trade_Inbox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.izzat.syamil.tradecrate.MainActivity;
import com.izzat.syamil.tradecrate.R;
import model.InventoryItem;
import model.TradeSession;
import model.Trader;
import util.FireDatabase;
import util.TradeInboxAdapter;

import java.util.ArrayList;

public class TradeInboxFragment extends Fragment {

    private ArrayList<InventoryItem> myInventories = new ArrayList<>();
    private ArrayList<TradeSession> tradeSessions = new ArrayList<>();
    private ArrayList<Trader> otherTraderInfo = new ArrayList<>();
    private FireDatabase dbUtil = new FireDatabase();
    private ValueEventListener dbListener;
    private DatabaseReference dbRef;
    private TradeInboxAdapter tiAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.trade_inbox_fragment_layout, container, false);

        //pass the fetched session to adapter
       /* tradeSessions = new ArrayList<>();
        RecyclerView tiRecyclerView = v.findViewById(R.id.iInbox_RecV);
        tiRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        tiAdapter = new TradeInboxAdapter(v.getContext(), tradeSessions);
        tiRecyclerView.setAdapter(tiAdapter);*/


        //pass the fetched session to adapter
        tradeSessions = new ArrayList<>();
        RecyclerView tiRecyclerView = v.findViewById(R.id.iInbox_RecV);
        tiRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        tiAdapter = new TradeInboxAdapter(v.getContext(), tradeSessions);
        tiRecyclerView.setAdapter(tiAdapter);

        myInventories = new ArrayList<>();
        dbRef = dbUtil.getUserRef();
        //getMyTradeInbox();

        dbListener = dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                tradeSessions.clear();

                try{

                    String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    String otherUserID = "";

                    DataSnapshot currentUserDSnap = dataSnapshot.child(currentUserID);
                    DataSnapshot myTradeInboxDSnap = currentUserDSnap.child("trade_inbox");

                    for(DataSnapshot ds: myTradeInboxDSnap.getChildren()){

                        TradeSession fetchedSession = new TradeSession();
                        Trader otherTrader = new Trader();

                        fetchedSession.setKey(ds.getKey());

                        if(ds.hasChild("initiator_id")){
                            fetchedSession.settInitiatorID(ds.child("initiator_id").getValue().toString());
                            otherUserID = fetchedSession.getTInitiatorID();

                            fetchedSession.settReceiverID(currentUserID);

                            otherTrader.setTraderID(fetchedSession.getTInitiatorID());

                            otherTrader.setTraderName(dataSnapshot.child(fetchedSession.getTInitiatorID())
                                    .child("google_name").getValue().toString());

                            otherTrader.setImageURL(dataSnapshot.child(fetchedSession.getTInitiatorID())
                                    .child("google_profile_picture").getValue().toString());

                            //get my inventories from inventory_fragment fetched
                            myInventories =((MainActivity)getActivity()).getMyInventories();

                            for(DataSnapshot ds2: ds.child("your_offering").getChildren()){

                               /* for(InventoryItem i : myInventories){
                                    if(i.getKey().equals(ds2.getKey())){
                                        i.setQuantityAsString(ds2.child("quantity").getValue().toString());
                                    }
                                }
                                fetchedSession.setTradeOut( myInventories );*/

                                InventoryItem tempItem = new InventoryItem();
                                tempItem.setKey(ds2.getKey());
                                tempItem.setQuantityAsString(ds2.child("quantity").getValue().toString());

                                //get other trader's offering items picture, etc.
                                DataSnapshot myInventoryDS = dataSnapshot
                                        .child(currentUserID)
                                        .child("inventory").child(tempItem.getKey());

                                tempItem.setAvailability(myInventoryDS.child("availability").getValue().equals(true));
                                tempItem.setItemDescription(myInventoryDS.child("description").getValue().toString());
                                tempItem.setItemImageUrl(myInventoryDS.child("image").getValue().toString());
                                tempItem.setItemName(myInventoryDS.child("item_name").getValue().toString());
                                tempItem.setLocation(myInventoryDS.child("location").getValue().toString());

                                fetchedSession.addTradeOut( tempItem );

                            }

                            //get other trader offerings
                            for(DataSnapshot ds1: ds.child("their_offering").getChildren()){


                                InventoryItem tempItem = new InventoryItem();
                                tempItem.setKey(ds1.getKey());
                                tempItem.setQuantityAsString(ds1.child("quantity").getValue().toString());

                                //get other trader's offering items picture, etc.
                                DataSnapshot otherInventoryDS = dataSnapshot.child(fetchedSession.getTInitiatorID())
                                        .child("inventory").child(tempItem.getKey());

                                tempItem.setAvailability(otherInventoryDS.child("availability").getValue().equals(true));
                                tempItem.setItemDescription(otherInventoryDS.child("description").getValue().toString());
                                tempItem.setItemImageUrl(otherInventoryDS.child("image").getValue().toString());
                                tempItem.setItemName(otherInventoryDS.child("item_name").getValue().toString());
                                tempItem.setLocation(otherInventoryDS.child("location").getValue().toString());

                                fetchedSession.addTradeIn( tempItem );

                            }


                        }   else   if(ds.hasChild("receiver_id")) {
                            fetchedSession.settReceiverID(ds.child("receiver_id").getValue().toString());
                            otherUserID = fetchedSession.getTInitiatorID();
                            fetchedSession.settInitiatorID(currentUserID);

                            otherTrader.setTraderID(fetchedSession.getTReceiverID());

                            otherTrader.setTraderName(dataSnapshot.child(fetchedSession.getTReceiverID())
                                    .child("google_name").getValue().toString());

                            otherTrader.setImageURL(dataSnapshot.child(fetchedSession.getTReceiverID())
                                    .child("google_profile_picture").getValue().toString());

                            //myInventories =((MainActivity)getActivity()).getMyInventories();

                            for(DataSnapshot ds2: ds.child("your_offering").getChildren()){

                             /*   for(InventoryItem i : myInventories){
                                    if(i.getKey().equals(ds2.getKey())){
                                        i.setQuantityAsString(ds2.child("quantity").getValue().toString());
                                    }
                                }*/

                                InventoryItem tempItem = new InventoryItem();
                                tempItem.setKey(ds2.getKey());
                                tempItem.setQuantityAsString(ds2.child("quantity").getValue().toString());

                                //get other trader's offering items picture, etc.
                                DataSnapshot myInventoryDS = dataSnapshot
                                        .child(currentUserID)
                                        .child("inventory").child(tempItem.getKey());

                                tempItem.setAvailability(myInventoryDS.child("availability").getValue().equals(true));
                                tempItem.setItemDescription(myInventoryDS.child("description").getValue().toString());
                                tempItem.setItemImageUrl(myInventoryDS.child("image").getValue().toString());
                                tempItem.setItemName(myInventoryDS.child("item_name").getValue().toString());
                                tempItem.setLocation(myInventoryDS.child("location").getValue().toString());

                                fetchedSession.addTradeOut( tempItem );

                            }

                            //get other trader offerings
                            for(DataSnapshot ds1: ds.child("their_offering").getChildren()){

                                InventoryItem tempItem = new InventoryItem();
                                tempItem.setKey(ds1.getKey());
                                tempItem.setQuantityAsString(ds1.child("quantity").getValue().toString());

                                //get other trader's offering items picture, etc.
                                DataSnapshot otherInventoryDS = dataSnapshot
                                        .child(fetchedSession.getTReceiverID())
                                        .child("inventory").child(tempItem.getKey());

                                tempItem.setAvailability(otherInventoryDS.child("availability").getValue().equals(true));
                                tempItem.setItemDescription(otherInventoryDS.child("description").getValue().toString());
                                tempItem.setItemImageUrl(otherInventoryDS.child("image").getValue().toString());
                                tempItem.setItemName(otherInventoryDS.child("item_name").getValue().toString());
                                tempItem.setLocation(otherInventoryDS.child("location").getValue().toString());

                                fetchedSession.addTradeIn( tempItem );
                            }

                        }
                        fetchedSession.setOtherTraderInfo(otherTrader);

                        //get status, method, time, venue, etc..
                        fetchedSession.setCurrentStatus(ds.child("status").getValue().toString());
                        fetchedSession.setTradeMethod(ds.child("trade_method").getValue().toString());
                        fetchedSession.setTradeInformations(ds.child("when").getValue().toString(),
                                ds.child("time").getValue().toString(),
                                ds.child("trading_location").getValue().toString());

                        fetchedSession.setTraderWaitingForReply(ds.child("isWaitingForReply").getValue().equals(true)? currentUserID: otherUserID );

                        tradeSessions.add(fetchedSession);
                    }


                }   catch (NullPointerException e){
                    e.printStackTrace();
                }

                tiAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return v;
    }

    /*private void getMyTradeInbox(){

        dbListener = dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                tradeSessions.clear();

                try{

                    Log.e("test test", dataSnapshot.toString());

                    TradeSession fetchedSession = new TradeSession();
                    Trader otherTrader = new Trader();
                    String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    DataSnapshot currentUserDSnap = dataSnapshot.child(currentUserID);
                    DataSnapshot tradeInboxDSnap = currentUserDSnap.child("trade_inbox");

                    for(DataSnapshot ds: tradeInboxDSnap.getChildren()){

                        fetchedSession.setKey(ds.getKey());
                        if(ds.hasChild("initiator_id")){
                            fetchedSession.settInitiatorID(ds.child("initiator_id").getValue().toString());
                            fetchedSession.settReceiverID(currentUserID);

                            otherTrader.setTraderName(dataSnapshot.child(fetchedSession.getTInitiatorID())
                                    .child("google_name").getValue().toString());

                            otherTrader.setImageURL(dataSnapshot.child(fetchedSession.getTInitiatorID())
                                    .child("google_profile_picture").getValue().toString());

                            //get my inventories from inventory_fragment fetched
                            myInventories =((MainActivity)getActivity()).getMyInventories();

                            for(DataSnapshot ds2: ds.child("your_offering").getChildren()){

                                for(InventoryItem i : myInventories){
                                    if(i.getKey().equals(ds2.getKey())){
                                        i.setQuantityAsString(ds2.child("quantity").getValue().toString());
                                    }
                                }
                                fetchedSession.setTradeIn( myInventories );
                            }

                            //get other trader offerings
                            for(DataSnapshot ds1: ds.child("their_offering").getChildren()){


                                InventoryItem tempItem = new InventoryItem();
                                tempItem.setKey(ds1.getKey());
                                tempItem.setQuantityAsString(ds1.child("quantity").getValue().toString());

                                    //get other trader's offering items picture, etc.
                                    DataSnapshot otherInventoryDS = dataSnapshot.child(fetchedSession.getTInitiatorID())
                                            .child("inventory").child(tempItem.getKey());

                                    tempItem.setAvailability(otherInventoryDS.child("availability").getValue().equals(true));
                                    tempItem.setItemDescription(otherInventoryDS.child("description").getValue().toString());
                                    tempItem.setItemImageUrl(otherInventoryDS.child("image").getValue().toString());
                                    tempItem.setItemName(otherInventoryDS.child("item_name").getValue().toString());
                                    tempItem.setLocation(otherInventoryDS.child("location").getValue().toString());

                                fetchedSession.addTradeOut( tempItem );

                            }


                        }   else   if(ds.hasChild("receiver_id")) {
                            fetchedSession.settReceiverID(ds.child("receiver_id").getValue().toString());
                            fetchedSession.settInitiatorID(currentUserID);

                            otherTrader.setTraderName(dataSnapshot.child(fetchedSession.getTReceiverID())
                                    .child("google_name").getValue().toString());

                            otherTrader.setImageURL(dataSnapshot.child(fetchedSession.getTReceiverID())
                                    .child("google_profile_picture").getValue().toString());

                            myInventories =((MainActivity)getActivity()).getMyInventories();

                            for(DataSnapshot ds2: ds.child("your_offering").getChildren()){

                                for(InventoryItem i : myInventories){
                                    if(i.getKey().equals(ds2.getKey())){
                                        i.setQuantityAsString(ds2.child("quantity").getValue().toString());
                                    }
                                }
                                fetchedSession.setTradeOut( myInventories );
                            }


                            //get other trader offerings
                            for(DataSnapshot ds1: ds.child("their_offering").getChildren()){


                                InventoryItem tempItem = new InventoryItem();
                                tempItem.setKey(ds1.getKey());
                                tempItem.setQuantityAsString(ds1.child("quantity").getValue().toString());

                                //get other trader's offering items picture, etc.
                                DataSnapshot otherInventoryDS = dataSnapshot.child(fetchedSession.getTInitiatorID())
                                        .child("inventory").child(tempItem.getKey());

                                tempItem.setAvailability(otherInventoryDS.child("availability").getValue().equals(true));
                                tempItem.setItemDescription(otherInventoryDS.child("description").getValue().toString());
                                tempItem.setItemImageUrl(otherInventoryDS.child("image").getValue().toString());
                                tempItem.setItemName(otherInventoryDS.child("item_name").getValue().toString());
                                tempItem.setLocation(otherInventoryDS.child("location").getValue().toString());

                                fetchedSession.addTradeIn( tempItem );

                            }

                        }
                        fetchedSession.setOtherTraderInfo(otherTrader);

                        //get status, method, time, venue, etc..
                        fetchedSession.setCurrentStatus(ds.child("status").getValue().toString());
                        fetchedSession.setTradeMethod(ds.child("trade_method").getValue().toString());
                        fetchedSession.setTradeInformations(ds.child("when").getValue().toString(),
                                ds.child("time").getValue().toString(),
                                ds.child("trading_location").getValue().toString());

                        tradeSessions.add(fetchedSession);
                    }


                }   catch (NullPointerException e){
                    e.printStackTrace();
                }

                tiAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbRef.removeEventListener(dbListener);
    }
}
