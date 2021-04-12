package com.example.juliaignacyk_czw_800

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CurrenciesListAdapter (var dataSet: MutableList<CurrencyDetails>, var context: Context): RecyclerView.Adapter<CurrenciesListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val currencyCodeTextView: TextView
        val rateTextView: TextView
        val flagView: ImageView
        val arrowView: ImageView
        init {
            currencyCodeTextView = view.findViewById(R.id.currencyCodeTextView)
            rateTextView = view.findViewById(R.id.currentRateTextView)
            flagView = view.findViewById(R.id.flagView)
            arrowView = view.findViewById(R.id.arrow)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.currency_row, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val currency = dataSet[position]
        viewHolder.currencyCodeTextView.text = currency.currencyCode
        viewHolder.rateTextView.text = currency.rate.toString()
        viewHolder.flagView.setImageResource(currency.flag)
        viewHolder.itemView.setOnClickListener{goToDetails(currency.currencyCode)}
        if (currency.getArrow()){
            viewHolder.arrowView.setImageResource(R.drawable.green)
        }else{
            viewHolder.arrowView.setImageResource(R.drawable.red)
        }
    }

    private fun goToDetails(currencyCode: String) {
        val intent = Intent(context, HistoricRatesActivity::class.java).apply{
            putExtra("currencyCode", currencyCode)
        }
        context.startActivity(intent)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size


}
