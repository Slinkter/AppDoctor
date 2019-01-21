package com.cudpast.app.doctor.doctorApp.Remote;

import com.cudpast.app.doctor.doctorApp.Model.FCMResponse;
import com.cudpast.app.doctor.doctorApp.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAXPtHyeE:APA91bF4hnrk3mNyNRxQ1uw7z9khEPGvEbQvVQugq2djQ9rKjUrmkF_g8TxnOvvUmtVU1N_J22cCzEckjkW6sIj30_GtaSN6RJ7AfHnsAbSwPd59pXP1GZOerTAwhM7doV1FNzkaZe7N"
    })

    @POST("fcm/send")
    Call<FCMResponse> sendMessage(@Body Sender body);
}
