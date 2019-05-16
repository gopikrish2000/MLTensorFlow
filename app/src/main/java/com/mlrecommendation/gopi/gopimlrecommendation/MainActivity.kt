package com.mlrecommendation.gopi.gopimlrecommendation

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.mlrecommendation.gopi.gopimlrecommendation.utils.StickerSuggestionsUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textEt.visibility = View.GONE

        activateMlBtn.setOnClickListener {
            val stringInput = if (textEt.text.toString().isBlank()) "Goo" else textEt.text.toString()
            StickerSuggestionsUtils.getInstance().getRecStickers("", stringInput, showResultsTv, overallAverageTv)
        }
    }
}
