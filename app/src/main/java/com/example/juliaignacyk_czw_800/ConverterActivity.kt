package com.example.juliaignacyk_czw_800

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.TimeoutError
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import org.json.JSONArray

class ConverterActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener{
//z polskich na inne
    internal lateinit var spinnerFromPLN: Spinner
    internal lateinit var PLNInput: EditText
    internal lateinit var otherCurOutput: TextView
    internal lateinit var buttonFromPLN: Button
// z innych na polskie
    internal lateinit var spinnerToPLN: Spinner
    internal lateinit var otherInput: TextView
    internal lateinit var PLNOutput: EditText
    internal lateinit var buttonToPLN: Button
// pomocniczne Array
    internal lateinit var dataSetCode: MutableList<String?>
    internal lateinit var data: MutableList<Pair<String,Double>>
// zmienne
    internal var currentCurFromPLN: Double = 0.0
    private var plnValueText: Double = 0.0
    private var otherValueText: Double = 0.0
    internal var currentCurToPLN: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_converter)

        spinnerFromPLN = findViewById(R.id.spinnerFromPLN)
        PLNInput = findViewById(R.id.PLNInput)
        otherCurOutput = findViewById(R.id.otherCurOutput)
        buttonFromPLN = findViewById(R.id.buttonFromPLN)
        buttonFromPLN.setOnClickListener { calculateFromPLNfunction() }

        spinnerToPLN = findViewById(R.id.spinnerToPLN)
        otherInput = findViewById(R.id.PLNOutput)
        PLNOutput = findViewById(R.id.otherInput)
        buttonToPLN = findViewById(R.id.buttonToPLN)
        buttonToPLN.setOnClickListener { calculateToPLNfunction() }

        DataProvider.prepare(this)
        makeRequestTableA()
    }

    private fun makeRequestTableA(){
        val urlTableA = "https://api.nbp.pl/api/exchangerates/tables/A?format=json"
        val currancyRatesRequestTableA = JsonArrayRequest(
                Request.Method.GET , urlTableA,null,
                Response.Listener { response ->
                    print("SUCCESS")
                    loadDataFromA(response)
                    makeRequestTableB()
                },
                Response.ErrorListener { error ->
                    println(error)
                    handleNetworkError(error)
                })
        DataProvider.queue.add(currancyRatesRequestTableA)
    }

    private fun makeRequestTableB(){
        val urlTableB = "https://api.nbp.pl/api/exchangerates/tables/B?format=json"
        val currancyRatesRequestTableB = JsonArrayRequest(
                Request.Method.GET , urlTableB,null,
                Response.Listener { response ->
                    print("SUCCESS")
                    loadDataFromB(response)
                },
                Response.ErrorListener { error ->
                    println(error)
                    handleNetworkError(error)
                })
        DataProvider.queue.add(currancyRatesRequestTableB)
    }

    private fun loadDataFromA(response: JSONArray?) {
        response?.let {
            val rates = response.getJSONObject(0).getJSONArray("rates")
            val ratesCount = rates.length()
            val tmpCode : MutableList<String?> = mutableListOf<String?>()
            val tmpD :MutableList<Pair<String,Double>>  = mutableListOf<Pair<String,Double>>()

            for(i in 0 until ratesCount){
                val currencyCode = rates.getJSONObject(i).getString("code")
                val currencyRate = rates.getJSONObject(i).getDouble("mid")
                tmpCode.add(currencyCode)
                tmpD.add(Pair(currencyCode,currencyRate))
            }
            this.dataSetCode = tmpCode
            this.data = tmpD
        }
    }

    private fun loadDataFromB(response: JSONArray?) {
        response?.let {
            val rates = response.getJSONObject(0).getJSONArray("rates")
            val ratesCount = rates.length()
            val tmpCode : MutableList<String?> = mutableListOf<String?>()
            val tmpD :MutableList<Pair<String,Double>>  = mutableListOf<Pair<String,Double>>()

            for(i in 0 until ratesCount){
                val currencyCode = rates.getJSONObject(i).getString("code")
                val currencyRate = rates.getJSONObject(i).getDouble("mid")
                tmpCode.add(currencyCode)
                tmpD.add(Pair(currencyCode,currencyRate))
            }

            this.dataSetCode.addAll(tmpCode)
            this.data.addAll(tmpD)
        }
        val adapter = ArrayAdapter(this,android.R.layout.simple_spinner_item, dataSetCode)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFromPLN.adapter = adapter
        spinnerFromPLN.onItemSelectedListener = this
        spinnerToPLN.adapter = adapter
        spinnerToPLN.onItemSelectedListener = this
    }

    private fun handleNetworkError(error: VolleyError) {
        println(error.networkResponse.statusCode.toString())
        if (error is TimeoutError){
            this.dataSetCode.clear()
            this.data.clear()
            makeRequestTableA()
        }
    }

    private fun calculateToPLNfunction() {
        otherValueText = PLNOutput.text.toString().toDouble()
        val rounded = String.format("%.2f",(otherValueText * currentCurToPLN))
        otherInput.text = rounded
    }

    private fun calculateFromPLNfunction() {
        plnValueText = PLNInput.text.toString().toDouble()
        val rounded = String.format("%.2f", (plnValueText / currentCurFromPLN))
        otherCurOutput.text = rounded
    }

    override fun onItemSelected(arg0: AdapterView<*>?, arg1: View, position: Int, id: Long) {
        val items = spinnerFromPLN.selectedItem.toString()
        val items2 = spinnerToPLN.selectedItem.toString()
        for ((index, element) in data.withIndex()) {
            if(element.first == items){
                this.currentCurFromPLN = element.second
                this.currentCurToPLN = element.second
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}