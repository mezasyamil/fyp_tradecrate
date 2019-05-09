package com.izzat.syamil.tradecrate.Trade_Inbox;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.izzat.syamil.tradecrate.R;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import model.InventoryItem;
import model.TradeSession;
import util.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;

public class TradeRequestView extends AppCompatActivity implements View.OnClickListener, android.app.TimePickerDialog.OnTimeSetListener {

    private TradeSession tradingInstance;
    private TextView tName;
    private CircleImageView tProfile;
    private ArrayList<InventoryItem> tradeInItems, tradeOutItems, tradeInBeforeChanges, tradeOutBeforeChanges;
    private PlaceTradeAdapter tradeInAdapter, tradeOutAdapter;
    private RecyclerView tradeInRecyclerV, tradeOutRecyclerV;
    private ImageButton addMoreTradeIn, addMoreTradeOut, backButton;
    private AppCompatButton declineOrCancel, acceptButton, updateConfirm;
    private TextView calInput, timeInput, locationInput, tradeStatus;
    private Spinner tMethod;
    private String currentTraderID = "", otherTraderID = "", time, date, location;
    private boolean anyChanges = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_trade);

        tradingInstance = getIntent().getExtras().getParcelable("view trade session");

        currentTraderID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if(tradingInstance.getTInitiatorID().equals(currentTraderID)){
            otherTraderID = tradingInstance.getTReceiverID();

        }   else    {
            otherTraderID = tradingInstance.getTInitiatorID();

        }

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(this);

        tName = findViewById(R.id.traderName);
        tProfile = findViewById(R.id.traderProfile);

        tradeStatus = findViewById(R.id.tradeStatus);
        tradeStatus.setVisibility(View.VISIBLE);
        setCurrentTradeStatus();

        try{
            //Log.e("checkArray", tradingInstance.getTradeOut().get(0).getItemName());
            tName.setText(tradingInstance.getOtherTraderInfo().getTraderName());

            Picasso.with(this)
                    .load(tradingInstance.getOtherTraderInfo().getImageUrl())
                    .into(tProfile);

            //set trade details
            date = tradingInstance.getDateAppointed();
            time = tradingInstance.getTimeAppointed();
            location = tradingInstance.getLocation();


                    //set & display trading-in item/s

                        //these are to check changes made from user
                        tradeInBeforeChanges = new ArrayList<>();
                        tradeInBeforeChanges = tradingInstance.getTradeIn();

                        tradeInItems = new ArrayList<>();
                        tradeInItems = tradingInstance.getTradeIn();
                        tradeInAdapter = new PlaceTradeAdapter(this, tradeInItems );

                    //attach adapter to designated recyclerView
                        tradeInRecyclerV = findViewById(R.id.tradeInRView);
                        tradeInRecyclerV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
                        tradeInRecyclerV.setAdapter(tradeInAdapter);

                    //add more trade-ins
                        addMoreTradeIn = findViewById(R.id.addMoreTradeIn);
                        addMoreTradeIn.setOnClickListener(this);

                    //get view holder
                    //viewHolderTradeIn = tradeInAdapter.getViewHolder();


            //set & display trading-out item/s

                //these are to check changes made from user
                tradeInBeforeChanges = new ArrayList<>();
                tradeInBeforeChanges = tradingInstance.getTradeIn();

                tradeOutItems = new ArrayList<>();
                tradeOutItems = tradingInstance.getTradeOut();
                tradeOutAdapter = new PlaceTradeAdapter(this, tradeOutItems);


            //attach adapter to recyclerView for trade-out
                tradeOutRecyclerV = findViewById(R.id.tradeOutRView);
                tradeOutRecyclerV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
                tradeOutRecyclerV.setAdapter(tradeOutAdapter);

                addMoreTradeOut = findViewById(R.id.addMoreTradeOut);
                addMoreTradeOut.setOnClickListener(this);

            //get view holder
            //viewHolderTradeOut = tradeOutAdapter.getViewHolder();


                    //setCalendarInput
                        FrameLayout calButton = findViewById(R.id.calFrame);
                        calButton.setOnClickListener(this);
                        calInput = findViewById(R.id.calText);
                        calInput.setText(tradingInstance.getDateAppointed());

                    //setTimeInput
                        FrameLayout timeButton = findViewById(R.id.timeFrame);
                        timeButton.setOnClickListener(this);
                        timeInput = findViewById(R.id.timeDisp);
                        timeInput.setText(tradingInstance.getTimeAppointed());

                    //setLocationInput
                        FrameLayout locationButton = findViewById(R.id.locationFrame);
                        locationButton.setOnClickListener(this);
                        locationInput = findViewById(R.id.locationDisp);
                        locationInput.setText(tradingInstance.getLocation());

                    //set trade method input
                    tMethod = findViewById(R.id.spinner);
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }



        //action buttons
        declineOrCancel = findViewById(R.id.discardTrade);
        declineOrCancel.setOnClickListener(this);

                acceptButton = findViewById(R.id.placeTrade);
                acceptButton.setOnClickListener(this);

        updateConfirm = findViewById(R.id.makeChangesButton);
        updateConfirm.setOnClickListener(this);

                if(tradingInstance.getTInitiatorID().equals(currentTraderID)
                        && tradingInstance.getWaitingForReplyID().equals(currentTraderID)){

                    declineOrCancel.setText("Cancel Request");
                    acceptButton.setVisibility(View.INVISIBLE);
                    //updateConfirm.setVisibility(View.INVISIBLE);


                    }   else if(tradingInstance.getTReceiverID().equals(currentTraderID)
                            && !tradingInstance.getWaitingForReplyID().equals(currentTraderID))    {

                        declineOrCancel.setText("Decline");
                        acceptButton.setText("Agree");
                        //updateConfirm.setVisibility(View.VISIBLE);


                }   else if(tradingInstance.getTInitiatorID().equals(currentTraderID)
                        && !tradingInstance.getWaitingForReplyID().equals(currentTraderID)){

                    declineOrCancel.setText("Cancel Request");
                    acceptButton.setVisibility(View.INVISIBLE);
                    //updateConfirm.setVisibility(View.VISIBLE);


                    }   else if(tradingInstance.getTReceiverID().equals(currentTraderID)
                            && tradingInstance.getWaitingForReplyID().equals(currentTraderID)){

                        declineOrCancel.setText("Decline");
                        acceptButton.setText("Agree");
                        //updateConfirm.setVisibility(View.INVISIBLE);

                    }   else if(tradingInstance.getTReceiverID().equals(currentTraderID)
                        && tradeStatus.equals("Request ")){

                }

        checkAnyChanges();

    }

    @Override
    public void onClick(View v) {

        Bundle bundle = new Bundle();
        DialogAddMore newDialog = new DialogAddMore();

        if(tradingInstance.getTInitiatorID().equals(currentTraderID)){

            switch (v.getId()){

                case R.id.makeChangesButton:
                    //send acknowledgement to other trader that this user wanted to make changes of the requested trade instance
                    break;

                case R.id.back_button:
                    finish();
                    break;

                        case R.id.discardTrade:
                            tradingInstance.setCurrentStatus(TradeSession.TRADE_STATUS[3]);
                            tradeStatus.setText("REQUEST CANCELLED");
                            break;

                case R.id.placeTrade:
                    //prompt sender whether new updates are coincide
                    break;

                        case R.id.addMoreTradeOut:

                            bundle.putString("trader id", currentTraderID);
                            bundle.putString("fromActivity", "tradeRequestView");
                            newDialog.setArguments(bundle);
                            newDialog.show(getSupportFragmentManager(), "addMore");

                            break;

                case R.id.addMoreTradeIn:

                    bundle.putString("trader id", otherTraderID);
                    bundle.putString("trader name", tradingInstance.getOtherTraderInfo().getTraderName());
                    bundle.putString("fromActivity", "tradeRequestView");
                    newDialog.setArguments(bundle);
                    newDialog.show(getSupportFragmentManager(), "addMore");

                    break;

                        case R.id.calFrame:
                            bundle.putString("fromActivity", "tradeRequestView");
                            newDialog.setArguments(bundle);
                            new CalendarDialog().show(getSupportFragmentManager(), "calendar");
                            break;

                case R.id.timeFrame:
                  /*  bundle.putString("fromActivity", "tradeRequestView");
                    newDialog.setArguments(bundle);*/
                    new TimePickerDialog().show(getSupportFragmentManager(), "time picker");
                    break;

                        case R.id.locationFrame:
                            bundle.putString("fromActivity", "tradeRequestView");
                            newDialog.setArguments(bundle);
                            new LocationDialog().show(getSupportFragmentManager(), "location input");
                            break;
            }

        }   else if(tradingInstance.getTReceiverID().equals(currentTraderID)){

            switch (v.getId()){

                case R.id.makeChangesButton:
                    break;

                case R.id.back_button:
                    finish();

                        case R.id.discardTrade:
                            //decline trade request
                            tradingInstance.setCurrentStatus(TradeSession.TRADE_STATUS[2]);
                            break;

                case R.id.placeTrade:

                    //proceed to items exchange
                    new FireDatabase().setTradingStatus("Request Accepted", tradingInstance.getTradeKey(), otherTraderID );
                    Intent i = new Intent(this, InventoryExchange.class);
                    i.putExtra("trade ready", tradingInstance);
                    i.putExtra("from which activity", "trade request view" );
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);

                    break;

                        case R.id.addMoreTradeOut:

                            bundle.putString("trader id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            bundle.putString("fromActivity", "tradeRequestView");
                            newDialog.setArguments(bundle);
                            newDialog.show(getSupportFragmentManager(), "addMore");

                            break;

                case R.id.addMoreTradeIn:

                    bundle.putString("trader id", otherTraderID);
                    bundle.putString("trader name", tradingInstance.getOtherTraderInfo().getTraderName());
                    bundle.putString("fromActivity", "tradeRequestView");
                    newDialog.setArguments(bundle);
                    newDialog.show(getSupportFragmentManager(), "addMore");


                    break;

                        case R.id.calFrame:
                            bundle.putString("fromActivity", "tradeRequestView");
                            CalendarDialog calendarDialog = new CalendarDialog();
                            calendarDialog.setArguments(bundle);
                            calendarDialog.show(getSupportFragmentManager(), "calendar");
                            break;

                case R.id.timeFrame:

                    new TimePickerDialog().show(getSupportFragmentManager(), "time picker");
                    break;

                        case R.id.locationFrame:
                            bundle.putString("fromActivity", "tradeRequestView");
                            LocationDialog locationDialog = new LocationDialog();
                            locationDialog.setArguments(bundle);
                            locationDialog.show(getSupportFragmentManager(), "location input");
                            break;
            }

        }
    }

    private void setCurrentTradeStatus(){

        if(tradingInstance.getCurrentTradeStatus().equals("Request Pending")){
            tradeStatus.setText(tradingInstance.getCurrentTradeStatus().toUpperCase());

        }   else if(tradingInstance.getCurrentTradeStatus().equals("Request Accepted")){
            tradeStatus.setText(tradingInstance.getCurrentTradeStatus().toUpperCase());

        }   else if(tradingInstance.getCurrentTradeStatus().equals("Request Declined")){
            tradeStatus.setText(tradingInstance.getCurrentTradeStatus().toUpperCase());

        }   else if(tradingInstance.getCurrentTradeStatus().equals("Request Cancelled")){
            tradeStatus.setText(tradingInstance.getCurrentTradeStatus().toUpperCase());

        }   else if(tradingInstance.getCurrentTradeStatus().equals("Request Changes")){
            tradeStatus.setText(tradingInstance.getCurrentTradeStatus().toUpperCase());

        }   else if(tradingInstance.getCurrentTradeStatus().equals("Trade Pending")){
            tradeStatus.setText(tradingInstance.getCurrentTradeStatus().toUpperCase());

        }   else if(tradingInstance.getCurrentTradeStatus().equals("Trading")){
            tradeStatus.setText(tradingInstance.getCurrentTradeStatus().toUpperCase());

        }   else if(tradingInstance.getCurrentTradeStatus().equals("Trade Successful")){
            tradeStatus.setText(tradingInstance.getCurrentTradeStatus().toUpperCase());

        }   else if(tradingInstance.getCurrentTradeStatus().equals("Trade Cancelled")){
            tradeStatus.setText(tradingInstance.getCurrentTradeStatus().toUpperCase());

        }
    }

    public ArrayList<InventoryItem> getInBasketTradeIns(){
        return tradeInItems;
    }

            public ArrayList<InventoryItem> getInBasketTradeOuts() {return  tradeOutItems;}

    public void addTradeInsFromDialog(InventoryItem tInItem){
        tradeInItems.add(tInItem);
        tradeInAdapter.notifyDataSetChanged();
        checkAnyChanges();
    }

            public void addTradeOutsFromDialog(InventoryItem tOutItem){
                tradeOutItems.add(tOutItem);
                tradeOutAdapter.notifyDataSetChanged();
                checkAnyChanges();
            }


    public void setPickedDate(int d, int m, int y){

        LocalDate picked = LocalDate.of(y, Month.of(m), d);
        this.date = picked.toString();
        calInput.setText(picked.toString());
        checkAnyChanges();
        //calInput.setText(String.format("%02d-%02d-%04d", day , month , year ));
    }

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timeInput.setText(String.format("%02d:%02d", hourOfDay, minute));
                this.time = timeInput.getText().toString();
                checkAnyChanges();
            }

    public void setLocationInput(String location){
        this.location = location;
        locationInput.setText(location);
        checkAnyChanges();
    }

        public String getLocation(){
            return location;
        }


    public void checkAnyChanges(){


        if( !tradingInstance.getDateAppointed().equals(date) ){

                anyChanges = true;

            }   else if( !tradingInstance.getTimeAppointed().equals(time) ) {

                anyChanges = true;

            }   else if( !tradingInstance.getLocation().equals(location) ) {

                anyChanges = true;

            }


        if(anyChanges){
            updateConfirm.setVisibility(View.VISIBLE);
        }

    }

    public void setChangesMade(){

        anyChanges = true;

    }

    private void updateChanges(){

    }

    // fetch changes made by other trader
    private void listenerForUpdates(){

    /*    DatabaseReference dbRef = new FireDatabase().getCurrentUserRef().child("trade_inbox");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds: dataSnapshot.getChildren()){

                    for(DataSnapshot ds1: ds.getChildren()){

                        new TradeSession()

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

    }
}
