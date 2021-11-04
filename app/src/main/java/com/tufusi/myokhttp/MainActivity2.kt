package com.tufusi.myokhttp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tufusi.myokhttp.api.Api_Retrofit
import com.tufusi.myokhttp.calladapter.TUFUSICallAdapterFactory
import com.tufusi.myokhttp.converter.StringConverterFactory
import com.tufusi.myokhttp.entity.HttpResponse
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.guava.GuavaCallAdapterFactory
import retrofit2.adapter.java8.Java8CallAdapterFactory
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.protobuf.ProtoConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.converter.wire.WireConverterFactory
import java.util.concurrent.TimeUnit

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val baseUrl = "https://www.tufusi.com"
        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .addConverterFactory(JacksonConverterFactory.create())
            .addConverterFactory(ProtoConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .addConverterFactory(WireConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        val apiService = retrofit.create(Api_Retrofit::class.java)

        val call = apiService.getProfileInfo("USER_ID")

//        val response: Response<HttpResponse> = call.execute()
//        if (response.isSuccessful) {
//            val dataBean = response.body()
//            println("同步请求成功-> ${dataBean.toString()}")
//        } else {
//            println("同步请求失败-> code = ${response.code()}, errMsg = ${response.message()}")
//        }

        call.enqueue(object : Callback<HttpResponse> {
            override fun onResponse(call: Call<HttpResponse>, response: Response<HttpResponse>) {
                val dataBean = response.body()
                println("异步请求成功-> ${dataBean.toString()}")
            }

            override fun onFailure(call: Call<HttpResponse>, t: Throwable) {
                println("异步请求失败-> errMsg = ${t.message}")
            }
        })

        val retrofit1 = Retrofit.Builder()
            .baseUrl(baseUrl)
            // 这里需要注意：自定义的Converter一定要放在官方提供的Converter前面
            // addConverterFactory 是有先后顺序的, 多个 Converter 都支持同一种类型的转换的话, 只会是第一个生效
            .addConverterFactory(StringConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val retrofit2 = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addCallAdapterFactory(Java8CallAdapterFactory.create())
            .addCallAdapterFactory(GuavaCallAdapterFactory.create())
            .build()

        val retrofit3 = Retrofit.Builder()
            .addCallAdapterFactory(TUFUSICallAdapterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }
}