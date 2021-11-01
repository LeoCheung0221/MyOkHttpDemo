package com.tufusi.myokhttp.api

import com.tufusi.myokhttp.entity.HttpResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface Api_Retrofit {

    // 添加单个参数
//    @Headers("Cache-Control: max-age=640000")

    // 添加多个Header参数
    @Headers(
        "Accept: application/vnd.yourapi.v1.full+json",
        "User-Agent: Your-App-Name"
    )

    @GET("api/app/home/getProfileInfo")
    fun getProfileInfo(@Query("userId") userId: String?): Call<HttpResponse>

    @POST("api/app/info/submitInfo")
    fun submitInfo(@Body body: RequestBody): Call<HttpResponse>

    @HEAD("/api/app/user/info")
    fun getUserHeader(): Call<HttpResponse?>?

    @HTTP(method = "GET", path = "api/app/home/getProfileInfo/{userId}", hasBody = false)
    fun getProfileInfo1(@Path("userId") userId: String): Call<HttpResponse>

    @HTTP(method = "GET", hasBody = false)
    fun getProfileInfo2(@Url url: String): Call<HttpResponse>


    @FormUrlEncoded
    @POST("api/app/info/submitInfo")
    fun submitInfo2(
        @Field("name") name: String,
        @FieldMap params: Map<String, Any>
    ): Call<HttpResponse>

    @Multipart
    @POST("api/app/repo/unloadFile")
    fun uploadFile(
        @Part("userId") userId: String,
        @Part("file") file: MultipartBody.Part
    ): Call<HttpResponse>

    @Streaming
    @GET
    fun getImageStream(@Url url: String): Call<ResponseBody>


//    @GET("api/app/prod/getProductList")
//    fun getProductList1(
//        @Query("pageSize") pageSize: Int?,
//        @Query("currentPage") currentPage: Int?
//    ): Call<List<ProductBean>>
//
//    @GET("api/app/prod/getProductList")
//    fun getProductList2(
//        @QueryMap params: Map<String, Any>
//    ): Call<List<ProductBean>>

    @GET("api/app/home/getProfileInfo/{userId}")
    fun getProfileInfo3(@Path("userId") userId: String?): Call<HttpResponse>

    // 图片文件上传
    @Multipart
    @POST("api/app/upload/imgFile")
    fun uploadImgFile(
        @Part("userId") userId: RequestBody?,
        @Part("file") file: MultipartBody.Part,
        @PartMap params: Map<String, RequestBody?>
    ): Call<ResponseBody>

    @Multipart
    @POST("api/app/upload/files")
    fun uploadFiles(
        @Part("userId") userId: RequestBody?,
        @Part files: List<MultipartBody.Part>
    ): Call<ResponseBody>

}