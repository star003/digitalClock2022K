package com.example.digitalclock2022k

import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.IBinder
import android.content.ComponentName
import android.util.Log


class WgService : Service() {

    val this_marker = "wg_widget service" //** зададим имя маркера для логов
    override fun onBind(intent: Intent): IBinder? {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val allWidgetIds = intent?.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
        Log.i(this_marker, "WgService -> onStartCommand")
        //1
        if (allWidgetIds != null) {
            //2
            for (appWidgetId in allWidgetIds) {
                //3
                //intent.action = "ACTION_BROADCASTWIDGETSAMPLE"
                Log.i(this_marker, "WgService -> onStartCommand-> set intent")
                val intent = Intent(this, dcWidget::class.java)
                intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

                val ids = AppWidgetManager.getInstance(application).getAppWidgetIds(
                    ComponentName(
                        application,
                        dcWidget::class.java
                    )
                )
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                sendBroadcast(intent)

                dcWidget.updateAppWidget(this, appWidgetManager, appWidgetId);
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

}