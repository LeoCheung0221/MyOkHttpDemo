package com.tufusi.myokhttp.api;

import com.tufusi.myokhttp.entity.HttpResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api_CMS {


    @GET("api/app/home/getFullInfo")
    Call<HttpResponse> getFullInfo(@Query("userId") String userId);
}
