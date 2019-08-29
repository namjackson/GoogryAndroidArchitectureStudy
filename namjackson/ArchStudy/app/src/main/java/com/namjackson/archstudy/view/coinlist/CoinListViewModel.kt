package com.namjackson.archstudy.view.coinlist

import androidx.lifecycle.*
import com.namjackson.archstudy.base.BaseViewModel
import com.namjackson.archstudy.data.model.Ticker
import com.namjackson.archstudy.data.source.Result
import com.namjackson.archstudy.data.source.TickerRepository
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
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

    private fun initMarket() {
        viewModelScope.launch {
            val resultMarket = tickerRepository.getMarketAll(baseCurrency.value ?: "")
            resultMarket.catch { e ->
                showError(e.toString())
            }.collect {
                resultLoad(it) {
                    markets = it
                    getTickers(markets)
                }
            }
        }

    }

    private lateinit var channel: ReceiveChannel<Result<List<Ticker>>>

    private fun getTickers(markets: String) {
        if (this::channel.isInitialized) {
            channel.cancel()
        }
        viewModelScope.launch {
            channel = tickerRepository.getTickers(markets)
            channel.consumeAsFlow()
                .catch { e -> showError(e.toString()) }
                .collect {
                    resultLoad(it) {
                        _isLoading.value = false
                        coinList.value = it
                    }
                }
        }

    }


    fun changeSearch(searchStr: CharSequence) {
        _searchStr.value = searchStr.toString()
    }

    private fun filteringCoinList(): List<Ticker>? =
        coinList.value?.filter { it.name.contains(searchStr.value?.toUpperCase().toString()) }

    override fun onCleared() {
        super.onCleared()
        if (this::channel.isInitialized) {
            channel.cancel()
        }
    }
}