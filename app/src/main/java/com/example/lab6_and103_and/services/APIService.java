package com.example.lab6_and103_and.services;

import com.example.lab6_and103_and.model.Fruits;
import com.example.lab6_and103_and.model.Response;
import com.example.lab6_and103_and.model.User;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public interface APIService {
    String DOMAIN = "http://10.0.2.2:3000/";

    @GET("/list")
    Call<ArrayList<Fruits>> getFruits();

    @Multipart
    @POST("/add-fruits")
    Call<Fruits> addFruits(@PartMap Map<String, RequestBody> requestBodyMap,
                           @Part ArrayList<MultipartBody.Part> ds);

    @Multipart
    @POST("/register-send-email")
    Call<Response<User>> register(
            @Part("username") RequestBody username,
            @Part("password") RequestBody password,
            @Part("email") RequestBody email,
            @Part("name") RequestBody name,
            @Part MultipartBody.Part avartar
    );

    @POST("/login")
    Call<Response<User>> login (@Body User user);

    @DELETE("/delete/{id}")
    Call<Fruits> deleteFruits(@Path("id") String id);

    @Multipart
    @PUT("/update-fruit/{id}")
    Call<Fruits> update(@PartMap Map<String, RequestBody> requestBodyMap,
                                                   @Path("id") String id,
                                                   @Part ArrayList<MultipartBody.Part> ds
    );

    @Multipart
    @PUT("/update-no-image/{id}")
    Call<Fruits> updateNoImage(@PartMap Map<String, RequestBody> requestBodyMap,
                        @Path("id") String id
    );
}
