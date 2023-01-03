package com.example.notificaretest

import android.app.Application
import re.notifica.Notificare

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Notificare.launch()
    }

}