package com.namjackson.archstudy.data.source

import com.namjackson.archstudy.data.model.Ticker
import com.namjackson.archstudy.data.source.local.TickerLocalDataSource
import com.namjackson.archstudy.data.source.remote.TickerRemoteDataSource
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TickerRepository private constructor(
    val tickerLocalDataSource: TickerLocalDataSource,
    val tickerRemoteDataSource: TickerRemoteDataSource
) {
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    suspend fun getMarketAll(
        baseCurrency: String
    ): Flow<Result<String>> = withContext(ioDispatcher) {
        flow {
            emit(Result.Loading)
            emit(tickerRemoteDataSource.getMarketAll(baseCurrency))
        }
    }

    fun getTickers(
        markets: String
    ): ReceiveChannel<Result<List<Ticker>>> = CoroutineScope(ioDispatcher).produce {
        send(Result.Loading)
        while (isActive) {
            send(tickerRemoteDataSource.getTickers(markets))
            delay(10_000L)
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

