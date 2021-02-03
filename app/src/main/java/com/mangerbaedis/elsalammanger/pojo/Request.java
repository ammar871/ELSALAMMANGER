package com.mangerbaedis.elsalammanger.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Request implements Parcelable {
    private String phone;
    private String name;
    private String addresse;
    private String total;
    private String comment;
    private List<Pro_Of_Product> pro_cato;

    public Request(String phone, String name, String addresse, String total, String comment, List<Pro_Of_Product> foods) {
        this.phone = phone;
        this.name = name;
        this.addresse = addresse;
        this.total = total;

        this.comment = comment;
        this.pro_cato = foods;
    }

    public Request() {
    }

    protected Request(Parcel in) {
        phone = in.readString();
        name = in.readString();
        addresse = in.readString();
        total = in.readString();
        comment = in.readString();
        pro_cato = in.createTypedArrayList(Pro_Of_Product.CREATOR);
    }

    public static final Creator<Request> CREATOR = new Creator<Request>() {
        @Override
        public Request createFromParcel(Parcel in) {
            return new Request(in);
        }

        @Override
        public Request[] newArray(int size) {
            return new Request[size];
        }
    };

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddresse() {
        return addresse;
    }

    public void setAddresse(String addresse) {
        this.addresse = addresse;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Pro_Of_Product> getFoods() {
        return pro_cato;
    }

    public void setFoods(List<Pro_Of_Product> foods) {
        this.pro_cato = foods;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(phone);
        parcel.writeString(name);
        parcel.writeString(addresse);
        parcel.writeString(total);
        parcel.writeString(comment);
        parcel.writeTypedList(pro_cato);
    }
}
