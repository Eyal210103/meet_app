package com.example.meetapp.notifications2;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key = AAAAcujv7pk:APA91bHMFEod8roanRDiRaKrzMvFe6IGCSjL7jbvH9S_MUwLKadw3Jmh8qBXAm07bzfYPP_IJmV_wfZEciOuHJqhpwY6IWxMI5obFAhSIXmMtBzcOqAvQBoTWENYZ6KnTS6eon-Rm1GD"
            }
    )

    @POST("https://fcm.googleapis.com/fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
