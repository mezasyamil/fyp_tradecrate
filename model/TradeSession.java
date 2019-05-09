package model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class TradeSession implements Parcelable{

    /*public final static int REQUEST_PENDING = 0;
    public final static int REQUEST_ACCEPTED = 1;*/

    public final static String[] TRADE_STATUS = new String[]{"Request Pending", "Request Accepted", "Request Declined",
            "Request Cancelled", "Request Changes", "Changes Agreed", "Trade Pending", "Trading", "Trade Successful", "Trade Cancelled" };
    private String tInitiatorID = "", tReceiverID = "", currentTradeStatus = "", tradeMethod = "", location = "",
                    timeAppointed = "", dateAppointed = "", t_key = "", waitingForReplyID = "";
    private ArrayList<InventoryItem> tradeOut, tradeIn;
    private Trader otherTraderInfo = new Trader();

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){

        @Override
        public Object createFromParcel(Parcel source) {
            return new TradeSession(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new Object[size];
        }
    };

    public TradeSession(){
        tradeOut = new ArrayList<>();
        tradeIn = new ArrayList<>();
    }

    public TradeSession(String tInitiatorID, String tReceiverID){

        this.tInitiatorID = tInitiatorID;
        this.tReceiverID = tReceiverID;
        tradeOut = new ArrayList<>();
        tradeIn = new ArrayList<>();
    }

        //parcel
        public TradeSession(Parcel source){

            try{
                this.t_key = source.readString();
                this.currentTradeStatus = source.readString();
                this.tradeMethod = source.readString();
                this.tInitiatorID = source.readString();
                this.tReceiverID = source.readString();
                this.dateAppointed = source.readString();
                this.timeAppointed = source.readString();
                this.location = source.readString();
                this.otherTraderInfo = source.readParcelable(Trader.class.getClassLoader());

                this.tradeIn = new ArrayList<InventoryItem>();
                source.readTypedList(tradeIn, InventoryItem.CREATOR_ARRAY);

                this.tradeOut = new ArrayList<InventoryItem>();
                source.readTypedList( tradeOut ,InventoryItem.CREATOR_ARRAY);
            }
            catch (NullPointerException e){
                e.printStackTrace();
            }

        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

            dest.writeString(this.t_key);
            dest.writeString(this.currentTradeStatus);
            dest.writeString(this.tradeMethod);
            dest.writeString(this.tInitiatorID);
            dest.writeString(this.tReceiverID);
            dest.writeString(this.dateAppointed);
            dest.writeString(this.timeAppointed);
            dest.writeString(this.location);
            dest.writeParcelable(getOtherTraderInfo(), flags);
            dest.writeTypedList(getTradeIn());
            dest.writeTypedList(getTradeOut());

        }

        @Override
        public int describeContents() {
            return 0;
        }

    //setter
    public void setKey(String tradeKey) { this.t_key = tradeKey; }

    public void settInitiatorID(String iID) { this.tInitiatorID = iID; }

    public void settReceiverID(String rID) { this.tReceiverID = rID; }

    public void setTraderWaitingForReply(String tID) {
        this.waitingForReplyID = tID;

    }

    public void setOtherTraderInfo(Trader otherTInfo) { this.otherTraderInfo = otherTInfo; }

    public void setTradeIn(ArrayList<InventoryItem> confirmItem){
        this.tradeIn = confirmItem;
    }

    public void setTradeOut(ArrayList<InventoryItem> confirmItem){
        this.tradeOut = confirmItem;
    }

    public void addTradeIn(InventoryItem confirmItem){
        tradeIn.add(confirmItem);

    }

    public void addTradeOut(InventoryItem confirmItem){
        tradeOut.add(confirmItem);

    }

    public void setTradeMethod(String methodType){
        this.tradeMethod = methodType;
    }

    public void setCurrentStatus(String stat){
        currentTradeStatus = stat;
    }

    public void setTradeInformations(String date, String time, String venue ){
        this.dateAppointed = date;
        this.timeAppointed = time;
        this.location = venue;

    }

    //getter
    public String getTradeKey(){ return t_key; }

    public String getCurrentTradeStatus(){
        return currentTradeStatus;
    }

    public String getTInitiatorID() {
        return tInitiatorID;
    }

    public String getTReceiverID() {
        return tReceiverID;
    }

    public String getWaitingForReplyID(){
        return waitingForReplyID;
    }

    public Trader getOtherTraderInfo() {return otherTraderInfo; }

    public String getTradeMethod(){ return tradeMethod;}

    public String getLocation(){ return location; }

    public String getTimeAppointed(){ return timeAppointed; }

    public String getDateAppointed(){ return dateAppointed; }

    public ArrayList<InventoryItem> getTradeIn(){
        return tradeIn;
    }

    public ArrayList<InventoryItem> getTradeOut(){
        return tradeOut;
    }

}
