package util;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.izzat.syamil.tradecrate.R;
import com.squareup.picasso.Picasso;
import model.InventoryItem;

import java.util.ArrayList;

public class PlaceTradeAdapter extends RecyclerView.Adapter<PlaceTradeAdapter.PlaceTradeViewHolder> {

    private Context context;
    private PlaceTradeViewHolder viewHolder;
    private ArrayList<InventoryItem> tradeBasket = new ArrayList<>();
    private ArrayList<InventoryItem> tradeBasketBeforeChanges = new ArrayList<>();
    private ArrayList<String> latestQuantityInput = new ArrayList<>();
    private View v;

    public PlaceTradeAdapter(Context context, ArrayList<InventoryItem> selected){
        this.context = context;
        this.tradeBasket = selected;
        this.tradeBasketBeforeChanges = selected;
    }

    public class PlaceTradeViewHolder extends RecyclerView.ViewHolder{

        public ImageView iImage;
        public TextView iName;
        public EditText qtt;
        public Spinner unitSpinner;
        public ImageButton removeCartItem;

        public PlaceTradeViewHolder(@NonNull View itemView){
            super(itemView);

            iImage = itemView.findViewById(R.id.tradeableImage);
            iName = itemView.findViewById(R.id.tradeableName);
            qtt = itemView.findViewById(R.id.qty);
            unitSpinner = itemView.findViewById(R.id.spinner2);
            removeCartItem = itemView.findViewById(R.id.removeCartItem);
        }
    }

    @NonNull
    @Override
    public PlaceTradeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        v = LayoutInflater.from(context).inflate(R.layout.trading_card, parent, false);
        viewHolder = new PlaceTradeViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PlaceTradeViewHolder holder, final int position) {
        final InventoryItem tradeInterest = tradeBasket.get(position);
        //final String quantity = latestQuantityInput.get(position);

        holder.iName.setText(tradeInterest.getItemName());

            Picasso.with(context)
                    .load(tradeInterest.getItemImageURL())
                    .centerCrop()
                    .fit()
                    .into(holder.iImage);

            holder.removeCartItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final InventoryItem undoItem = tradeInterest;
                    tradeBasket.remove(position);
                    
                    //Prompt user if they wanted to undo
                    Snackbar.make(v, "Undo removed items", Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tradeBasket.add(position, undoItem);
                            notifyDataSetChanged();
                        }
                    }).show();
                    
                    notifyDataSetChanged();
                }
            });

        if(tradeInterest.getQuantity().isEmpty()){
            tradeInterest.setQuantity(Double.parseDouble(holder.qtt.getEditableText().toString()));

        }   else    {
            holder.qtt.setText(tradeInterest.getQuantity());

        }

            holder.qtt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try{

                        if(!tradeInterest.getQuantity().equals(s.toString())){
                            tradeInterest.setQuantity(Double.parseDouble(s.toString()));
                            tradeInterest.setIfChanges(true);

                        }   else {
                            tradeInterest.setQuantity(Double.parseDouble(s.toString()));

                        }

                    }
                    catch (NumberFormatException e){
                        e.printStackTrace();
                    }
                    //latestQuantityInput.add(s.toString());

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        if(!tradeInterest.getQuantity().isEmpty()){

            int i = 0;
            while(i < 0){

                if( holder.unitSpinner.getItemAtPosition(i).equals(tradeInterest.getQuantityWithoutUnitMeasurement()) ){

                    holder.unitSpinner.setSelection(i);

                }

            }
        }

        if(!tradeInterest.getQuantityUnit().equals("")){

            int i = 0;
            while(i < 2){

                if( holder.unitSpinner.getItemAtPosition(i).equals(tradeInterest.getQuantityUnit()) ){

                    holder.unitSpinner.setSelection(i);
                    break;

                }

                i++;
            }

        }   else    {
            tradeInterest.setQuantityUnit(holder.unitSpinner.getSelectedItem().toString());

        }

        holder.unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(!parent.getItemAtPosition(position).toString().equals(
                        tradeInterest.getQuantityUnit())){

                    tradeInterest.setQuantityUnit(parent.getItemAtPosition(position).toString());
                    tradeInterest.setIfChanges(true);

                }   else    {
                    tradeInterest.setQuantityUnit(parent.getItemAtPosition(position).toString());

                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });

    }

    @Override
    public int getItemCount() {
        return tradeBasket.isEmpty()? 0 : tradeBasket.size();
    }


    public PlaceTradeViewHolder getViewHolder(){
        return viewHolder;
    }
}
