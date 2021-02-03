package com.mangerbaedis.elsalammanger.pojo;

import android.os.Parcel;
import android.os.Parcelable;


public class Pro_Of_Product implements Parcelable {


    private String name;


    private String descShort;


    private String description;


    private String price;


    private String nameOfMarket;
    private String phoneOfMarket;
    private String locationOfMarket;
    private String image;


    private String numberQuntity;


    private int uid;
    private String idcatogray;
    private boolean deleivary;

    public Pro_Of_Product(String name, String descShort, String description, String price, String nameOfMarket,
                          String phoneOfMarket,String locationOfMarket, String image, String idcatogray,boolean deleivary) {
        this.name = name;
        this.descShort = descShort;
        this.description = description;
        this.price = price;
        this.nameOfMarket = nameOfMarket;
        this.phoneOfMarket = phoneOfMarket;
        this.locationOfMarket=locationOfMarket;
        this.image = image;
        this.idcatogray = idcatogray;
        this.deleivary=deleivary;
    }


    protected Pro_Of_Product(Parcel in) {
        name = in.readString();
        descShort = in.readString();
        description = in.readString();
        price = in.readString();
        nameOfMarket = in.readString();
        phoneOfMarket = in.readString();
        locationOfMarket = in.readString();
        image = in.readString();
        numberQuntity = in.readString();
        uid = in.readInt();
        idcatogray = in.readString();
        deleivary = in.readByte() != 0;
    }

    public static final Creator<Pro_Of_Product> CREATOR = new Creator<Pro_Of_Product>() {
        @Override
        public Pro_Of_Product createFromParcel(Parcel in) {
            return new Pro_Of_Product(in);
        }

        @Override
        public Pro_Of_Product[] newArray(int size) {
            return new Pro_Of_Product[size];
        }
    };

    public String getIdcatogray() {
        return idcatogray;
    }

    public void setIdcatogray(String idcatogray) {
        this.idcatogray = idcatogray;
    }

    public Pro_Of_Product(String name, String descShort, String description, String price, String nameOfMarket, String image, String idcato) {
        this.name = name;
        this.descShort = descShort;
        this.description = description;
        this.price = price;
        this.nameOfMarket = nameOfMarket;
        this.image = image;
        this.idcatogray = idcato;
    }


    public Pro_Of_Product() {
    }

    public String getPhoneOfMarket() {
        return phoneOfMarket;
    }

    public void setPhoneOfMarket(String phoneOfMarket) {
        this.phoneOfMarket = phoneOfMarket;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }


    public String getNumberQuntity() {
        return numberQuntity;
    }

    public void setNumberQuntity(String numberQuntity) {
        this.numberQuntity = numberQuntity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescShort() {
        return descShort;
    }

    public void setDescShort(String descShort) {
        this.descShort = descShort;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getNameOfMarket() {
        return nameOfMarket;
    }

    public void setNameOfMarket(String nameOfMarket) {
        this.nameOfMarket = nameOfMarket;
    }

    public String getLocationOfMarket() {
        return locationOfMarket;
    }

    public void setLocationOfMarket(String locationOfMarket) {
        this.locationOfMarket = locationOfMarket;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(name);
        dest.writeString(descShort);
        dest.writeString(description);
        dest.writeString(price);
        dest.writeString(nameOfMarket);
        dest.writeString(phoneOfMarket);
        dest.writeString(locationOfMarket);
        dest.writeString(image);
        dest.writeString(numberQuntity);
        dest.writeInt(uid);
        dest.writeString(idcatogray);
        dest.writeByte((byte) (deleivary ? 1 : 0));
    }

    public boolean isDeleivary() {
        return deleivary;
    }

    public void setDeleivary(boolean deleivary) {
        this.deleivary = deleivary;
    }
}
