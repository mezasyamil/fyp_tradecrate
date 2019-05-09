package util;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import model.InventoryItem;
import model.TradeSession;
import model.Trader;

import java.util.HashMap;
import java.util.Map;

public class FireDatabase {

    private DatabaseReference databaseReference;
    private Trader traderInfo;
    private String newToken;
    private Map<String, Object> updates = new HashMap<String, Object>();

    public FireDatabase(){
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
    }

    public DatabaseReference getUserRef(){
        return databaseReference;
    }

    public DatabaseReference getCurrentUserRef(){
        return databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    public void setStatus(boolean YesNo, final String selectedKey){

        updates.put("availability", YesNo);

       /* FirebaseDatabase.getInstance()
                .getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("inventory")
                .child(selectedKey)
                .child("availability")
                .setValue(YesNo);*/

        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("inventory")
                .child(selectedKey)
                .updateChildren(updates);
    }

    public void setTradingStatus(String newStatus, String tradingID, String traderOtherID){

        int count = 0;

        while(count < 2){

            if(count == 0){
                updates.put("status", newStatus);
                getCurrentUserRef().child("trade_inbox").child(tradingID).updateChildren(updates);

            }   else    {
                updates.put("status", newStatus);
                getUserRef().child(traderOtherID).child("trade_inbox").child(tradingID).updateChildren(updates);

            }

            count++;
        }

    }

    public Trader getTrader(String traderID){

        //final ArrayList<Trader> traders = new ArrayList<>();
        traderInfo = new Trader();

        DatabaseReference traderRef = databaseReference.child(traderID);
        traderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try{
                    /*traderInfo.setTraderName(dataSnapshot.child("google_name").getValue().toString());
                    traderInfo.setImageURL(dataSnapshot.child("google_name").getValue().toString());*/
                    traderInfo.setTraderName(dataSnapshot.child("google_name").getValue().toString());
                    traderInfo.setImageURL(dataSnapshot.child("google_profile_picture").getValue().toString());

                    //traders.add(traderInfo);
                }
                catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //return traders;
        return traderInfo;
    }

    public void addToken(){

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                newToken = instanceIdResult.getToken();
            }
        });

        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("inventory")
                .child("token")
                .setValue(newToken);

    }

    //call this when the trade session is created
    public void createNewTradeSession(TradeSession newSession){

        try{
            //write to current trader's entries
            int i = 0;
            String trade_inbox =  getUserRef().child(newSession.getTInitiatorID()).child("trade_inbox").push().getKey();
            String t_ID = "";

            while(i < 2){

                if(i == 0){
                    t_ID = newSession.getTInitiatorID();

                    getUserRef().child(t_ID).child("trade_inbox").child(trade_inbox)
                            .child("receiver_id")
                            .setValue(newSession.getTReceiverID());

                    getUserRef().child(t_ID).child("trade_inbox").child(trade_inbox)
                            .child("status")
                            .setValue(newSession.getCurrentTradeStatus());

                    getUserRef().child(t_ID).child("trade_inbox").child(trade_inbox)
                            .child("isWaitingForReply")
                            .setValue(true);

                    getUserRef().child(t_ID).child("trade_inbox").child(trade_inbox)
                            .child("trade_method")
                            .setValue(newSession.getTradeMethod());

                    getUserRef().child(t_ID).child("trade_inbox").child(trade_inbox)
                            .child("trading_location")
                            .setValue(newSession.getLocation());

                    getUserRef().child(t_ID).child("trade_inbox").child(trade_inbox)
                            .child("time")
                            .setValue(newSession.getTimeAppointed());

                    getUserRef().child(t_ID).child("trade_inbox").child(trade_inbox)
                            .child("when")
                            .setValue(newSession.getDateAppointed());

                    for(InventoryItem t1: newSession.getTradeIn()){
                        getUserRef().child(t_ID).child("trade_inbox").child(trade_inbox)
                                .child("their_offering")
                                .child(t1.getKey())
                                .child("quantity").setValue(t1.getQuantity());

                        getUserRef().child(t_ID).child("trade_inbox").child(trade_inbox)
                                .child("their_offering")
                                .child(t1.getKey())
                                .child("hasReceived").setValue(false);

                    }

                    for(InventoryItem t2: newSession.getTradeOut()){
                        getUserRef().child(t_ID).child("trade_inbox").child(trade_inbox)
                                .child("your_offering")
                                .child(t2.getKey())
                                .child("quantity").setValue(t2.getQuantity());

                        getUserRef().child(t_ID).child("trade_inbox").child(trade_inbox)
                                .child("your_offering")
                                .child(t2.getKey())
                                .child("hasReceived").setValue(false);

                    }

                }   else {
                    t_ID = newSession.getTReceiverID();

                    getUserRef().child(t_ID).child("trade_inbox").child(trade_inbox)
                            .child("initiator_id")
                            .setValue(newSession.getTInitiatorID());

                    getUserRef().child(t_ID).child("trade_inbox").child(trade_inbox)
                            .child("status")
                            .setValue(newSession.getCurrentTradeStatus());

                    getUserRef().child(t_ID).child("trade_inbox").child(trade_inbox)
                            .child("isWaitingForReply")
                            .setValue(false);

                    getUserRef().child(t_ID).child("trade_inbox").child(trade_inbox)
                            .child("trade_method")
                            .setValue(newSession.getTradeMethod());

                    getUserRef().child(t_ID).child("trade_inbox").child(trade_inbox)
                            .child("trading_location")
                            .setValue(newSession.getLocation());

                    getUserRef().child(t_ID).child("trade_inbox").child(trade_inbox)
                            .child("time")
                            .setValue(newSession.getTimeAppointed());

                    getUserRef().child(t_ID).child("trade_inbox").child(trade_inbox)
                            .child("when")
                            .setValue(newSession.getDateAppointed());

                    for(InventoryItem t1: newSession.getTradeIn()){
                        getUserRef().child(t_ID).child("trade_inbox").child(trade_inbox)
                                .child("your_offering")
                                .child(t1.getKey())
                                .child("quantity").setValue(t1.getQuantity());

                        getUserRef().child(t_ID).child("trade_inbox").child(trade_inbox)
                                .child("your_offering")
                                .child(t1.getKey())
                                .child("hasReceived").setValue(false);

                    }

                    for(InventoryItem t2: newSession.getTradeOut()){
                        getUserRef().child(t_ID).child("trade_inbox").child(trade_inbox)
                                .child("their_offering")
                                .child(t2.getKey())
                                .child("quantity").setValue(t2.getQuantity());

                        getUserRef().child(t_ID).child("trade_inbox").child(trade_inbox)
                                .child("their_offering")
                                .child(t2.getKey())
                                .child("hasReceived").setValue(false);

                    }

                }

                i++;
            }
        }
        catch (NullPointerException e){
            e.printStackTrace();

        }
    }

    public InventoryItem retrieveInventory( String traderID , String inventoryID ){

        final InventoryItem retInventory = new InventoryItem();
        getUserRef().child(traderID).child(inventoryID).child("inventory").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                retInventory.setAvailability(dataSnapshot.child("availability").getValue().toString().equals("true"));
                retInventory.setItemDescription(dataSnapshot.child("description").getValue().toString());
                retInventory.setItemImageUrl(dataSnapshot.child("image").getValue().toString());
                retInventory.setItemName(dataSnapshot.child("item_name").getValue().toString());
                retInventory.setLocation(dataSnapshot.child("location").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return retInventory;
    }

  /*  public boolean updateTradeSession(TradeSession thisTS){

        boolean updated = false;

            getUserRef().

        return updated;
    }*/


}
