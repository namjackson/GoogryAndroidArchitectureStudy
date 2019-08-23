package com.namjackson.archstudy.view.coinlist

import android.util.Log
import androidx.lifecycle.*
import com.namjackson.archstudy.base.BaseViewModel
import com.namjackson.archstudy.data.model.Ticker
import com.namjackson.archstudy.data.source.Result
import com.namjackson.archstudy.data.source.TickerRepository
import com.namjackson.archstudy.util.Event
import kotlinx.coroutines.launch


class CoinListViewModel(
    val tickerRepository: TickerRepository
) : BaseViewModel() {

    private val coinList = MutableLiveData<List<Ticker>>()

    private val _filterCoinList = MediatorLiveData<List<Ticker>>()
    val filterCoinList: LiveData<List<Ticker>>
        get() = _filterCoinList

    private val _searchStr = MutableLiveData<String>("")
    val searchStr: LiveData<String>
        get() = _searchStr

    var baseCurrency = MutableLiveData<String>("")

    private lateinit var markets: String

    init {
        _filterCoinList.addSource(_searchStr, Observer {
            _filterCoinList.value = filteringCoinList()
        })
        _filterCoinList.addSource(coinList, Observer {
            _filterCoinList.value = filteringCoinList()
        })
        _filterCoinList.addSource(baseCurrency, Observer {
            baseCurrency.value?.let {
                if (it.isNotEmpty()) {
                    initMarket()
                }
            }
        })
    }


    fun loadCoinList() {
        if (!this::markets.isInitialized) {
            initMarket()
        } else {
            getTickers(markets)
        }
    }

    fun initMarket() {
        _isLoading.postValue(true)

        viewModelScope.launch {
            val resultMarket = tickerRepository.getMarketAll(baseCurrency.value ?: "")
            if (resultMarket is Result.Success) {
                markets = resultMarket.data
                getTickers(markets)
            } else {
                _showToastEvent.value = Event(resultMarket.toString())
                _isLoading.value = false
            }
        }

    }

    fun getTickers(markets: String) {
        viewModelScope.launch {
            val resultList = tickerRepository.getTickers(markets)
            if (resultList is Result.Success) {
                coinList.value = resultList.data
            } else {
                _showToastEvent.value = Event(resultList.toString())
            }
            _isLoading.value = false
        }

    }

    fun changeSearch(searchStr: CharSequence) {
        _searchStr.value = searchStr.toString()
    }

    private fun filteringCoinList(): List<Ticker>? =
        coinList.value?.filter { it.name.contains(searchStr.value?.toUpperCase().toString()) }
}