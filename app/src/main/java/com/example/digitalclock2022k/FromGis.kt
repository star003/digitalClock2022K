package com.example.digitalclock2022k

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.util.Log
import android.view.WindowManager
import android.widget.ImageView

import android.widget.TextView
import java.util.*
import kotlin.collections.ArrayList
import android.view.Menu
//import android.R
import android.view.MenuItem
import android.os.AsyncTask
import java.io.IOException
import android.graphics.Bitmap

import android.graphics.BitmapFactory
import java.io.InputStream
import java.lang.Exception
import java.net.URL


class FromGis : AppCompatActivity() {
    var _fields = ArrayList<TextView?>()
    var _fieldsI: ArrayList<ImageView?> = ArrayList<ImageView?>()

    var sc = 0
    val this_marker = "FromGis" //** зададим имя маркера для логов
    var this_small = true
    private val APP_PREFERENCES = "digitalClock"
    private val mSettings: SharedPreferences? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_from_gis)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        /*
		 * 0-3 заголовки
		 * 4-16  -облака
		 * 17-29 -осадки
		 * 30-42 - температура
		 * 43-55 -давление
		 * 56-68 -влажность
		 * 69-81 - ветер
		 * 82-94 - ощущения
		 */
        val fldId: List<Int> = Arrays.asList(
            R.id.gsDataCurr, R.id.gsDataDay1, R.id.gsDataDay2, R.id.gsDataDay3,
            R.id.gsCl1, R.id.gsCl2, R.id.gsCl3, R.id.gsCl4,
            R.id.gsCl5, R.id.gsCl6, R.id.gsCl7, R.id.gsCl8,

            R.id.gsCl9, R.id.gsCl10, R.id.gsCl11, R.id.gsCl12,
            R.id.gsCl13, R.id.gsPer1, R.id.gsPer2, R.id.gsPer3,
            R.id.gsPer4, R.id.gsPer5, R.id.gsPer6, R.id.gsPer7,

            R.id.gsPer8, R.id.gsPer9, R.id.gsPer10, R.id.gsPer11,
            R.id.gsPer12, R.id.gsPer13, R.id.gsTmp1, R.id.gsTmp2,
            R.id.gsTmp3, R.id.gsTmp4, R.id.gsTmp5, R.id.gsTmp6,

            R.id.gsTmp7, R.id.gsTmp8, R.id.gsTmp9, R.id.gsTmp10,
            R.id.gsTmp11, R.id.gsTmp12, R.id.gsTmp13, R.id.gsPrs1,
            R.id.gsPrs2, R.id.gsPrs3, R.id.gsPrs4, R.id.gsPrs5,

            R.id.gsPrs6, R.id.gsPrs7, R.id.gsPrs8, R.id.gsPrs9,
            R.id.gsPrs10, R.id.gsPrs11, R.id.gsPrs12, R.id.gsPrs13,
            R.id.gsWic1, R.id.gsWic2, R.id.gsWic3, R.id.gsWic4,

            R.id.gsWic5, R.id.gsWic6, R.id.gsWic7, R.id.gsWic8,
            R.id.gsWic9, R.id.gsWic10, R.id.gsWic11, R.id.gsWic12,
            R.id.gsWic13, R.id.gsWin1, R.id.gsWin2, R.id.gsWin3,

            R.id.gsWin4, R.id.gsWin5, R.id.gsWin6, R.id.gsWin7,
            R.id.gsWin8, R.id.gsWin9, R.id.gsWin10, R.id.gsWin11,
            R.id.gsWin12, R.id.gsWin13, R.id.gsRto1, R.id.gsRto2,

            R.id.gsRto3, R.id.gsRto4, R.id.gsRto5, R.id.gsRto6,
            R.id.gsRto7, R.id.gsRto8, R.id.gsRto9, R.id.gsRto10,
            R.id.gsRto11, R.id.gsRto12, R.id.gsRto13
        )
        /*
		 * разберем на два вида полей
		 * текстовое и графическое
		 * с 0 по 3 и 30 до конца - это текст
		 * с 4 по 29 - картинки
		 */
        //Log.i( "$this_marker fldId.size() " + fldId.size.toString())
        for (i in fldId.indices) {
            if ((i < 4) or (i > 29)) {
                val fff = findViewById(fldId[i]) as TextView
                _fields.add(fff)
                _fieldsI.add(null)
            } else {
                _fields.add(null)
                val ggg = findViewById(fldId[i]) as ImageView
                _fieldsI.add(ggg)
            }
        }
        var mt = goGis()
        mt.execute()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.from_gis, menu)
        return true
    } //public boolean onCreateOptionsMenu(Menu menu)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.getItemId()
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    } //public boolean onOptionsItemSelected(MenuItem item)

    inner class goGis : AsyncTask<Void, Void, java.lang.String>(){

        var _stringData = ArrayList<String>()

        override fun onPreExecute() {
            super.onPreExecute()
        } //protected void onPreExecute()

        /////////////////////////////////////////////////////////////////////////////////////
        override fun doInBackground(vararg params: Void?): java.lang.String? {
            //var x = ArrayList<ArrayList<String>>()
            Log.i(this_marker, "FromGis -> class goGis-> doInBackground")
            try {
                var x = tst.grabGismeteo()
                var xsize: Int =x.size
                Log.i(this_marker, "FromGis -> class goGis-> doInBackground ->grabGismeteo x = $xsize")
                for (a in x) {
                    for (h in a) {
                        var asize: Int =a.size
                        Log.i(this_marker, "FromGis -> class goGis-> doInBackground ->grabGismeteo x = $asize")
                        _stringData.add(h)
                        Log.i(this_marker, "FromGis -> class goGis-> doInBackground ->_stringData.add()")
                    }
                }
            } catch (e: IOException) {
                Log.e(this_marker, "error tst.grabGismeteo() in class goGis")
                Log.e(this_marker, e.toString())

            }

            return null
        } //protected Void doInBackground(Void... params)

        /////////////////////////////////////////////////////////////////////////////////////
        override protected fun onPostExecute(result: java.lang.String?) {
            Log.i(this_marker, "FromGis -> class goGis-> protected fun onPostExecute")
            super.onPostExecute(result)

            for (i1 in 0 until _stringData.size) {
                if ((i1 < 4) or (i1 > 29)) {
                    Log.i(this_marker, "FromGis -> class goGis-> protected fun onPostExecute i1=$i1")
                    //**вывод текстовой информации
                    if (_stringData[i1].contains("ФАКТ") and this_small) {
                        _fields.get(i1)?.setText("Ф")
                    } else {
                        _fields.get(i1)?.setText(_stringData[i1])
                    }
                } else {
                    //**вывод  графической информации
                    var ggg: ImageView? = _fieldsI.get(i1)
                    var d = DownloadImageTask()
                    d.gDownloadImageTask(ggg)
                    d.execute(_stringData[i1])
                }
            }
        } //protected void onPostExecute(Void result)
    } //class goGis extends AsyncTask<Void, Void, Void>

    inner class DownloadImageTask :AsyncTask<String, Void, Bitmap> (){
        var bmImage: ImageView? = null

        fun gDownloadImageTask(bmImage: ImageView?) {
            this.bmImage = bmImage
        } //public DownloadImageTask(ImageView bmImage)

        protected override fun doInBackground(vararg urls: String): Bitmap? {
            val urldisplay = urls[0]
            var mIcon11: Bitmap? = null
            try {
                val inn: InputStream = URL(urldisplay).openStream()
                mIcon11 = BitmapFactory.decodeStream(inn)
            } catch (e: Exception) {
                Log.e(
                    this_marker,
                    "error protected Bitmap doInBackground in private class DownloadImageTask"
                )
            }
            return mIcon11
        } //protected Bitmap doInBackground(String... urls)

        override fun onPostExecute(result: Bitmap?) {
            bmImage?.setImageBitmap(result)
        } //protected void onPostExecute(Bitmap result)
    } //private class DownloadImageTask extends AsyncTask<String, Void, Bitmap>

}