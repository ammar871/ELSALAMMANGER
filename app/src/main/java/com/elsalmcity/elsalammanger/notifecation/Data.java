package com.elsalmcity.elsalammanger.notifecation;

public class Data {
    private String body,title,image,click_action;


    public Data() {
    }


    public Data(String body, String title, String image, String click_action) {
        this.body = body;
        this.title = title;
        this.image = image;
        this.click_action = click_action;
    }

    public String getClick_action() {
        return click_action;
    }

    public void setClick_action(String click_action) {
        this.click_action = click_action;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
