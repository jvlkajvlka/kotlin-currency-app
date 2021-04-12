package com.example.juliaignacyk_czw_800

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.blongho.country_data.Country
import com.blongho.country_data.World
import com.blongho.country_data.World.getAllCountries


object DataProvider {
    private lateinit var data: Array<CurrencyDetails>
    lateinit var queue: RequestQueue
    private lateinit var countries : List<Country>
    fun prepare(context: Context){
        queue = Volley.newRequestQueue(context)
        World.init(context)
        countries = getAllCountries().distinctBy{ it.currency.code}
    }

    fun getFlagForCurrency(currencyCode: String):Int{
        if(currencyCode == "USD"){
            return R.drawable.us
        }else if(currencyCode == "EUR"){
            return R.drawable.eu
        }else if(currencyCode == "GBP"){
            return R.drawable.gb
        }else if(currencyCode == "CHF"){
            return R.drawable.ch
        }else if(currencyCode == "HKD"){
            return R.drawable.hk
        }else if(currencyCode == "IDR") {
            return R.drawable.id
        }else{
            return countries.find{ it.currency.code == currencyCode }?.flagResource ?: World.getWorldFlag()
        }
    }

    fun getData(): Array<CurrencyDetails>{
        return data
    }

    fun getOneRow(position: Int):CurrencyDetails{
        return data[position]
    }
}