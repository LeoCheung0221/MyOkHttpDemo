# MyOkHttpDemo
OkHttp常用方法demo

先不考虑更细节的实现原理, 我们按照这一套模板也来自定义一个数据转换器, 这个转换器实现 ResponseBody 到 String 的转换

**StringBodyConverter**
```kotlin
class StringBodyConverter : Converter<ResponseBody, String> {
    companion object {
        val INSTANCE = StringBodyConverter()
    }

    override fun convert(value: ResponseBody): String? {
        return value.string()
    }
}
```

**StringConverterFactory**
```kotlin
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
```

代码中使用, 示例如下：
```kotlin
  val retrofit1 = Retrofit.Builder()
      .baseUrl(baseUrl)
      // 这里需要注意：自定义的Converter一定要放在官方提供的Converter前面
      // addConverterFactory 是有先后顺序的, 多个 Converter 都支持同一种类型的转换的话, 只会是第一个生效, 后面源码会分析到
      .addConverterFactory(StringConverterFactory.create())
      .addConverterFactory(GsonConverterFactory.create())
      .build()
```
类似的还有其他转换都可以自定义并添加进工场集,
