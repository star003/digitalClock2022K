package com.example.digitalclock2022k
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.content.ComponentName
import android.app.PendingIntent
import android.os.AsyncTask
import android.util.Log
import android.widget.TextView
import com.example.digitalclock2022k.gisFromSite.this_marker
import java.lang.NumberFormatException

class dcWidget : AppWidgetProvider() {

    val this_marker = "wg_widget" //** зададим имя маркера для логов
    //private val ACTION_SIMPLEAPPWIDGET = "ACTION_BROADCASTWIDGETSAMPLE"
    lateinit var ct: Context

    companion object {

        fun updateAppWidget(
            context: Context, appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            Log.i(this_marker, "----welcome widget----")
            val views = RemoteViews(context.packageName, R.layout.simple_app_widget)
            val intent = Intent(context, dcWidget::class.java)
            intent.action = "ACTION_BROADCASTWIDGETSAMPLE"
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            views.setOnClickPendingIntent(R.id.tvWidget, pendingIntent)
            //views.setOnClickPendingIntent(R.id.tvWidgetRoom, pendingIntent)
            //views.setOnClickPendingIntent(R.id.tvWidgetPRS, pendingIntent)
            appWidgetManager.updateAppWidget(appWidgetId, views)

        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        Log.i(this_marker, "widget: onUpdate")
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Log.i(this_marker, "widget: onReceive")
        //if (ACTION_SIMPLEAPPWIDGET.equals(intent.action)) {
        ct = context
        Log.i(this_marker, "widget: onReceive -> ACTION_SIMPLEAPPWIDGET")
        val views = RemoteViews(context.packageName, R.layout.simple_app_widget)
        views.setTextViewText(R.id.tvWidget, "Wait...")
        views.setTextViewText(R.id.tvWidgetPRS, "Wait...")
        views.setTextViewText(R.id.tvWidgetRoom, "Wait...")
        val m = goMyData();
        m.execute()
        val appWidget = ComponentName(ct, dcWidget::class.java)
        val appWidgetManager = AppWidgetManager.getInstance(ct)
        //***моожет после выполнения повторить ?
        appWidgetManager.updateAppWidget(appWidget, views)
        //}
    }

    inner class goMyData : AsyncTask<Void, Void, java.lang.String>()  {

        var room = ""
        var prs = ""
        var data = ""

        override fun onPreExecute() {
            super.onPreExecute()
        } //protected void onPreExecute()

        /////////////////////////////////////////////////////////////////////////////////////
        override fun doInBackground(vararg params: Void?): java.lang.String? {

            val a = tst.myData()
            data = if (a.size > 0) a[5] else "-"
            prs = if (a.size > 0) a[2] else "-"
            room = if (a.size > 0) a[1] else "-"
            return null

        } //protected Void doInBackground(Void... params)

        /////////////////////////////////////////////////////////////////////////////////////
        override protected fun onPostExecute(result: java.lang.String?) {
            try {
                val views = RemoteViews(ct.packageName, R.layout.simple_app_widget)
                views.setTextViewText(R.id.tvWidget, data)
                views.setTextViewText(R.id.tvWidgetPRS, prs)
                views.setTextViewText(R.id.tvWidgetRoom, room)
                val appWidget = ComponentName(ct, dcWidget::class.java)
                val appWidgetManager = AppWidgetManager.getInstance(ct)
                //***моожет после выполнения повторить ?
                appWidgetManager.updateAppWidget(appWidget, views)
                Log.i(this_marker, "widget ->goMyData -> onPostExecute: " + room+" , "+prs+ " , "+data)

            } catch (e3: NumberFormatException) {
                Log.e(
                    this_marker,
                    "ошибка вывода в форму, представление строка - число (class goMyData) "
                )
            }
        } //protected void onPostExecute(Void result)
    } //class goUsd extends AsyncTask<Void, Void, Void>
}//    dcWidget
