package com.example.digitalclock2022k
import android.app.Service
import android.content.Intent
import android.os.IBinder

class UpdateWidgetService: Service() {

    //@Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) {
        // generates random number


    }

}