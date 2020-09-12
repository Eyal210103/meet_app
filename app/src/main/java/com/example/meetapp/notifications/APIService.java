package com.example.meetapp.notifications;

import com.example.meetapp.notifications.MyResponse;
import com.example.meetapp.notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key = AAAAcujv7pk:APA91bEtjeaKqiz3z1qUfrsKDb3HoF450sGUQ5GI46vZPis3I9Btn05CtLO2ToXqMK3nfv5PYZ0XI_l_lLVqpMvGHmNK2fmNERrrzrULKEe4Q099gL9tsk8DJx4bkZ4MTbictcNrIPNP"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
