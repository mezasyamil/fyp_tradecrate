package com.izzat.syamil.tradecrate.Home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.storage.StorageReference;
import com.izzat.syamil.tradecrate.InventoryView;
import com.izzat.syamil.tradecrate.R;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import model.InventoryItem;
import model.Tradeable;
import util.FirebaseStorageUtil;

import java.util.ArrayList;

public class BrowseAdapter extends RecyclerView.Adapter<BrowseAdapter.BrowseViewHolder> {

    private Context context;
    private View browseView;
    private ArrayList<Tradeable> tradeables = new ArrayList<>();
    private ArrayList<InventoryItem> myInventory;
    //private DatabaseReference fireDB = new FireDatabase().getInventoryDatabaseRef();
    private StorageReference fireStorage = new FirebaseStorageUtil().getInventoryStorageRef();
    private BrowseViewHolder bHolder;

    public BrowseAdapter(Context cont, ArrayList<Tradeable> tItems){
        this.context = cont;
        this.tradeables = tItems;
    }

    public class BrowseViewHolder extends RecyclerView.ViewHolder{
        public CircleImageView owner;
        public TextView trader_name, tradeable_name, tradeable_location, inventory_since;
        public ImageView background_tradeable;
        public CardView cardV;

        public BrowseViewHolder(@NonNull View itemView){
            super(itemView);
            owner = itemView.findViewById(R.id.t_owner);
            trader_name = itemView.findViewById(R.id.trader_name);
            tradeable_name = itemView.findViewById(R.id.t_name);
            tradeable_location = itemView.findViewById(R.id.t_Location);
            inventory_since = itemView.findViewById(R.id.uploadSince);
            background_tradeable = itemView.findViewById(R.id.t_backG);
            cardV = itemView.findViewById(R.id.home_card);

        }
    }


    @NonNull
    @Override
    public BrowseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.home_card, parent, false);
        bHolder = new BrowseViewHolder(inflate);
        return bHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BrowseViewHolder holder, int position) {
        final Tradeable tradeable = tradeables.get(position);

        holder.trader_name.setText(tradeable.getTraderName());
        Picasso.with(context)
                .load(tradeable.getTraderPicture())
                .into(holder.owner);

        InventoryItem i_tradeable = tradeable.getTradeable();
        holder.tradeable_name.setText(i_tradeable.getItemName());
        holder.tradeable_location.setText(i_tradeable.getItemLocation());
        Picasso.with(context)
                .load(i_tradeable.getItemImageURL())
                .placeholder(R.drawable.accent_solid_top_curves)
                .fit()
                .centerCrop()
                .into(holder.background_tradeable);

        holder.cardV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, InventoryView.class);
                i.putExtra("view selected item", tradeable);
               /* if(!tradeable.isSelf()){
                    i.putExtra("my inventories", myInventory);
                }*/
                context.startActivity(i);
            }
        });

    }

  /*  public BrowseViewHolder getBrowseViewHolder(){
        return bHolder;
    }*/

    @Override
    public int getItemCount() {

        return tradeables.size();
    }
}
