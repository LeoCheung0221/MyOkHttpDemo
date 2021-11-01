package com.tufusi.myokhttp.calladapter

import retrofit2.Call
import java.io.IOException

class TUFUSICall<T>(private var call: Call<T>) {

    @Throws(IOException::class)
    fun get(): T? {
        return call.execute().body() // 请求同步执行返回响应实体
    }
}