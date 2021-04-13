package com.example.juliaignacyk_czw_800

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.TimeoutError
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import org.json.JSONObject

class HistoricRatesActivity : AppCompatActivity() {
    internal lateinit var todayRate: TextView
    internal lateinit var yesterdayRate: TextView
    internal lateinit var lineChartWeekly: LineChart
    internal lateinit var lineChartMonthly: LineChart
    internal lateinit var currencyCode: String
    internal lateinit var dataLast7Days: Array<Pair<String,Double>>
    internal lateinit var dataLast30Days: Array<Pair<String,Double>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historic_rates)

        todayRate = findViewById(R.id.todayRate)
        yesterdayRate = findViewById(R.id.yesterdayRate)
        lineChartWeekly = findViewById(R.id.weeklyRates)
        lineChartMonthly = findViewById(R.id.monthlyRates)
        currencyCode = intent.getStringExtra("currencyCode")!!

        DataProvider.prepare(this)
        getHistoricRates("http://api.nbp.pl/api/exchangerates/rates/A/%s/last/7/", "http://api.nbp.pl/api/exchangerates/rates/A/%s/last/30/")
        getHistoricRates("http://api.nbp.pl/api/exchangerates/rates/B/%s/last/7/", "http://api.nbp.pl/api/exchangerates/rates/B/%s/last/30/")

    }

    fun getHistoricRates(url_1: String, url_2: String){
        val queueLast7Days = DataProvider.queue
        val urlLast7Days = url_1.format(currencyCode)
        val historicRatesRequestLast7Days = JsonObjectRequest(
            Request.Method.GET, urlLast7Days, null,
            Response.Listener { response ->
                println("Success - 7 days history ")
                loadDataLast7Days(response)
                showDataLast7Days()
                getHistoricRates(url_2)
            },  Response.ErrorListener { error ->
            println(error)
            handleNetworkError(error)
        })
        queueLast7Days.add(historicRatesRequestLast7Days)

    }

    private fun getHistoricRates(url_2: String){
        val queueLast30Days = DataProvider.queue
        val urlLast30Days = url_2.format(currencyCode)
        val historicRatesRequestLast30Days = JsonObjectRequest(
                Request.Method.GET, urlLast30Days, null,
                Response.Listener { response ->
                    println("Success - 30 days history ")
                    loadDataLast30Days(response)
                    showDataLast30Days()
                }, Response.ErrorListener { error ->
            println(error)
            handleNetworkError(error)
        })
        queueLast30Days.add(historicRatesRequestLast30Days)
    }

    private fun showDataLast7Days(){
        todayRate.text = getString(R.string.todayRateText,dataLast7Days.last().second)
        yesterdayRate.text = getString(R.string.yesterdayRateText,dataLast7Days[dataLast7Days.size-2].second)
        val entries = ArrayList < Entry>()
        for ((index, element) in dataLast7Days.withIndex()){
            entries.add(Entry(index.toFloat(),element.second.toFloat()))
        }

        val lineData = LineData(LineDataSet(entries, "Kurs"))
        lineChartWeekly.data = lineData
        val description = Description()
        description.text = "Kurs %s z ostatnich 7 dni".format((currencyCode))
        lineChartWeekly.description = description
        lineChartWeekly.xAxis.valueFormatter = IndexAxisValueFormatter(dataLast7Days.map{it.first}.toTypedArray())
        lineChartWeekly.invalidate()
    }

    private fun showDataLast30Days(){
        val entries = ArrayList < Entry>()
        for ((index, element) in dataLast30Days.withIndex()){
            entries.add(Entry(index.toFloat(),element.second.toFloat()))
        }

        val lineData = LineData(LineDataSet(entries, "Kurs"))
        lineChartMonthly.data = lineData
        val description = Description()
        description.text = "Kurs %s z ostatnich 30 dni".format((currencyCode))
        lineChartMonthly.description = description
        lineChartMonthly.xAxis.valueFormatter = IndexAxisValueFormatter(dataLast30Days.map{it.first}.toTypedArray())
        lineChartMonthly.invalidate()
    }

    private fun loadDataLast7Days(response: JSONObject?) {
        response?.let {
            val rates = response.getJSONArray("rates")
            val ratesCount = rates.length()
            val tmpData = arrayOfNulls<Pair<String,Double>>(ratesCount)
            for(i in 0 until ratesCount){
                val date = rates.getJSONObject(i).getString("effectiveDate")
                val rate = rates.getJSONObject(i).getDouble("mid")
                tmpData[i] = Pair(date,rate)
            }
            this.dataLast7Days = tmpData as Array<Pair<String,Double>>
        }
    }

    private fun loadDataLast30Days(response: JSONObject?) {
        response?.let {
            val rates = response.getJSONArray("rates")
            val ratesCount = rates.length()
            val tmpData = arrayOfNulls<Pair<String,Double>>(ratesCount)
            for(i in 0 until ratesCount){
                val date = rates.getJSONObject(i).getString("effectiveDate")
                val rate = rates.getJSONObject(i).getDouble("mid")
                tmpData[i] = Pair(date,rate)
            }
            this.dataLast30Days = tmpData as Array<Pair<String,Double>>
        }
    }
    private fun handleNetworkError(error: VolleyError) {
        println("ERROR")
        println(error.networkResponse.statusCode.toString())

    }
}