package model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.auth.FirebaseAuth;

public class Tradeable implements Parcelable{

    private InventoryItem tradeable;
    private String trader_name, trader_url, trader_id;
    private boolean mine = false, received = false;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new Tradeable(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new Object[size];
        }
    };

    public Tradeable(){

    }

    public Tradeable(String t_id, String t_name, String t_url){
        this.trader_id = t_id;
        this.trader_name = t_name;
        this.trader_url = t_url;

    }

    //set parcelable to pass throughtout Activities
    Tradeable(Parcel source){

        try{
            this.trader_id = source.readString();
            this.trader_name = source.readString();
            this.trader_url = source.readString();

            this.tradeable = source.readParcelable(InventoryItem.class.getClassLoader());
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    //setter
    public void setTrader(Trader trdr){
        this.trader_id = trdr.getTraderID();
        this.trader_name = trdr.getTraderName();
        this.trader_url = trdr.getImageUrl();

    }

    public void setTrader_name(String trader_name) {
        this.trader_name = trader_name;
    }

    public void setTrader_url(String trader_url) {
        this.trader_url = trader_url;
    }

    public void setTrader_id(String trader_id) {
        this.trader_id = trader_id;
    }

    /*public void setCurrentTrading(boolean yesNo){
        this.currentTrading = yesNo;
    }*/

    public void setTradeable(InventoryItem iItem){
        this.tradeable = iItem;
    }

    public boolean isSelf(){
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(getTraderID())){
            mine = true;
        }
        return mine;
    }

    public void setReceived(boolean yes_no){

        this.received = yes_no;

    }

    public boolean hasReceived(){

        return received;
    }

    //getter
    public String getTraderName(){
        return trader_name;
    }

    public String getTraderPicture(){
        return trader_url;
    }

    public String getTraderID(){
        return trader_id;
    }

    public InventoryItem getTradeable(){
        return  tradeable;
    }

    //to check if current user is in trade session with the current view of another trader;
    /*public boolean isCurrentTrading(){
        return currentTrading;
    }*/

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        try{
            dest.writeString(this.trader_id);
            dest.writeString(this.trader_name);
            dest.writeString(this.trader_url);

            dest.writeParcelable(this.tradeable, flags);
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
