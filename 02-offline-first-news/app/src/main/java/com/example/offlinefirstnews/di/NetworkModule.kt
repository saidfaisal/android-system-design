package com.example.offlinefirstnews.di

import com.example.offlinefirstnews.BuildConfig
import com.example.offlinefirstnews.data.remote.NewsApiService
import com.example.offlinefirstnews.di.qualifier.FreeNewsApiKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
        }
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        json: Json,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.freenewsapi.io/v1/")
            .client(okHttpClient)
            .addConverterFactory(
                json.asConverterFactory(
                    "application/json".toMediaType()
                )
            ).build()
    }

    @Provides
    @Singleton
    fun provideNewsApiService(
        retrofit: Retrofit
    ): NewsApiService {
        return retrofit.create(NewsApiService::class.java)
    }

    @Provides
    @Singleton
    @FreeNewsApiKey
    fun provideFreeNewsApiKey(): String {
        return BuildConfig.FREE_NEWS_API_KEY
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @FreeNewsApiKey apiKey: String
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .header("x-api-key", apiKey)
                    .build()
                chain.proceed(request)
            }.build()
    }
}