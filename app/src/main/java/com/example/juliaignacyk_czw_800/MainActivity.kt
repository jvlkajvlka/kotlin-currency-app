package com.example.juliaignacyk_czw_800
// Julia Ignacyk: 303111
// WYkonałam wszytskie punkty
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {
    internal lateinit var currencyButton: Button
    internal lateinit var goldButton: Button
    internal lateinit var calcButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        goldButton = findViewById(R.id.goldButton)
        currencyButton = findViewById(R.id.currencyButton)
        calcButton = findViewById(R.id.converterButton)

        println(goldButton.text)
        goldButton.setOnClickListener {
            val intent = Intent(this,GoldActivity::class.java)
            println("gold button")
            startActivity(intent)
        }

        currencyButton.setOnClickListener {
            println("currency button")
            val intent = Intent(this,CurrencyActivity::class.java)
            startActivity(intent)
        }

        calcButton.setOnClickListener {
            println("converter button")
            val intent = Intent(this, ConverterActivity::class.java)
            startActivity(intent)
        }


    }
}