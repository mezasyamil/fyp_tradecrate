package com.izzat.syamil.tradecrate.Home;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.izzat.syamil.tradecrate.R;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import model.InventoryItem;
import model.TradeSession;
import model.Tradeable;
import util.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;

public class TradePlace extends AppCompatActivity implements View.OnClickListener, android.app.TimePickerDialog.OnTimeSetListener {

    private Tradeable interestedItem;
    private TradeSession readyBasket;
    protected ArrayList<InventoryItem> tradeInItems,tradeOutItems;
    private ArrayList<InventoryItem> myInventory;
    private PlaceTradeAdapter tradeInAdapter, tradeOutAdapter;
    private RecyclerView tradeInRecyclerV, tradeOutRecyclerV;
    private TextView tName, calInput, timeInput, locationInput;
    private ImageButton addMoreTradeIn, addMoreTradeOut;
    private CircleImageView tProfile;
    private TradeSession tradeSession;
    private int day = 0, month = 0, year = 0;
    private String location, time, otherTraderID, traderID, date;
    private boolean validDate = false;
    private Spinner tMethod;
    private PlaceTradeAdapter.PlaceTradeViewHolder viewHolderTradeIn, viewHolderTradeOut;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_trade);
        interestedItem = getIntent().getExtras().getParcelable("interested item");
        //myInventory = getIntent().getExtras().getParcelable("my inventories");

        //other trader name & image
        tName = findViewById(R.id.traderName);
        tName.setText(interestedItem.getTraderName());
        
        tProfile = findViewById(R.id.traderProfile);
        Picasso.with(this)
                .load(interestedItem.getTraderPicture())
                .into(tProfile);

        //action buttons
        findViewById(R.id.discardTrade).setOnClickListener(this);
        findViewById(R.id.placeTrade).setOnClickListener(this);
        findViewById(R.id.back_button).setOnClickListener(this);

        //set trader ids
        otherTraderID = interestedItem.getTraderID();
        traderID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //set & display trading-in item/s
        tradeInItems = new ArrayList<>();
        tradeInItems.add(interestedItem.getTradeable());
        tradeInAdapter = new PlaceTradeAdapter(this, tradeInItems);
        
            //attach adapter to designated recyclerView
            tradeInRecyclerV = findViewById(R.id.tradeInRView);
            tradeInRecyclerV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
            tradeInRecyclerV.setAdapter(tradeInAdapter);
            
            //add more trade-ins
            addMoreTradeIn = findViewById(R.id.addMoreTradeIn);
            addMoreTradeIn.setOnClickListener(this);

            //get view holder
            viewHolderTradeIn = tradeInAdapter.getViewHolder();

                //set & display trading-out item/s
        tradeOutItems = new ArrayList<>();
        tradeOutAdapter = new PlaceTradeAdapter(this, tradeOutItems);

            //attach adapter to recyclerView for trade-out
            tradeOutRecyclerV = findViewById(R.id.tradeOutRView);
            tradeOutRecyclerV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
            tradeOutRecyclerV.setAdapter(tradeOutAdapter);

            addMoreTradeOut = findViewById(R.id.addMoreTradeOut);
            addMoreTradeOut.setOnClickListener(this);

            //get view holder
            viewHolderTradeOut = tradeOutAdapter.getViewHolder();

        //setCalendarInput
        FrameLayout calButton = findViewById(R.id.calFrame);
        calButton.setOnClickListener(this);
            calInput = findViewById(R.id.calText);

        //setTimeInput
        FrameLayout timeButton = findViewById(R.id.timeFrame);
        timeButton.setOnClickListener(this);
            timeInput = findViewById(R.id.timeDisp);

        //setLocationInput
        FrameLayout locationButton = findViewById(R.id.locationFrame);
        locationButton.setOnClickListener(this);
            locationInput = findViewById(R.id.locationDisp);

        //set trade method input
        tMethod = findViewById(R.id.spinner);

    }

    @Override
    public void onClick(View v) {

        Bundle bundle = new Bundle();
        DialogAddMore newDialog = new DialogAddMore();

        switch(v.getId()){

            case R.id.back_button:
                finish();
                break;

            case R.id.discardTrade:
                finish();
                break;

            case R.id.placeTrade:

                if(tradeInItems.isEmpty() && tradeOutItems.isEmpty()){
                    Snackbar.make(findViewById(R.id.tradePlacer), "At least one item needed for trading.", Snackbar.LENGTH_SHORT)
                            .show();

                }   else if(calInput.getText().equals("")){
                    Snackbar.make(findViewById(R.id.tradePlacer), "Trading date hasn't been set yet", Snackbar.LENGTH_SHORT)
                            .show();
                    //Toast.makeText(this, "Trading date hasn't been set yet", Toast.LENGTH_SHORT).show();

                }   else if(timeInput.getText().equals("")){
                    Snackbar.make(findViewById(R.id.tradePlacer), "Trading time hasn't been set yet", Snackbar.LENGTH_SHORT)
                            .show();

                }   else if(locationInput.getText().equals("")){
                    Snackbar.make(findViewById(R.id.tradePlacer), "Trading location hasn't been set yet", Snackbar.LENGTH_SHORT)
                            .show();

                }   else if(tMethod.getSelectedItem() == null && tMethod == null){
                    Snackbar.make(findViewById(R.id.tradePlacer), "Trading method hasn't been picked yet", Snackbar.LENGTH_SHORT)
                            .show();

                }   else if(
                        tMethod.getSelectedItem() == null && tMethod == null &&
                        locationInput.getText().equals("") &&
                        timeInput.getText().equals("") &&
                        calInput.getText().equals("") &&
                        tradeInItems.isEmpty() && tradeOutItems.isEmpty()
                    )  {
                    Snackbar.make(findViewById(R.id.tradePlacer), "Please fill in all required fields.", Snackbar.LENGTH_SHORT)
                            .show();

                }   else    {
                    readyBasket = new TradeSession(traderID, otherTraderID);
                    readyBasket.setCurrentStatus(TradeSession.TRADE_STATUS[0]);
                    readyBasket.setTradeMethod(tMethod.getSelectedItem().toString());

                    //get all trade offerings
                    try{

                        for(InventoryItem tI : tradeInItems){
                            readyBasket.addTradeIn(tI);
                        }

                        for(InventoryItem tO : tradeOutItems){
                            readyBasket.addTradeOut(tO);
                        }

                    }
                    catch (NullPointerException e){
                        e.printStackTrace();
                    }

                        //set trade informations(location, time, date)
                        readyBasket.setTradeInformations( date, time, location);

                    FireDatabase writeToFirebase = new FireDatabase();
                    writeToFirebase.createNewTradeSession(readyBasket);

                    finish();
                }

                break;
                
            case R.id.addMoreTradeOut:
               /* if(tradeOutItems.isEmpty()){
                    Snackbar.make(findViewById(R.id.tradePlacer), "The only item has been added.", Snackbar.LENGTH_SHORT).show();

                }   else    {*/
                    bundle.putString("trader id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    bundle.putString("fromActivity", "TradePlace");
                    newDialog.setArguments(bundle);
                    newDialog.show(getSupportFragmentManager(), "addMore");

                //}
                break;

            case R.id.addMoreTradeIn:
               /* if(tradeOutItems.isEmpty()){
                    Snackbar.make(findViewById(R.id.tradePlacer), "The only item has been added.", Snackbar.LENGTH_SHORT).show();

                }   else    {*/
                    bundle.putString("trader id", interestedItem.getTraderID());
                    bundle.putString("trader name", interestedItem.getTraderName());
                    bundle.putString("fromActivity", "TradePlace");
                    newDialog.setArguments(bundle);
                    newDialog.show(getSupportFragmentManager(), "addMore");

                //}
                break;

            case R.id.calFrame:
                bundle.putString("fromActivity", "TradePlace");
                CalendarDialog calendarDialog = new CalendarDialog();
                calendarDialog.setArguments(bundle);
                calendarDialog.show(getSupportFragmentManager(), "calendar");
                break;

            case R.id.timeFrame:

                new TimePickerDialog().show(getSupportFragmentManager(), "time picker");
                break;

            case R.id.locationFrame:
                bundle.putString("fromActivity", "TradePlace");
                LocationDialog locationDialog = new LocationDialog();
                locationDialog.setArguments(bundle);
                locationDialog.show(getSupportFragmentManager(), "location input");
                break;
        }
    }

    public ArrayList<InventoryItem> getInBasketTradeIns(){
        return tradeInItems;
    }

    public ArrayList<InventoryItem> getInBasketTradeOuts() {return  tradeOutItems;}

        public void addTradeInsFromDialog(InventoryItem tInItem){
            tradeInItems.add(tInItem);
            tradeInAdapter.notifyDataSetChanged();
        }

        public void addTradeOutsFromDialog(InventoryItem tOutItem){
            tradeOutItems.add(tOutItem);
            tradeOutAdapter.notifyDataSetChanged();

        }

    public void setPickedDate(int d, int m, int y){

        LocalDate picked = LocalDate.of(y, Month.of(m), d);
        this.date = picked.toString();
        calInput.setText(picked.toString());
        //calInput.setText(String.format("%02d-%02d-%04d", day , month , year ));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        timeInput.setText(String.format("%02d:%02d", hourOfDay, minute));
        this.time = timeInput.getText().toString();
    }

    public void setLocationInput(String location){
        this.location = location;
        locationInput.setText(location);
        }

    public String getLocation(){
        return location;
    }

/*        private void writeToFirebase(String trader1ID, String trader2ID){

            try{
                DatabaseReference tradeInboxRef = FirebaseDatabase.getInstance().getReference("Trade Inbox");

                String trade_inbox = tradeInboxRef.push().getKey();

                //write to trader's entries
                tradeInboxRef.child("trade inbox").child(trade_inbox).child("initiator_id").setValue(traderID);
                tradeInboxRef.child("trade inbox").child(trade_inbox).child("receiver_id").setValue(otherTraderID);
                tradeInboxRef.child("trade inbox").child(trade_inbox).child("status").setValue("Request Pending");
                //tradeInb

                for(InventoryItem t1: tradeOutItems){
                    tradeInboxRef.child("trade inbox").child(trade_inbox).child("initiator_offering").setValue(t1.getKey());

                }

                for(InventoryItem t2: tradeInItems){
                    tradeInboxRef.child("trade inbox").child(trade_inbox).child("receiver_offering").setValue(t2.getKey());

                }

                //add trade instance id into each traders user reference
                DatabaseReference tradeInboxChild = new FireDatabase().getUserRef();
                tradeInboxChild.child(trader1ID).child("Trade Inbox").child(trade_inbox);
                tradeInboxChild.child(trader2ID).child("Trade Inbox").child(trade_inbox);

            }
            catch (NullPointerException e){
                e.printStackTrace();

            }
        }*/
}
