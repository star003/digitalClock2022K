package com.example.digitalclock2022k

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.SharedPreferences
import android.view.WindowManager
import android.util.Log
import java.util.*
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import java.lang.String
import android.view.Window
import android.view.View
import android.os.AsyncTask
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import java.lang.NumberFormatException


class MainActivity : AppCompatActivity() {

    val this_marker = "DashBoard" //** зададим имя маркера для логов
    val dyn_brent_usd = "" //**переключатель показа динамики нефть/доллар
    val this_small = true
    val APP_PREFERENCES = "digitalClock"
    lateinit var mSettings: SharedPreferences

    var sc:Int = 0

    var _fields = ArrayList<TextView>()
    var _color: List<Int> = Arrays.asList(
        R.color.gm1, R.color.gm2, R.color.gm3, R.color.gm4,
        R.color.gm5, R.color.gm6, R.color.gm7, R.color.gm8
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mSettings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        val face =  ResourcesCompat.getFont(this, R.font.electron)

        Log.i(this_marker, "----welcome dashboard----")
        Log.i(this_marker, "this small $this_small")

        val fldId: List<Int> = Arrays.asList(
            R.id.curT, R.id.minT, R.id.trend, R.id.maxT,
            R.id.brent, R.id.usd, R.id.bch1, R.id.brr1,
            R.id.m1, R.id.weekDay, R.id.day, R.id.mont,
            R.id.prg, R.id.astr,
            R.id.curT1, R.id.minT1, R.id.trend1, R.id.maxT1,
            R.id.curT3, R.id.minT3, R.id.trend3, R.id.maxT3,
            R.id.curT2, R.id.minT2, R.id.trend2, R.id.maxT2
        )
        /* 0	1	2	3
		 * 4	5	6	7
		 * 8	9	10	11
		 * 12	13
		 * 14	15	16	17
		 * 18	19	20	21
		 * 22	23	24	25
		*/

        Log.i(this_marker, "init elements")

        Log.i(this_marker, "fldId.size() " + fldId.size)

        for (i in fldId.indices) {
            val fff = findViewById(fldId[i]) as TextView
            //Log.i(this_marker, "step 1 $i")
            fff.typeface = face
            _fields.add(fff)
        }

        Log.i(this_marker, "set name elements")
        Log.i(this_marker, "go thread")

        var mt = goInd()
        mt.execute()

        val t: Thread = object : Thread() {
            override fun run() {
                try {
                    //**при первом пуске , заполним поля дата - время
                    refreshTime()
                    while (!isInterrupted) {
                        sleep(60000) //**обновимся раз в 60 сек
                        runOnUiThread {
                            sc++
                            Log.i(this_marker, "Refresh sleep 60s... $sc")
                            refreshTime()
                            if (sc > 2) {
                                //Log.i(this_marker, "ENTER Refresh tablo (60s)")
                                //**раз в 5 минут обновим показания
                                sc = 0
                                mt = goInd()
                                mt.execute()

                            }
                        } //public void run()
                        //runOnUiThread(new Runnable()
                    }
                } catch (e: InterruptedException) {
                    Log.e(this_marker, "error in t = new Thread()- public void run()")
                }
            } //public void run()
        } //Thread t = new Thread()
        t.start()
    } //onCreate

    fun onClick(v: View) {
        if (v.getId() === R.id.usd) {
            Log.i(this_marker, "take USD")
            val intent = Intent(this, FromGis::class.java)
            startActivity(intent)
            /*
			dyn_brent_usd = "usd";
			Intent intent = new Intent(this, DynUsd.class);
		    startActivity(intent);
		    */
        }
        if (v.getId() === R.id.brent) {
            Log.i(this_marker, "take R.id.brent")
            /*
			dyn_brent_usd = "brent";
			Intent intent = new Intent(this, DynUsd.class);
		    startActivity(intent);
		    */
        }
        if (v.getId() === R.id.curT) {
            Log.i(this_marker, "R.id.curT")
            var mt2 = goCurrT()
            mt2.execute()
        }
        if (v.getId() === R.id.prg) {
            Log.i(this_marker, "R.id.prg")
            //val intent = Intent(this, FromGis::class.java)
            //startActivity(intent)
        }
        if ((v.getId() === R.id.bch1) or (v.getId() === R.id.m1) or (v.getId() === R.id.brr1)) {
            Log.i(this_marker, "take R.id.bch1 | v.getId()==R.id.m1 | v.getId()==R.id.brr1")
            //val intent = Intent(this, ClockForHome::class.java)
            //startActivity(intent)
        }
        if (v.getId() === R.id.mont) {
            Log.i(this_marker, "take R.id.mont")
            //val intent = Intent(this, MainActivity::class.java)
            //startActivity(intent)
        }
    } //public void onClick(View v)

    fun refreshTime() {
        val currentTime = Calendar.getInstance()
        _fields[6].text =
            if (currentTime[11] >= 10) String.valueOf(currentTime[11]) else "0" + String.valueOf(
                currentTime[11]
            )
        _fields[8].text =
            if (currentTime[12] >= 10) String.valueOf(currentTime[12]) else "0" + String.valueOf(
                currentTime[12]
            )
        val xx: Array<out kotlin.String>? = tst.getCurrData()
        _fields[9].text = xx?.get(8)
        _fields[10].text = xx?.get(2)
        _fields[11].text = xx?.get(6)

    } //void refreshTime()

    inner class goInd() : AsyncTask<Void, Void, String>() {


        var _stringData = Arrays.asList("", "", "", "", "", "", "", "", "", "", "", "", "", "")
        var _stringData1 = Arrays.asList("", "", "", "", "", "", "", "", "", "", "", "", "", "")
        var _stringData2 = Arrays.asList("", "", "", "", "", "", "", "", "", "", "", "", "", "")
        var _stringData3 = Arrays.asList("", "", "", "", "", "", "", "", "", "", "", "", "", "")
        var _stringDataMG = Arrays.asList(
            "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", ""
        )

        //val this_marker = this_marker

        /////////////////////////////////////////////////////////////////////////////////////

        /////////////////////////////////////////////////////////////////////////////////////
        override fun onPreExecute() {
            Log.i(this_marker, "Starting goInd")
            super.onPreExecute()
        } //protected void onPreExecute()

        override fun doInBackground(vararg params: Void?): String? {

            //*** нужно предусмотреть сохранение прошлых показаний на случай ,
            //*** если не прочитает новые
            Log.i(this_marker, "Set data in tablo")
            //*** Улица
            addInd(tst.readThingSpeak("180657", "1", "field1", "96", 5), _stringData)
            //*** дом
            addInd(tst.readThingSpeak("180093", "3", "field3", "96", 10), _stringData1)
            //*** отопл
            addInd(tst.readThingSpeak("180093", "1", "field1", "96", 10), _stringData3)
            //*** батарея 56
            addInd(tst.readThingSpeak("180657", "2", "field2", "96", 10), _stringData2)
            //addInd(tst.readThingSpeakTest() , _stringData2);

            _stringData.set(4,"Brent: "+tst.investing());
            _stringData.set(5, "USD: "+tst.usd());

            //_stringData[4] = "Brent: " + "-"
            //_stringData[5] = "USD: " + "-"
            return null
        } //protected Void doInBackground(Void... params)

        //@SuppressLint("ResourceAsColor")
        override protected fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            Log.i(this_marker, "goInd -> onPostExecute")
            val ind = Arrays.asList(0, 1, 2, 3, 4, 5, 12, 13)
            try {
                for (i in ind.indices) {
                    _fields.get(ind[i]).setText(_stringData[ind[i]])
                }
                for (i in 0..3) {
                    _fields.get(ind[i] + 14).setText(_stringData1[ind[i]])
                    _fields.get(ind[i] + 18).setText(_stringData2[ind[i]])
                    _fields.get(ind[i] + 22).setText(_stringData3[ind[i]])
                }
                if (this_small != true) {
                    for (i in _stringDataMG.indices) {

                        //_fieldsMg.get(i).setText(_stringDataMG.get(i));
                        //_fieldsMg.get(i).setBackgroundResource(_color.get(Integer.valueOf(_stringDataMG.get(i))-1));
                    }
                }
            } catch (e3: NumberFormatException) {
                println("$this_marker ошибка вывода в форму, представление строка - число (class goInd) ")
            }

        } //protected void onPostExecute(Void result)

    } //goInd

    /*
     * x - данные показателя
     * _stringData -что меняем.
     */
    fun addInd(x: List<kotlin.String>, _stringData: MutableList<kotlin.String?>) {
        val ind = Arrays.asList(0, 2, 6, 4)
        if (x.size > 6) {
            if (x[6].contains("u") or x[6].contains("d") or x[6].contains("n")) {
                //**все ок , заполним графы
                Log.i(this_marker, "data in my weather...ок " + x[6])
                for (i in ind.indices) {
                    _stringData[i] = if (x[ind[i]].indexOf(" ") > 0) x[ind[i]] else "" + x[ind[i]]
                    Log.i(
                        this_marker,
                        if (x[ind[i]].indexOf(" ") > 0) x[ind[i]] else "" + x[ind[i]]
                    )
                }
            } else {
                //**вернет старые данные
                Log.e(this_marker, "data in my weather...fail " + x[6])
                for (i in 0..3) {
                    _stringData[i] = _fields[i].text.toString()
                }
            }
            /*test*/
            _stringData.forEach {
                Log.i(this_marker, "test _stringData: " + it)
            }
        }
    } //addInd

    inner class goCurrT : AsyncTask<Void, Void, String>() {

        var _stringData = Arrays.asList("", "", "", "", "", "", "", "", "", "", "", "", "", "")
        var _stringData1 = Arrays.asList("", "", "", "", "", "", "", "", "", "", "", "", "", "")
        var _stringData2 = Arrays.asList("", "", "", "", "", "", "", "", "", "", "", "", "", "")
        var _stringData3 = Arrays.asList("", "", "", "", "", "", "", "", "", "", "", "", "", "")

        /////////////////////////////////////////////////////////////////////////////////////
        override fun onPreExecute() {
            super.onPreExecute()
        } //protected void onPreExecute()

        /////////////////////////////////////////////////////////////////////////////////////
        override fun doInBackground(vararg params: Void?): String? {
            addInd(tst.readThingSpeak("180657", "1", "field1", "96", 5), _stringData)
            addInd(tst.readThingSpeak("180093", "1", "field1", "1440", 10), _stringData1)
            addInd(tst.readThingSpeak("180657", "2", "field2", "96", 5), _stringData2)
            addInd(tst.readThingSpeak("200376", "2", "field2", "96", 5), _stringData3)
            //addInd(gisFromSite.readThingSpeakTest() , _stringData3);
            return null
        } //protected Void doInBackground(Void... params)

        /////////////////////////////////////////////////////////////////////////////////////
        override protected fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                val ind = Arrays.asList(0, 1, 2, 3)
                for (i in ind.indices) {
                    //!!
                    _fields.get(ind[i]).setText(_stringData[ind[i]])
                    _fields.get(ind[i] + 14).setText(_stringData1[ind[i]])
                    _fields.get(ind[i] + 18).setText(_stringData2[ind[i]])
                    _fields.get(ind[i] + 22).setText(_stringData3[ind[i]])
                    Log.i(
                        this_marker,
                        "_fields.get(ind.get(i+14)).setText(_stringData.get(ind.get(i)))" + _stringData[ind[i]]
                    )
                }
            } catch (e3: NumberFormatException) {
                Log.e(
                    this_marker,
                    "ошибка вывода в форму, представление строка - число (class goCurrT) "
                )
            }
        } //protected void onPostExecute(Void result)
    } //class goCurrT extends AsyncTask<Void, Void, Void>

    inner class goUsd :AsyncTask<Void, Void, String>()  {
        var _stringData = Arrays.asList("", "", "", "", "", "", "", "", "", "", "", "", "", "")
        override fun onPreExecute() {
            super.onPreExecute()
        } //protected void onPreExecute()

        /////////////////////////////////////////////////////////////////////////////////////
        override fun doInBackground(vararg params: Void?): String? {

			_stringData.set(4, tst.investing());
	    	_stringData.set(5, tst.usd());

            //_stringData[4] = "-"
            //_stringData[5] = "-"
            return null
        } //protected Void doInBackground(Void... params)

        /////////////////////////////////////////////////////////////////////////////////////
        override protected fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {

	    		_stringData.set(5,"USD: "	+_stringData.get(5));
	    		_stringData.set(4,"brent: "	+_stringData.get(4));

                //_stringData[5] = "USD: "
                //_stringData[4] = "brent: "
            } catch (e3: NumberFormatException) {
                Log.e(
                    this_marker,
                    "ошибка вывода в форму, представление строка - число (class goUsd) "
                )
            }
        } //protected void onPostExecute(Void result)
    } //class goUsd extends AsyncTask<Void, Void, Void>

} //MainActivity