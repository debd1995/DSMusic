package com.debd.kgp.dsmusic.restClient;

import com.debd.kgp.dsmusic.model.restAPIModel.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ClientAPI {

    @GET("user/{uname}/profile/checkLogin")
    @Headers("Accept: application/json")
    Call<String> login(
            @Path("uname") String username,
            @Header("Authorization") String authString
    );

    @POST("signup")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    Call<User> signup(@Body User user);

    @GET("user/{uname}/profile/profileDetails")
    @Headers("Accept: application/json")
    Call<User> getProfile(
            @Path("uname") String username,
            @Header("Authorization") String authString
    );

}
