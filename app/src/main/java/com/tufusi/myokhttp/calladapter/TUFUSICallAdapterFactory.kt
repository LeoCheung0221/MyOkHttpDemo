package com.tufusi.myokhttp.calladapter

import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class TUFUSICallAdapterFactory : CallAdapter.Factory() {
    companion object {
        private val INSTANCE = TUFUSICallAdapterFactory()

        fun create(): TUFUSICallAdapterFactory {
            return INSTANCE
        }
    }

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<Any, Any>? {
        // 获取原始类型
        val rawType = getRawType(returnType)
        if (rawType == TUFUSICall::class.java && returnType is ParameterizedType) {
            val callReturnType = getParameterUpperBound(0, returnType)
            return TUFUSICallAdapter(callReturnType)
        }
        return null
    }
}