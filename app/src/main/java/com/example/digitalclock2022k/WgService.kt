package com.example.digitalclock2022k

import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.IBinder

class WgService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val allWidgetIds = intent?.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
        //1
        if (allWidgetIds != null) {
            //2
            for (appWidgetId in allWidgetIds) {
                //3
                intent.action = "ACTION_BROADCASTWIDGETSAMPLE"
                dcWidget.updateAppWidget(this, appWidgetManager, appWidgetId);
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

}