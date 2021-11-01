package com.tufusi.myokhttp.converter

import okhttp3.ResponseBody
import retrofit2.Converter

class StringBodyConverter : Converter<ResponseBody, String> {

    companion object {
        val INSTANCE = StringBodyConverter()
    }

    override fun convert(value: ResponseBody): String? {
        return value.string()
    }
}