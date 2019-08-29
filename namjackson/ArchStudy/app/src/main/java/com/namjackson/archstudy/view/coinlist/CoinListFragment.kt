package com.namjackson.archstudy.view.coinlist

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import com.namjackson.archstudy.R
import com.namjackson.archstudy.base.BaseFragment
import com.namjackson.archstudy.databinding.FragmentCoinListBinding
import com.namjackson.archstudy.view.coinlist.adapter.CoinListAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class CoinListFragment
    : BaseFragment<FragmentCoinListBinding>(R.layout.fragment_coin_list) {

    private var timer: Timer = Timer()

    private val viewModel by viewModel<CoinListViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind {

            spinner.adapter = ArrayAdapter(
                context,
                android.R.layout.simple_spinner_item,
                arrayOf("KRW", "BTC", "ETH", "USDT")
            )

            viewmodel = viewModel
            lifecycleOwner = this@CoinListFragment.viewLifecycleOwner
            recyclerView.adapter = CoinListAdapter()
        }

        subscribeToShowToast()
    }


//    override fun onResume() {
//        super.onResume()
//        timer = Timer()
//        timer.schedule(object : TimerTask() {
//            override fun run() {
//                viewModel.loadCoinList()
//            }
//        }, (10 * SECOND), (10 * SECOND))
//    }

    override fun onPause() {
//        timer.cancel()
        super.onPause()
    }

    private fun subscribeToShowToast() {
        viewModel.showToastEvent.observe(this, androidx.lifecycle.Observer { event ->
            event?.getContentIfNotHandled()?.let {
                showToast(it)
            }
        })
    }

    companion object {
        private const val SECOND = 1000L
        fun newInstance() = CoinListFragment()
    }

}
