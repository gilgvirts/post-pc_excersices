package com.user.ex8.server;


import com.user.ex8.data.SetImage;
import com.user.ex8.data.SetUserPrettyNameRequest;
import com.user.ex8.data.TokenResponse;
import com.user.ex8.data.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserManagementInterface {
    @GET("/users/{user}/token/")
    Call <TokenResponse> getToken(@Path("user") String userName);
    @GET("/user")
    Call<UserResponse> retrieveUserInfo(@Header("Authorization") String token ,@Query("user") String userName);
    @Headers({"Content-Type: application/json"})
    @POST("/user/edit/")
    Call <SetUserPrettyNameRequest> updateUser(@Header("Authorization") String token, @Body SetUserPrettyNameRequest request);
    @Headers({"Content-Type: application/json"})
    @POST("/user/edit/")
    Call<SetImage> setImage(@Header("Authorization") String token, @Body SetImage request);
}
