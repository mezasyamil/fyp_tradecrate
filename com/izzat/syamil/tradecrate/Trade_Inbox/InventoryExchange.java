package com.izzat.syamil.tradecrate.Trade_Inbox;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.izzat.syamil.tradecrate.MainActivity;
import com.izzat.syamil.tradecrate.R;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import model.InventoryItem;
import model.TradeSession;
import model.Tradeable;
import util.ExchangeItemAdapter;
import util.ExpectingItemAdapter;
import util.FireDatabase;

import java.util.ArrayList;

public class InventoryExchange extends AppCompatActivity implements View.OnClickListener {

    private TradeSession tradeSession;
    private ImageView itemViewer;
    private RecyclerView otherTraderRecycler, myExpectingRecyclerV, myOfferings;
    private ArrayList<Tradeable> otherExpectedItems = new ArrayList<>(), myExpectedItems = new ArrayList<>(), tradeOut = new ArrayList<>();
    private String currentUser, fromWhichActivity = "", otherUser = "";
    private TextView receivedItem, overallTradeStatus;
    private ExpectingItemAdapter otherAdapter, myAdapter;
    private ExchangeItemAdapter myOfferingsAdptr;
    private AppCompatButton rescheduleButton, cancelButton;
    private boolean otherAllReceived = false, myAllReceived = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_exchange);

        tradeSession = getIntent().getExtras().getParcelable("trade ready");
        fromWhichActivity = getIntent().getExtras().getString("from which activity");
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        otherUser = tradeSession.getOtherTraderInfo().getTraderID();

        //buttons
        rescheduleButton = findViewById(R.id.rescheduleButton);
        cancelButton = findViewById(R.id.cancelTrade);

        overallTradeStatus = findViewById(R.id.tradeStatus);

        findViewById(R.id.backButton).setOnClickListener(this);

        try{

            TextView tradeDate = findViewById(R.id.whenDisp);
            tradeDate.setText(tradeSession.getDateAppointed());

            TextView tradeTime = findViewById(R.id.timeDisp);
            tradeTime.setText(tradeSession.getTimeAppointed());

            TextView tradeLocation = findViewById(R.id.locationDisp);
            tradeLocation.setText(tradeSession.getLocation());

            itemViewer = findViewById(R.id.itemDisplayer);
            Picasso.with(this)
                    .load(tradeSession.getTradeIn().get(0).getItemImageURL())
                    .centerCrop()
                    .fit()
                    .into(itemViewer);

            receivedItem = findViewById(R.id.itemReceivedStatus);

            CircleImageView otherTraderPic = findViewById(R.id.otherTraderPhoto);
            Picasso.with(this)
                    .load(tradeSession.getOtherTraderInfo().getImageUrl())
                    .fit()
                    .into(otherTraderPic);

            TextView otherTraderLabel = findViewById(R.id.otherTraderBasketIndicator);
            otherTraderLabel.setText(tradeSession.getOtherTraderInfo().getTraderName() + " is expecting:");

            //set other user details
            otherTraderRecycler = findViewById(R.id.otherTraderExpecting);
            otherTraderRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, true));

                otherAdapter = new ExpectingItemAdapter(this, otherExpectedItems);
                otherTraderRecycler.setAdapter(otherAdapter);

                otherAdapter.setOnItemClick(new ExpectingItemAdapter.OnItemClick() {
                    @Override
                    public void onItemClick(int position) {

                        Picasso.with(InventoryExchange.this)
                                .load(otherExpectedItems.get(position).getTradeable().getItemImageURL())
                                .centerCrop()
                                .fit()
                                .into(itemViewer);

                        receivedItem.setText(otherExpectedItems.get(position).hasReceived()? "RECEIVED": "YET TO RECEIVED");

                    }
                });

            myOfferings = findViewById(R.id.recyclerViewMyOfferings);
            myOfferings.setLayoutManager(new LinearLayoutManager(this));

            myOfferingsAdptr = new ExchangeItemAdapter(this, tradeOut);
            myOfferings.setAdapter(myOfferingsAdptr);

            for(InventoryItem i : tradeSession.getTradeOut()){

                final Tradeable temp = new Tradeable(

                        tradeSession.getOtherTraderInfo().getTraderID(),
                        tradeSession.getOtherTraderInfo().getTraderName(),
                        tradeSession.getOtherTraderInfo().getImageUrl()

                );

                temp.setTradeable(i);
                otherExpectedItems.add(temp);

                        new FireDatabase().getUserRef().child(currentUser).child("trade_inbox").child(tradeSession.getTradeKey())
                                .child("your_offering").child(i.getKey()).child("hasReceived").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                try{
                                    if(dataSnapshot.getValue().equals(false)){

                                        tradeOut.add(temp);

                                    }

                                }   catch (NullPointerException e){
                                    e.printStackTrace();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

            }

            otherAdapter.notifyDataSetChanged();
            myOfferingsAdptr.notifyDataSetChanged();


            //}

            //set current user expected items
            myExpectingRecyclerV = findViewById(R.id.recyclerReceiving);
            myExpectingRecyclerV .setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

            myAdapter = new ExpectingItemAdapter(this, myExpectedItems);
            myExpectingRecyclerV .setAdapter(myAdapter);
            myAdapter.setOnItemClick(new ExpectingItemAdapter.OnItemClick() {
                @Override
                public void onItemClick(int position) {

                    Picasso.with(InventoryExchange.this)
                            .load(myExpectedItems.get(position).getTradeable().getItemImageURL())
                            .centerCrop()
                            .fit()
                            .into(itemViewer);

                    receivedItem.setText(myExpectedItems.get(position).hasReceived()? "RECEIVED": "YET TO RECEIVED");

                }
            });

            for(InventoryItem i : tradeSession.getTradeIn()){

                Tradeable temp = new Tradeable(

                        tradeSession.getOtherTraderInfo().getTraderID(),
                        tradeSession.getOtherTraderInfo().getTraderName(),
                        tradeSession.getOtherTraderInfo().getImageUrl()

                );

                temp.setTradeable(i);
                myExpectedItems.add(temp);

            }

            myAdapter.notifyDataSetChanged();


        }   catch (NullPointerException e){
            e.printStackTrace();

        }

        prepareForTradingAction();
        checkReceivedYet();
    }

    private void prepareForTradingAction(){

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                //otherExpectedItems.get(viewHolder.getLayoutPosition()).setReceived(true);
                //tradeOut.remove(viewHolder.getLayoutPosition());

                //Log.d("uwu", tradeSession.getOtherTraderInfo().getTraderID());

                try{
                    new FireDatabase().getUserRef().child(otherUser).child("trade_inbox")
                            .child(tradeSession.getTradeKey())
                            .child("their_offering")
                            .child(otherExpectedItems.get(viewHolder.getLayoutPosition()).getTradeable().getKey())
                            .child("hasReceived")
                            .setValue(true);

                    new FireDatabase().getUserRef().child(currentUser).child("trade_inbox")
                            .child(tradeSession.getTradeKey())
                            .child("your_offering")
                            .child(otherExpectedItems.get(viewHolder.getLayoutPosition()).getTradeable().getKey())
                            .child("hasReceived")
                            .setValue(true);

                    tradeOut.remove(viewHolder.getLayoutPosition());
                    myOfferingsAdptr.notifyDataSetChanged();

                }   catch (NullPointerException e){
                    e.printStackTrace();

                }


            }
        }).attachToRecyclerView(myOfferings);

    }

    private void checkReceivedYet(){

        try{

            //check if current user and the other has received the promised items
            new FireDatabase().getCurrentUserRef().child("trade_inbox")
                    .child(tradeSession.getTradeKey())
                    .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    for(Tradeable t: myExpectedItems){

                        t.setReceived(

                                dataSnapshot.child("their_offering").child(t.getTradeable().getKey()).child("hasReceived").getValue().equals(true)

                        );

                        receivedItem.setText(myExpectedItems.get(0).hasReceived()? "RECEIVED": "YET TO RECEIVED");

                        if(t.hasReceived()){
                            myAllReceived = true;
                            rescheduleButton.setVisibility(View.INVISIBLE);
                            cancelButton.setVisibility(View.INVISIBLE);


                        }   else    {
                            myAllReceived = false;

                        }

                    }

                    for(Tradeable t: otherExpectedItems){

                        try{
                            t.setReceived(
                                    dataSnapshot.child("your_offering").child(t.getTradeable().getKey()).child("hasReceived")
                                            .getValue().equals(true)
                            );


                        }   catch (NullPointerException e){
                            e.printStackTrace();
                        }


                        if(t.hasReceived()){
                            otherAllReceived = true;

                        }   else    {
                            otherAllReceived = false;

                        }
                    }

                    if(myAllReceived && otherAllReceived){

                        overallTradeStatus.setText("TRADE COMPLETED");
                        new FireDatabase().getCurrentUserRef().child("trade_inbox").child(tradeSession.getTradeKey()).child("status")
                                .setValue("Trade Successful");

                        new FireDatabase().getUserRef().child(otherUser).child("trade_inbox").child(tradeSession.getTradeKey()).child("status")
                                .setValue("Trade Successful");

                    }

                    myAdapter.notifyDataSetChanged();
                    otherAdapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }   catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backButton:
                if(fromWhichActivity.equals("trade inbox fragment")){
                    finish();

                }   else    {
                    startActivity(new Intent(this, MainActivity.class));

                }
                break;
        }
    }

}
