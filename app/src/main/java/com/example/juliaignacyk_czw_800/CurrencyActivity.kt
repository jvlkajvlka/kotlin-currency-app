package com.example.juliaignacyk_czw_800

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.TimeoutError
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest

import org.json.JSONArray

class CurrencyActivity : AppCompatActivity() {

    internal lateinit var currienciesListRecyclerView: RecyclerView
    internal lateinit var currienciesListAdapter: CurrenciesListAdapter
    internal lateinit var dataSet: MutableList<CurrencyDetails>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currency)

        currienciesListRecyclerView = findViewById(R.id.currienciesListRecyclerView)
        currienciesListRecyclerView.layoutManager = LinearLayoutManager(this)
        val tmpData : MutableList<CurrencyDetails> = mutableListOf<CurrencyDetails>()
        currienciesListAdapter = CurrenciesListAdapter(tmpData,this)
        currienciesListRecyclerView.adapter = currienciesListAdapter
        DataProvider.prepare(this)
        mainRequest()
    }

    fun mainRequest() {
        val urlTableA = "https://api.nbp.pl/api/exchangerates/tables/A?format=json"

        val currancyRatesRequestTableA = JsonArrayRequest(
                Request.Method.GET, urlTableA, null,
                Response.Listener { response ->
                    print("SUCCESS")
                    loadDataFromA(response)
                    currienciesListAdapter.notifyDataSetChanged()
                    makeRequestForTableB()
                },
                Response.ErrorListener { error ->
                    println(error)
                    handleNetworkError(error)
                })
        DataProvider.queue.add(currancyRatesRequestTableA)
    }

    fun makeRequestForTableB(){
        val urlTableB = "https://api.nbp.pl/api/exchangerates/tables/B?format=json"
        val currancyRatesRequestTableB = JsonArrayRequest(
                Request.Method.GET , urlTableB,null,
                Response.Listener { response ->
                    print("SUCCESS")
                    loadDataFromB(response)
                    currienciesListAdapter.dataSet = dataSet
                    currienciesListAdapter.notifyDataSetChanged()
                },
                Response.ErrorListener { error ->
                    println(error)
                    handleNetworkError(error)
                })
        DataProvider.queue.add(currancyRatesRequestTableB)
    }

    private fun loadDataFromA(response: JSONArray?) {
        response?.let{
            val rates = response.getJSONObject(0).getJSONArray("rates")
            val ratesCount = rates.length()
            val tmpData : MutableList<CurrencyDetails> = mutableListOf<CurrencyDetails>()

            for (i in 0 until ratesCount){
                val currencyCode = rates.getJSONObject(i).getString("code")
                val currencyRate = rates.getJSONObject(i).getDouble("mid")
                val flagID = DataProvider.getFlagForCurrency(currencyCode)
                val currencyObject = CurrencyDetails(applicationContext,currencyCode,currencyRate,flagID,"A")
                tmpData.add(currencyObject)
            }
            this.dataSet = tmpData
        }
    }

    private fun loadDataFromB(response: JSONArray?) {
        response?.let{
            val rates = response.getJSONObject(0).getJSONArray("rates")
            val ratesCount = rates.length()
            val tmpData : MutableList<CurrencyDetails> = mutableListOf<CurrencyDetails>()

            for (i in 0 until ratesCount){
                val currencyCode = rates.getJSONObject(i).getString("code")
                val currencyRate = rates.getJSONObject(i).getDouble("mid")
                val flagID = DataProvider.getFlagForCurrency(currencyCode)
                val currencyObject = CurrencyDetails(applicationContext,currencyCode,currencyRate,flagID,"B")
                tmpData.add(currencyObject)
            }
            this.dataSet.addAll(tmpData)
        }
    }

    private fun handleNetworkError(error: VolleyError) {
        println("ERROR")
        println(error.networkResponse.statusCode.toString())
//        if (error is TimeoutError){
//            currienciesListAdapter.dataSet.clear()
//            mainRequest()
//        }
    }
}