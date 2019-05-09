package util;

import android.content.Context;
import android.os.Bundle;
import android.view.*;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.izzat.syamil.tradecrate.Home.TradePlace;
import com.izzat.syamil.tradecrate.R;
import com.izzat.syamil.tradecrate.Trade_Inbox.TradeRequestView;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import model.InventoryItem;

import java.util.ArrayList;

public class DialogAddMore extends DialogFragment implements View.OnClickListener{

    private String traderID = "", otherTraderName = "";
    private RecyclerView fromInventoryRecyclerV;
    private ArrayList<InventoryItem> inventoryInBasket;
    private ArrayList<InventoryItem> fromInventory;
    private ArrayList<InventoryItem> addSelected;
    private AppCompatButton confirm, cancel;
    private fromInventoryAdapter fromIAdapter;
    private TextView selectorTitle, emptyIndicator;
    private DatabaseReference dRef;
    private ValueEventListener dBListener;
    private Context context;
    private boolean isMine = false;
    private int width = 0, height = 0;
    private ConstraintLayout layout;
    private String fromWhichActivity = "";
    private View parent;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.choose_more_dialog, container, false);

        traderID = getArguments().getString("trader id");
        fromWhichActivity = getArguments().getString("fromActivity");

        /*Tradeable alreadyInBasket = new Tradeable();
        alreadyInBasket = getArguments().getParcelable("selected item");

        traderID = alreadyInBasket.getTraderID();*/

        fromInventoryRecyclerV = v.findViewById(R.id.displayFromInventory);
        fromInventoryRecyclerV.setLayoutManager(new LinearLayoutManager(getActivity()));

        fromIAdapter = new fromInventoryAdapter();
        fromInventoryRecyclerV.setAdapter(fromIAdapter);

        selectorTitle = v.findViewById(R.id.selectorTitle);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        addSelected = new ArrayList<>();
        inventoryInBasket = new ArrayList<>();
        if(traderID.equals(currentUser.getUid()) && fromWhichActivity.equals("TradePlace") ){
            inventoryInBasket = ((TradePlace)getActivity()).getInBasketTradeOuts();
            selectorTitle.setText("your");
            isMine = true;

        }   else  if(!traderID.equals(currentUser.getUid()) && fromWhichActivity.equals("TradePlace") )  {
            inventoryInBasket = ((TradePlace)getActivity()).getInBasketTradeIns();
            otherTraderName = getArguments().getString("trader name");
            selectorTitle.setText(otherTraderName + "'s");

        }   else  if(!traderID.equals(currentUser.getUid()) && fromWhichActivity.equals("tradeRequestView") )  {
            inventoryInBasket = ((TradeRequestView)getActivity()).getInBasketTradeIns();
            otherTraderName = getArguments().getString("trader name");
            selectorTitle.setText(otherTraderName + "'s");

        }   else   if(traderID.equals(currentUser.getUid()) && fromWhichActivity.equals("tradeRequestView") ){
            inventoryInBasket = ((TradeRequestView)getActivity()).getInBasketTradeOuts();
            selectorTitle.setText("your");
            isMine = true;

        }

        fetchInventories();

        /*fromInventory = new ArrayList<>();
        fromInventory.add(inventoryInBasket.get(0).getCartItem());
        fromIAdapter.notifyDataSetChanged();*/

        confirm = v.findViewById(R.id.insertSelected);
        confirm.setOnClickListener(this);

        cancel = v.findViewById(R.id.cancelInventoryPick);
        cancel.setOnClickListener(this);

        context = v.getContext();
        emptyIndicator = v.findViewById(R.id.emptyIndicator);

        layout = v.findViewById(R.id.dialogAddMore);

       /* View dialog = v.findViewById(R.id.dialogAddMore);
        height = dialog.getLayoutParams().height;
        width = dialog.getLayoutParams().width;*/

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setLayout(700, 450);
        window.setGravity(Gravity.CENTER);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.insertSelected:
                if(!fromInventory.isEmpty()){

                    for(InventoryItem tIO: fromInventory){
                        if(isMine && tIO.isSelectedFromInventory() && fromWhichActivity.equals("TradePlace")){
                            ((TradePlace)getActivity()).addTradeOutsFromDialog(tIO);

                        }
                        else if(!isMine && tIO.isSelectedFromInventory() && fromWhichActivity.equals("TradePlace")){
                            ((TradePlace)getActivity()).addTradeInsFromDialog(tIO);

                        }

                        else if(isMine && tIO.isSelectedFromInventory() && fromWhichActivity.equals("tradeRequestView")){
                            ((TradeRequestView)getActivity()).addTradeOutsFromDialog(tIO);
                            ((TradeRequestView)getActivity()).setChangesMade();

                        }

                        else if(!isMine && tIO.isSelectedFromInventory() && fromWhichActivity.equals("tradeRequestView")){
                            ((TradeRequestView)getActivity()).addTradeInsFromDialog(tIO);
                            ((TradeRequestView)getActivity()).setChangesMade();

                        }
                    }
                }
                dismiss();
            break;

            case R.id.cancelInventoryPick:
                dismiss();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void fetchInventories(){

        fromInventory = new ArrayList<>();
        dRef = new FireDatabase().getUserRef().child(traderID).child("inventory");
        dBListener = dRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for(DataSnapshot ds: dataSnapshot.getChildren()){

                        InventoryItem temp = new InventoryItem();
                        try{
                            String keyCheck = ds.getKey();

                            //test
                           /* temp.setKey(keyCheck);
                            temp.setItemImageUrl(ds.child("image").getValue().toString());
                            temp.setItemName(ds.child("item_name").getValue().toString());
                            fromInventory.add(temp);*/

                            if(inventoryInBasket.isEmpty() && ds.child("availability").getValue().equals(true)){
                                temp.setKey(keyCheck);
                                temp.setItemImageUrl(ds.child("image").getValue().toString());
                                temp.setItemName(ds.child("item_name").getValue().toString());
                                fromInventory.add(temp);

                            }   else  if(!inventoryInBasket.isEmpty() && ds.child("availability").getValue().equals(true))    {

                                for(InventoryItem tIO: inventoryInBasket){
                                    if( !keyCheck.equals(tIO.getKey()) ){
                                        temp.setKey(keyCheck);
                                        temp.setItemImageUrl(ds.child("image").getValue().toString());
                                        temp.setItemName(ds.child("item_name").getValue().toString());
                                        fromInventory.add(temp);
                                    }
                                }
                            }

                        }   catch (NullPointerException e){
                            e.printStackTrace();
                        }
                }

                fromIAdapter.notifyDataSetChanged();

                if(fromInventory.isEmpty()){
                    confirm.setText("Okay");
                    cancel.setVisibility(View.INVISIBLE);
                    //emptyIndicator.setVisibility(View.VISIBLE);
                    Snackbar.make(getView(), "The only item has been added.", Snackbar.LENGTH_SHORT)
                            //.setAnchorView(R.id.insertSelected)
                            .show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private class fromInventoryAdapter extends RecyclerView.Adapter<fromInventoryAdapter.fromInventoryViewHolder>
                                        {

        public class fromInventoryViewHolder extends RecyclerView.ViewHolder{

            public TextView tItemName;
            public CircleImageView tItemImage;
            public CheckBox selectedCheckBox;

            public fromInventoryViewHolder(@NonNull View itemView){
                super(itemView);
                tItemName = itemView.findViewById(R.id.tradeItemName);
                tItemImage = itemView.findViewById(R.id.tradeItemImage);
                selectedCheckBox = itemView.findViewById(R.id.checkForAdding);
            }
        }

        @NonNull
        @Override
        public fromInventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.from_inventory, parent, false);
            fromInventoryViewHolder viewHolder = new fromInventoryViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull final fromInventoryViewHolder holder, int position) {

            final InventoryItem temp2 = fromInventory.get(position);

            holder.tItemName.setText(temp2.getItemName());

            Picasso.with(getActivity())
                    .load(temp2.getItemImageURL())
                    .fit()
                    .into(holder.tItemImage);

            holder.selectedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(isChecked){
                        temp2.setSelectedFromInventory(true);
                    }
                    else    {
                        temp2.setSelectedFromInventory(false);
                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return fromInventory.size();
        }

    }
}


