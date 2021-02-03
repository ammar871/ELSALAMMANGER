package com.mangerbaedis.elsalammanger.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Product implements Parcelable {
    private String id;
    private String name;
    private String desc;
    private String address;
    private String descshort;
    private String pageFace;
    private String phoneNumber;
    private List<String> uris;
    private String image;

    protected Product(Parcel in) {
        id = in.readString();
        name = in.readString();
        desc = in.readString();
        address = in.readString();
        descshort = in.readString();
        pageFace = in.readString();
        phoneNumber = in.readString();
        uris = in.createStringArrayList();
        image = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Product() {
    }

    public Product(String name, String desc, String address, String descshort, String pageFace, String phoneNumber, String image) {
        this.name = name;
        this.desc = desc;
        this.address = address;
        this.descshort = descshort;
        this.pageFace = pageFace;
        this.phoneNumber = phoneNumber;
        this.image = image;
    }

    public Product(String id, String name, String desc, String address, String descshort, String pageFace, String phoneNumber, List<String> uris) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.address = address;
        this.descshort = descshort;
        this.pageFace = pageFace;
        this.phoneNumber = phoneNumber;
        this.uris = uris;
    }





    public String getDescshort() {
        return descshort;
    }

    public void setDescshort(String descshort) {
        this.descshort = descshort;
    }

    public String getPageFace() {
        return pageFace;
    }

    public void setPageFace(String pageFace) {
        this.pageFace = pageFace;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public List<String> getUris() {
        return uris;
    }

    public void setUris(List<String> uris) {
        this.uris = uris;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(desc);
        parcel.writeString(address);
        parcel.writeString(descshort);
        parcel.writeString(pageFace);
        parcel.writeString(phoneNumber);
        parcel.writeStringList(uris);
        parcel.writeString(image);
    }
}
