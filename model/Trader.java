package model;

import android.os.Parcel;
import android.os.Parcelable;

public class Trader implements Parcelable {
    private String trader_id, trader_name, image_url, token;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){

        @Override
        public Object createFromParcel(Parcel source) {
            return new Trader(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new Object[size];
        }
    };

    public Trader(){

    }

    //parcel
        public Trader(Parcel source){

            this.trader_id = source.readString();
            this.trader_name = source.readString();
            this.image_url = source.readString();
            this.token = source.readString();

        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

            dest.writeString(this.trader_id);
            dest.writeString(this.trader_name);
            dest.writeString(this.image_url);
            dest.writeString(this.token);
        }

        @Override
        public int describeContents() {
            return 0;
        }

    public Trader(String u_id, String trader_name) {
        this.trader_id = u_id;
        this.trader_name = trader_name;
    }

    public Trader(String u_id, String trader_name, String trader_image) {
        this.trader_id = u_id;
        this.trader_name = trader_name;
        this.image_url = trader_image;
    }


    //setter
    public void setToken(String userToken){
        this.token = userToken;
    }

    public void setTraderID(String trader_id) {
        this.trader_id = trader_id;
    }

    public void setTraderName(String trader_name) {
        this.trader_name = trader_name;
    }

    public void setImageURL(String image_url) {
        this.image_url = image_url;
    }

    //getter
    public String getTraderID(){
        return trader_id;
    }

    public String getTraderName() {
        return trader_name;
    }

    public String getImageUrl() {
        return image_url;
    }

    public String getToken(){return token;}

}
