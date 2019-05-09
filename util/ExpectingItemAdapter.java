package util;

import android.content.Context;
import android.graphics.Color;
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

public class ExpectingItemAdapter extends RecyclerView.Adapter<ExpectingItemAdapter .ExpectingChip> {

    private ArrayList<Tradeable> expectings;
    private Context context;
    private OnItemClick onItemClick;

    public ExpectingItemAdapter(Context c, ArrayList<Tradeable> expectingItems){

        this.context = c;
        this.expectings = expectingItems;

    }

    public class ExpectingChip extends RecyclerView.ViewHolder{

        public CircleImageView itemImage;
        public TextView label;

        public ExpectingChip(@NonNull View chipView){
            super(chipView);

            itemImage = chipView.findViewById(R.id.miniItemImage);
            label = chipView.findViewById(R.id.itemLabel);

        }
    }

    @NonNull
    @Override
    public ExpectingChip onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View chip = LayoutInflater.from(context).inflate(R.layout.basket_chip, parent, false);
        return new ExpectingChip(chip);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpectingChip holder, final int position) {

        try{
            Tradeable expecting = expectings.get(position);

            Picasso.with(context)
                    .load(expecting.getTradeable().getItemImageURL())
                    .fit()
                    .centerCrop()
                    .into(holder.itemImage);

            holder.label.setText( expecting.getTradeable().getQuantityWithoutUnitMeasurement() + " " +
                    expecting.getTradeable().getQuantityUnit().toUpperCase() +
                    " of " +
                    expecting.getTradeable().getItemName().toUpperCase());


            //holder.card.setBackgroundColor(Color.argb(100, 5,188, 120));
            if(expecting.hasReceived()){
                holder.itemView.setBackground(context.getDrawable(R.drawable.square_yeap));
                holder.label.setTextColor(Color.BLACK);

                //holder.itemView.setBackgroundColor(Color.argb(100, 5,188, 120));
                //BackgroundColor(Color.argb(100, 5,188, 120));
                //notifyDataSetChanged();
                //holder.label.setChecked(true);

            }   else    {
                holder.itemView.setBackground(context.getDrawable(R.drawable.square_nope));

            }

            //custom onClick item image viewing
            holder.label.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick.onItemClick(position);
                }
            });

        }   catch (NullPointerException e){
            e.printStackTrace();

        }
    }

    public interface OnItemClick{
        void onItemClick(int position);
    }

    @Override
    public int getItemCount() {
        return expectings.size();
    }

    public void setOnItemClick(OnItemClick onItemClick){
        this.onItemClick = onItemClick;
    }

}
