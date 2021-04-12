package com.example.juliaignacyk_czw_800


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import org.json.JSONArray

class GoldActivity : AppCompatActivity() {
    internal lateinit var todayGoldRate: TextView
    internal lateinit var lineChart: LineChart
    internal lateinit var data: Array<Pair<String,Double>>

    lateinit var queueTodayGoldCost: RequestQueue
    lateinit var queueHistoryGoldCost: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gold)

        todayGoldRate = findViewById(R.id.goldRate)
        lineChart = findViewById(R.id.goldChart)

        queueTodayGoldCost = Volley.newRequestQueue(this)
        queueHistoryGoldCost = Volley.newRequestQueue(this)
        getGoldRates()

    }

    private fun getGoldRates() {
        val urlTodayGoldCost = "http://api.nbp.pl/api/cenyzlota?format=json"
        val urlHistoryGoldCost = "http://api.nbp.pl/api/cenyzlota/last/30?format=json"


        val goldRate = JsonArrayRequest(
                Request.Method.GET, urlTodayGoldCost, null,
                Response.Listener { response ->
                    println("Success url1")
                    loadGoldRate(response)
                },
                Response.ErrorListener { error ->
                    handleNetworkError(error)
                }
        )

        val historicRates = JsonArrayRequest(
                Request.Method.GET, urlHistoryGoldCost, null,
                Response.Listener { response ->
                    println("Success url2")
                    loadHistoric(response)
                    showData()
                },
                Response.ErrorListener { error ->
                    handleNetworkError(error)
                }
        )


        queueTodayGoldCost.add(goldRate)
        queueHistoryGoldCost.add(historicRates)
    }

    private fun showData(){
        val entries = ArrayList <Entry>()
        for ((index, element) in data.withIndex()){
            entries.add(Entry(index.toFloat(),element.second.toFloat()))
        }
        val lineData = LineData(LineDataSet(entries, "Kurs"))
        lineChart.data = lineData
        val description = Description()
        description.text = "Kurs złota z ostatnich 30 dni"
        lineChart.description = description
        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(data.map{it.first}.toTypedArray())
        lineChart.invalidate()
    }

    private fun loadGoldRate(response: JSONArray?) {
        response?.let {
            val rate = response.getJSONObject(0).getDouble("cena")
            todayGoldRate.text = getString(R.string.todayGoldRate,rate)
        }
    }


    private fun loadHistoric(response: JSONArray?) {
        response?.let {
            val rates = response
            val ratesCount = rates.length()
            val tmpData = arrayOfNulls<Pair<String,Double>>(ratesCount)
            for(i in 0 until ratesCount){
                val date = rates.getJSONObject(i).getString("data")
                val rate = rates.getJSONObject(i).getDouble("cena")

                tmpData[i] = Pair(date,rate)
            }
            this.data = tmpData as Array<Pair<String,Double>>
        }
    }

    private fun handleNetworkError(error: VolleyError) {
        println(error.networkResponse.statusCode.toString())
    }
}