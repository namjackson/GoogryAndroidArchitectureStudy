package com.namjackson.archstudy.data.source.remote.upbit

import com.namjackson.archstudy.data.source.remote.upbit.response.UpbitMarket
import com.namjackson.archstudy.data.source.remote.upbit.response.UpbitTickerResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface UpbitApi {

    @GET(value = "market/all")
    suspend fun getMarketAll(): List<UpbitMarket>

    @GET(value = "ticker")
    suspend fun getTickers(@Query("markets") markets: String): List<UpbitTickerResponse>
}