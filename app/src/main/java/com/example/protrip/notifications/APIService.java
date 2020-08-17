package com.example.protrip.notifications;

import com.example.protrip.notifications.MyResponse;
import com.example.protrip.notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAEfVlGPs:APA91bFghjeDUdbrClZEcrD4S_fXAR6rro8CXEG0dSTv7p0zSFJTbwHDz8S4cf936kYTVn_HG7EAQ7HQHnpJMLIkqgiRGt_A-Oj89EIsQErU9lUKXl2WnN7VmWrDXyjukf0vxGUTn-eP"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
