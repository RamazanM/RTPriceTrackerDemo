package com.ramazanm.rtpricetrackerdemo.di

import com.google.gson.Gson
import com.ramazanm.rtpricetrackerdemo.BuildConfig
import com.ramazanm.rtpricetrackerdemo.data.repository.EchoStockRepositoryImpl
import com.ramazanm.rtpricetrackerdemo.data.repository.StockRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }
    @Provides
    @Singleton
    fun provideStockRepository(): StockRepository {
        return EchoStockRepositoryImpl(
            ioDispatcher = Dispatchers.IO,
            baseUrl = BuildConfig.BASE_URL,
            gson = Gson()
        )
    }

}