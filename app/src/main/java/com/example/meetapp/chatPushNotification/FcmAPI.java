package com.example.meetapp.chatPushNotification;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface FcmAPI {
    @Headers(
            {"Content-Type:application/json", "Authorization:key=ADD HERE YOUR KEY FROM FIREBASE PROJECT"
            }
            )

    @POST("https://fcm.googleapis.com/fcm/send")
    Call<Integer> sendNotification(@Body Sender body);
}
