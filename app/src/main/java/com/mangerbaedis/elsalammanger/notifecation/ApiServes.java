package com.mangerbaedis.elsalammanger.notifecation;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface ApiServes {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAvQzLMJk:APA91bF6IF-b1G5ptNuxleC5MND-xphkV4yJf-v4H2jHIuhFpPTSut1AzXNmxlFEtXtyu5D0phW-4hmjqGqzK8-3ZDbntEQaUffZ9b5lfjZJ_uvRhSVFNeQ40Dkklz13OYJ-TCXhY1Q9"
    })
    @POST("fcm/send")
    Call<MyResponse> sendNotifectoin(@Body Sender body);
    @POST("fcm/send")
    Call<MyResponse> sendNotifectoinmarket(@Body Sender body);
}
