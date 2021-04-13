package com.example.juliaignacyk_czw_800


import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

data class CurrencyDetails(val context: Context, var currencyCode: String, var rate: Double, var flag: Int = 0, var table: String) {
    internal var arrow: Boolean = false

    var queue: RequestQueue = Volley.newRequestQueue(context)

    init {
        getPastRates(table)
    }

    fun getPastRates(table: String) {
        if (table == "A"){
            val url = "http://api.nbp.pl/api/exchangerates/rates/A/%s/last/2/".format(currencyCode)

            val pastRatesRequest = JsonObjectRequest(
                    Request.Method.GET, url, null,
                    Response.Listener { response ->

                        loadData(response)
                    }, Response.ErrorListener { "ERROR" }
            )
            queue.add(pastRatesRequest)
        } else if (table == "B"){
            val url = "http://api.nbp.pl/api/exchangerates/rates/B/%s/last/2/".format(currencyCode)

            val pastRatesRequest = JsonObjectRequest(
                    Request.Method.GET, url, null,
                    Response.Listener { response ->

                        loadData(response)
                    }, Response.ErrorListener { "ERROR" }
            )
            queue.add(pastRatesRequest)
        }

    }


    private fun loadData(response: JSONObject?) {
        response?.let {
            val rates = response.getJSONArray("rates")
            val rate1 = rates.getJSONObject(0).getDouble("mid")
            val rate2 = rates.getJSONObject(1).getDouble("mid")
            this.arrow = rate2 > rate1
        }
    }

    fun getArrow(): Boolean {
        return arrow
    }

}
