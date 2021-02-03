package com.mangerbaedis.elsalammanger.notifecation;

import com.google.gson.annotations.SerializedName;

public class Sender {
    @SerializedName("to")
    private String to;

    @SerializedName("data")
    private Data data;

    public Sender() {
    }

    public Sender(String to, Data data) {
        this.to = to;
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
