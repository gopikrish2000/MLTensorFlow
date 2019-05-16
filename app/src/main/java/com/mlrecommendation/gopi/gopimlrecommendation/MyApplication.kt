package com.mlrecommendation.gopi.gopimlrecommendation

import android.app.Application
import android.os.Handler
import android.widget.Toast

class MyApplication : Application() {
    companion object {
        var instance: MyApplication? = null
    }
    lateinit var handler:Handler;

    override fun onCreate() {
        super.onCreate()
        instance = this
        handler = Handler()
    }

    fun showToast(msg: String) {
        handler.post { Toast.makeText(this, msg, Toast.LENGTH_LONG).show() }
    }
}