package com.namjackson.archstudy.data.source.remote

import com.namjackson.archstudy.data.model.Ticker
import com.namjackson.archstudy.data.source.Result

interface TickerRemoteDataSource {

    suspend fun getMarketAll(
        baseCurrency: String
    ): Result<String>

    suspend fun getTickers(
        markets: String
    ): Result<List<Ticker>>

}