package com.elsalmcity.elsalammanger.notifecation;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface ApiServes {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAADn5zSWA:APA91bEqHE5En1IkJrgXAYmAYwMMt3IGPEygp4x61OSb_S29KfVjIhyfPJh1YeMPmzv95ZAZv5Us4W_nnHnp4kBlXmToO6V2gdxVyo4sN-t1JPlR3sHJmKn1uWm4LkRDTLa9QfdusWfT"
    })
    @POST("fcm/send")
    Call<MyResponse> sendNotifectoin(@Body Sender body);

}
