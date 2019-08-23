package com.namjackson.archstudy.data.source

import com.namjackson.archstudy.data.model.Ticker
import com.namjackson.archstudy.data.source.local.TickerLocalDataSource
import com.namjackson.archstudy.data.source.remote.TickerRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TickerRepository private constructor(
    val tickerLocalDataSource: TickerLocalDataSource,
    val tickerRemoteDataSource: TickerRemoteDataSource
) {
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    suspend fun getMarketAll(
        baseCurrency: String
    ): Result<String> {
        return withContext(ioDispatcher) {
            return@withContext tickerRemoteDataSource.getMarketAll(baseCurrency)
        }
    }

    suspend fun getTickers(
        markets: String
    ): Result<List<Ticker>> {
        return withContext(ioDispatcher) {
            return@withContext tickerRemoteDataSource.getTickers(markets)
        }
    }

    companion object {
        private lateinit var instance: TickerRepository
        fun getInstance(
            tickerLocalDataSource: TickerLocalDataSource,
            tickerRemoteDataSource: TickerRemoteDataSource
        ): TickerRepository {
            if (!this::instance.isInitialized) {
                instance = TickerRepository(tickerLocalDataSource, tickerRemoteDataSource)
            }
            return instance
        }
    }


}