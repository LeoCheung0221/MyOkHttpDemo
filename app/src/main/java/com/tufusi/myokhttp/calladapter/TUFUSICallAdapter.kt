package com.tufusi.myokhttp.calladapter

import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class TUFUSICallAdapter<R>(private val responseType: Type) : CallAdapter<R, Any> {

    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<R>): Any {
        return TUFUSICall(call)
    }
}