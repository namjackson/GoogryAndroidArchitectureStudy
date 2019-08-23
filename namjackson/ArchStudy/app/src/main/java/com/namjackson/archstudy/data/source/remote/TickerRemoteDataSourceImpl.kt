package com.namjackson.archstudy.data.source.remote

import com.namjackson.archstudy.data.model.Ticker
import com.namjackson.archstudy.data.source.Result
import com.namjackson.archstudy.data.source.remote.upbit.UpbitApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TickerRemoteDataSourceImpl(
    private val upbitApi: UpbitApi
) : TickerRemoteDataSource {
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getMarketAll(baseCurrency: String): Result<String> = withContext(ioDispatcher) {
        try {
            val response = upbitApi.getMarketAll()
            if (response.isEmpty()) {
                return@withContext Result.Error(Exception("NoData"))
            }

            return@withContext Result.Success(
                response.filter {
                    it.market.startsWith(baseCurrency)
                }.joinToString { it.market }
            )

        } catch (e: Exception) {
            return@withContext Result.Error(e)
        }
    }


    override suspend fun getTickers(markets: String): Result<List<Ticker>> = withContext(ioDispatcher) {
        try {
            val response = upbitApi.getTickers(markets)
            with(response) {
                if (isEmpty()) {
                    return@withContext Result.Error(Exception("NoData"))
                }
                return@withContext Result.Success(
                    sortedByDescending { it.accTradePrice24h }.map { Ticker.from(it) }
                )
            }
        } catch (e: Exception) {
            return@withContext Result.Error(e)
        }
    }

    companion object {
        private lateinit var instance: TickerRemoteDataSource
        fun getInstance(upbit: UpbitApi): TickerRemoteDataSource {
            if (!this::instance.isInitialized) {
                instance = TickerRemoteDataSourceImpl(upbit)
            }
            return instance
        }
    }
}
