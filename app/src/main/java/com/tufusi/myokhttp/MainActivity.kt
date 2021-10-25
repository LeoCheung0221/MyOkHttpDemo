package com.tufusi.myokhttp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import com.tufusi.myokhttp.persistentcookiejar.PersistentCookieJar
import com.tufusi.myokhttp.persistentcookiejar.cache.SetCookieCache
import com.tufusi.myokhttp.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.tufusi.myokhttp.utils.NetUtils
import kotlinx.coroutines.CoroutineScope
import okhttp3.*
import okhttp3.internal.cache.CacheInterceptor
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.sql.SQLOutput
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG: String = "MyOkHttp"
    }

    private lateinit var client: OkHttpClient
    private lateinit var call: Call
    private lateinit var context: Context

    var btnClickGetSync: Button? = null
    var btnClickGetAsync: Button? = null
    var btnClickPostAsync: Button? = null
    var btnClickPostStream: Button? = null
    var btnClickUploadFile: Button? = null
    var btnClickPostForm: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this
        initOkHttp()
        findViewId()
        initListener()
    }

    private fun initOkHttp() {
        // 构建客户端类
//        client = OkHttpClient() // 可以直接创建客户端对象
        client = OkHttpClient.Builder().build() // 也可以建造者模式创建

        // 创建Request对象
        val request = Request.Builder()
            .url("https://www.tufusi.com")
            .get()
            .build()

        // 将请求对象封装成call对象
        call = client.newCall(request)

//        client = OkHttpClient.Builder()
//                .retryOnConnectionFailure(true)
//                .build() // 设置失败重试

//        client = OkHttpClient.Builder()
//                .addNetworkInterceptor(MyCacheInterceptor()) // 设置缓存拦截器
//                .cache(
//                        Cache(
//                                File(Environment.getExternalStorageState(), "my_cache"),
//                                10 * 1024 * 1024) // 设置缓存路径和缓存大小
//                )
//                .build()

//        client = OkHttpClient.Builder() // 设置超时时间
//                .connectTimeout(1, TimeUnit.MINUTES)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(60, TimeUnit.SECONDS)
//                .build()

//        val cookieJar = PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(this))
//        client = OkHttpClient.Builder()
//                .cookieJar(cookieJar)
//                .build()
    }

    // 通过责任链模式, 为请求创建一个接收者对象的链, 解耦请求的发送者和接收者
    // 在缓存拦截器中实现 同请求缓存命中 和 无网络的缓存强制读取
    inner class MyCacheInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            var request = chain.request() // 发往网络链条中取出请求
            val response: Response
            val newResponse: Response
            if (NetUtils.isNetworkAvailable(this@MainActivity)) {
                response = chain.proceed(request) // 发往响应尾端链条中取出对象结果
                // 构建缓存控制
                val cacheControl = CacheControl.Builder()
                    .maxAge(1, TimeUnit.MINUTES)
                    .build()
                newResponse = response.newBuilder()
                    .removeHeader("Pragma") // 只有HTTP_1_1才有
                    .removeHeader("Cache-Control")
                    .addHeader("Cache-Control", cacheControl.toString())
                    .build()
            } else { // 如果没有网络, 构造出一个请求, 取出缓存值
                request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE) // 强制从缓存中取
                    .build()
                newResponse = chain.proceed(request) // 构造请求传回链头
            }
            return newResponse
        }
    }

    private fun initListener() {
        // 发送同步GET请求
        btnClickGetSync!!.setOnClickListener {
            Thread { getRequestBySync() }.start()
        }

        btnClickGetAsync!!.setOnClickListener {
            getRequestByAsync()
        }

        btnClickPostAsync!!.setOnClickListener {
            postRequestByAsync()
        }

        btnClickPostStream!!.setOnClickListener {
            postRequestByStream()
        }

        btnClickUploadFile!!.setOnClickListener {
            asyncUploadFile()
        }

        btnClickPostForm!!.setOnClickListener {
            postMultipartForm();
        }
    }

    private fun postMultipartForm() {
        val file = File(externalCacheDir, "local.png")
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("name", "tufusi")
            .addFormDataPart("age", "4")
            .addFormDataPart(
                "image",
                "avatar.png",
                RequestBody.create(MediaType.parse("image/png"), file)
            )
            .build()
        val request = Request.Builder()
            .header("Authorization", "Client-ID " + "TUFUSI")
            .url("https://tufusi.com/upload")
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("$TAG  onFailure: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                println("$TAG  onResponse: ${response.body()?.string()}")
            }
        })
    }

    private fun asyncUploadFile() {
        val url = "http://tufusi.com/img/avatar.png"
        val request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("$TAG  onFailure: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val inputStream = response.body()?.byteStream()
                val outputStream = FileOutputStream(File(externalCacheDir, "avatar_local.png"))
                val byteBuffer = ByteArray(2048)
                var len: Int
                while (inputStream?.read(byteBuffer).also {
                        len = it ?: -1
                    } != -1) {
                    outputStream.write(byteBuffer, 0, len)
                }
                outputStream.flush()
                println("$TAG  文件下载成功")
            }
        })
    }

    // 需要动态申请相关文件权限
    private fun postRequestByStream() {
        val file = File(externalCacheDir, "local.txt") // 需要上传的文件对象
        val requestBody = RequestBody.create(
            MediaType.parse("text/x-markdown;charset=utf-8"),
            file
        )
        val request = Request.Builder()
            .url("https://www.tufusi.com/upload")
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {

            }
        })
    }

    private fun postRequestByAsync() {
        val formBody = FormBody.Builder()
            .add("key", "tufusi")
            .build()
        val request = Request.Builder()
            .url("https://www.tufusi.com/someReq")
            .post(formBody)
            .build()
        call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {

                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {

                }
            }
        })
    }

    private fun getRequestByAsync() {
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("onFailure: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val result = response.body()?.string()
                println("$TAG  $result")
                // 如果要更新UI, 请回到主线程更新
            }
        })
    }

    private fun getRequestBySync() {
        val response = call.execute()
        if (response.isSuccessful) {
            println("$TAG ${response.body()?.string()}")
        } else {
            println(IOException("Unexpected code $response").message)
        }
    }

    private fun findViewId() {
        btnClickGetSync = findViewById(R.id.btn_click_get_sync)
        btnClickGetAsync = findViewById(R.id.btn_click_get_async)
        btnClickPostAsync = findViewById(R.id.btn_click_post_async)
        btnClickPostStream = findViewById(R.id.btn_click_post_stream)
        btnClickUploadFile = findViewById(R.id.btn_click_upload_file)
        btnClickPostForm = findViewById(R.id.btn_click_post_form)
    }
}