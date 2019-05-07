package com.mlrecommendation.gopi.gopimlrecommendation

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.mlrecommendation.gopi.gopimlrecommendation.utils.StickerSuggestionsUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        activateMlBtn.setOnClickListener { textEt.text.toString().takeIf { it.isNotBlank() && it.length > 2 }?.run {
            StickerSuggestionsUtils.getInstance().getRecStickers("", this)
        } ?: Toast.makeText(this,"length less than 2", Toast.LENGTH_SHORT).show() }
    }
}
