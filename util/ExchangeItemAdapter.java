package util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.izzat.syamil.tradecrate.R;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import model.Tradeable;

import java.util.ArrayList;

public class ExchangeItemAdapter extends RecyclerView.Adapter<ExchangeItemAdapter.ExcViewHolder>{

    private Context context;
    private ArrayList<Tradeable> tradeables;

    public ExchangeItemAdapter(Context c, ArrayList<Tradeable> trds){
        this.context = c;
        this.tradeables = trds;

    }

    public class ExcViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView itemPicture;
        public TextView item_outgoing;

        public ExcViewHolder(@NonNull View itemView){
            super(itemView);

            itemPicture = itemView.findViewById(R.id.imageItem);
            item_outgoing = itemView.findViewById(R.id.itemLabel);
        }

    }

    @NonNull
    @Override
    public ExcViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.exchanging_card, parent, false);
        return new ExcViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ExcViewHolder holder, int position) {

        Tradeable temp = tradeables.get(position);

        holder.item_outgoing.setText(

                temp.getTradeable().getQuantity() + " of " + temp.getTradeable().getItemName()

        );


        Picasso.with(context)
                .load(temp.getTradeable().getItemImageURL())
                .centerCrop()
                .fit()
                .into(holder.itemPicture);

    }

    public Tradeable getTradeableAt(int position){
        return tradeables.get(position);
    }

    @Override
    public int getItemCount() {
        return tradeables.size();
    }

}
