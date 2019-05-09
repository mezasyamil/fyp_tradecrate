package model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;

public class InventoryItem implements Parcelable{

    private String item_name, item_image_url = "", item_description = "", location = "", i_key = "", quantityUnit = "";
    private long upload_since;
    private boolean i_status = false, selectedFromInventory = false, updated = false;
    private double quantity = 1.0;
    private ArrayList<String> interestedID;

    public static final Creator<InventoryItem> CREATOR_ARRAY = new Creator<InventoryItem>() {
        @Override
        public InventoryItem createFromParcel(Parcel source) {
            return new InventoryItem(source);
        }

        @Override
        public InventoryItem[] newArray(int size) {
            return new InventoryItem[size];
        }
    };

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new InventoryItem(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new Object[size];
        }
    };

    public InventoryItem(){
        interestedID = new ArrayList<>();
    }

    //name & item name are compulsory
    public InventoryItem(String iName){
        this.item_name = iName;
        this.i_status = true;
    }

    public InventoryItem(Parcel source){

        try{
            this.i_key = source.readString();
            this.item_name = source.readString();
            this.item_description = source.readString();
            this.location = source.readString();
            this.item_image_url = source.readString();
            setAvailability((source.readByte()!= 0));

            //read quantity
            setQuantityAsString(source.readString());
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        try{
            dest.writeString(this.i_key);
            dest.writeString(this.item_name);
            dest.writeString(this.item_description);
            dest.writeString(this.location);
            dest.writeString(this.item_image_url);
            dest.writeByte((byte)(this.i_status? 1: 0));

            //parcel quantity
            dest.writeString(getQuantity());
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //setter
    @Exclude
    public void setKey(String iKey){
        this.i_key = iKey;
    }

    public void setItemName(String t_name){
        this.item_name = t_name;
    }

    public void setItemImageUrl(String i_url){
        this.item_image_url = i_url;
    }

    public void setItemDescription(String i_description){
        this.item_description = i_description;
    }

    public void setLocation(String t_location){
        this.location = t_location;
    }

    public void setAvailability(boolean YesNo){
        this.i_status = YesNo;
    }

    public void setUploadDate(long timeDate){
        this.upload_since = timeDate;
    }

    public void setInterestedID(String iID){
        interestedID.add(iID);
    }


    //getter
    public String getItemName(){
        return item_name;
    }

    public String getItemImageURL(){
        return item_image_url;
    }

    public String getItemDescription(){
        return item_description;
    }

    public String getItemLocation(){
        return location;
    }

    public boolean isAvailable(){
        return i_status;
    }

    public long getUploadSince(){
        return upload_since;
    }

    public ArrayList<String> getInterestedID(){
        return interestedID;
    }


    @Exclude
    public String getKey(){
        return i_key;
    }

    //selection for trade initiating
    public void setSelectedFromInventory(boolean selectedFromInventory) {
        this.selectedFromInventory = selectedFromInventory;
    }

    public boolean isSelectedFromInventory(){
        return  selectedFromInventory;
    }

    //set Quantity
    public void setQuantity(double quantity){
        this.quantity = quantity;

    }

    public void setQuantityAsString(String qtt){
        String[] qArr = qtt.split(" ");

        for(int i = 0; i < qArr.length; i++){
            if(i == 0){
                this.quantity = Double.parseDouble(qArr[i]);

            }   else    {
                this.quantityUnit = qArr[i];
            }
        }
    }

    public void setQuantityUnit(String qttUnit){

        this.quantityUnit = qttUnit;

    }

    public String getQuantity(){
        return String.valueOf(quantity) + " " + quantityUnit;
    }

    public String getQuantityWithoutUnitMeasurement(){
        return String.valueOf(quantity);
    }

    public String getQuantityUnit() {
        return quantityUnit;
    }

    public void setIfChanges(boolean changes){

        this.updated = changes;

    }

    public boolean isThereAnyChanges(){

        return updated;

    }
}
