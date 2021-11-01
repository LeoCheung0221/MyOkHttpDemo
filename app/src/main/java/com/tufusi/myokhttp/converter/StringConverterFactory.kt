package com.tufusi.myokhttp.converter

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class StringConverterFactory : Converter.Factory() {
    companion object {
        private val INSTANCE = StringConverterFactory()
        fun create(): StringConverterFactory { // 静态创建方法
            return INSTANCE
        }
    }

    // 只需要重写这个方法即可, 其他方法不用覆写
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
//        return super.responseBodyConverter(type, annotations, retrofit)
        return if (type === String::class.java) { // 只处理到String的转换
            StringBodyConverter.INSTANCE
        } else { //其他类型不处理返回null
            null
        }
    }
}