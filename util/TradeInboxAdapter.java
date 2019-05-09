package util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.izzat.syamil.tradecrate.R;
import com.izzat.syamil.tradecrate.Trade_Inbox.InventoryExchange;
import com.izzat.syamil.tradecrate.Trade_Inbox.TradeRequestView;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import model.TradeSession;

import java.util.ArrayList;

public class TradeInboxAdapter extends RecyclerView.Adapter<TradeInboxAdapter.TIViewHolder>{

    private Context ctxt;
    private ArrayList<TradeSession> tradeSessions;

    public TradeInboxAdapter(Context context, ArrayList<TradeSession> tradeSessions){
        this.ctxt = context;
        this.tradeSessions = tradeSessions;
    }

    public class TIViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView otherTraderPic;
        public TextView otherTraderName, tradeDetails;
        public CardView cardSeeMore;

        public TIViewHolder(@NonNull View itemView){
            super(itemView);
            otherTraderPic = itemView.findViewById(R.id.otherTraderPhoto);
            otherTraderName = itemView.findViewById(R.id.otherTraderName);
            tradeDetails = itemView.findViewById(R.id.tDetails);
            cardSeeMore = itemView.findViewById(R.id.tradeRequestCard);

        }

    }

    @NonNull
    @Override
    public TIViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctxt).inflate(R.layout.t_inbox_card, parent, false);
        TIViewHolder tiViewHolder = new TIViewHolder(v);
        return tiViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TradeInboxAdapter.TIViewHolder holder, final int position) {
        final TradeSession tradeSession = tradeSessions.get(position);
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Picasso.with(ctxt).load(tradeSession.getOtherTraderInfo().getImageUrl())
                .fit()
                .placeholder(R.drawable.accent_solid_circle)
                .into(holder.otherTraderPic);

        holder.otherTraderName.setText(tradeSession.getOtherTraderInfo().getTraderName());
        //holder.otherTraderName.setText(tradeSession.getTradeIn().get(0).getQuantity());

        Log.d("check otherID", tradeSession.getOtherTraderInfo().getTraderID());

        if(tradeSession.getTInitiatorID().equals(currentUserID)){

            //status of trading behaviour dictates here
            switch (tradeSession.getCurrentTradeStatus()){

                case "Request Pending":
                    holder.tradeDetails.setText("Request in pending");

                    holder.cardSeeMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent i = new Intent(ctxt, TradeRequestView.class);
                            i.putExtra("view trade session", tradeSession );
                            ctxt.startActivity(i);

                        }
                    });

                    break;

                case "Request Accepted":
                    holder.tradeDetails.setText("Request agreed. Ready for trading");

                    tradeSession.setCurrentStatus("Trade Pending");

                    holder.cardSeeMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent i = new Intent(ctxt, InventoryExchange.class);
                            i.putExtra("trade ready", tradeSession );
                            i.putExtra("from which activity", "trade inbox fragment" );
                            ctxt.startActivity(i);

                        }
                    });

                    break;

                case "Request Declined":
                    holder.tradeDetails.setText("has declined your trade request");

                    holder.cardSeeMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent i = new Intent(ctxt, TradeRequestView.class);
                            i.putExtra("view trade session", tradeSession );
                            ctxt.startActivity(i);

                        }
                    });

                    //enable the sender resend new offers
                    break;

                //this particular case will acknowledge the sender of new trade changes
                case "Request Changes":

                    if(tradeSession.getWaitingForReplyID().equals(currentUserID)){
                        holder.tradeDetails.setText("Waiting for response");

                    }   else    {
                        holder.tradeDetails.setText("has made request changes");

                    }

                   /* holder.cardSeeMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent i = new Intent(ctxt, TradeRequestView.class);
                            i.putExtra("view trade session", tradeSession );
                            ctxt.startActivity(i);

                        }
                    });*/

                    break;

                case "Trade Successful":
                    holder.tradeDetails.setText("Successful exchanged");

                    holder.cardSeeMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent i = new Intent(ctxt, InventoryExchange.class);
                            i.putExtra("trade ready", tradeSession );
                            i.putExtra("from which activity", "trade inbox fragment" );
                            ctxt.startActivity(i);

                        }
                    });
                    break;

                case "Trade Cancelled":
                    holder.tradeDetails.setText("Cancelled");
                    break;
            }

        }   else    {

            //status of trading behaviour dictates here, for the receiver-end
            switch (tradeSession.getCurrentTradeStatus()){

                case "Request Pending":
                    holder.tradeDetails.setText("would like to trade with you");

                    holder.cardSeeMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent i = new Intent(ctxt, TradeRequestView.class);
                            i.putExtra("view trade session", tradeSession );
                            ctxt.startActivity(i);

                        }
                    });

                    break;

                case "Request Accepted":
                    holder.tradeDetails.setText("Request agreed. Ready for trading");

                    tradeSession.setCurrentStatus("Trade Pending");

                    holder.cardSeeMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent i = new Intent(ctxt, InventoryExchange.class);
                            i.putExtra("trade ready", tradeSession );
                            i.putExtra("from which activity", "trade inbox fragment" );
                            ctxt.startActivity(i);

                        }
                    });

                    break;

                case "Request Declined":
                    holder.tradeDetails.setText("Request declined");
                    break;

                case "Request Changes":
                    if(tradeSession.getWaitingForReplyID().equals(currentUserID)){
                        holder.tradeDetails.setText("Waiting for response");

                    }   else    {
                        holder.tradeDetails.setText("has made request changes");

                    }

                    //wait for the sender to respond, until both mutual agreed
                   /* holder.cardSeeMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent i = new Intent(ctxt, TradeRequestView.class);
                            i.putExtra("view trade session", tradeSession );
                            ctxt.startActivity(i);

                        }
                    });*/

                    break;

                case "Request Cancelled":
                    break;

                case "Trade Successful":
                    holder.tradeDetails.setText("Successful exchanged");

                    holder.cardSeeMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent i = new Intent(ctxt, InventoryExchange.class);
                            i.putExtra("trade ready", tradeSession );
                            i.putExtra("from which activity", "trade inbox fragment" );
                            ctxt.startActivity(i);

                        }
                    });

                    break;

                case "Trade Cancelled":
                    holder.tradeDetails.setText("Cancelled");
                    break;
            }

        }
    }

    @Override
    public int getItemCount() {
        //Log.d("check size", String.valueOf(tradeSessions.size()));
        return tradeSessions.size();
        //return  0;
    }
}
