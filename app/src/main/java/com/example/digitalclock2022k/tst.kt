package com.example.digitalclock2022k

import android.util.Log
import com.example.digitalclock2022k.gisFromSite.this_marker
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.IOException
import java.lang.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class tst {
    companion object {

        fun getCurrData(): Array<String>? {
            val currentTime = Calendar.getInstance()
            return arrayOf(
                currentTime[1].toString(),
                if (currentTime[2] + 1 >= 10) (currentTime[2] + 1).toString() else "0" + (currentTime[2] + 1).toString(),
                if (currentTime[5] >= 10) currentTime[5]
                    .toString() else "0" + currentTime[5].toString(),
                if (currentTime[11] >= 10) currentTime[11]
                    .toString() else "0" + currentTime[11].toString(),
                if (currentTime[12] >= 10) currentTime[12]
                    .toString() else "0" + currentTime[12].toString(),
                if (currentTime[13] >= 10) currentTime[13]
                    .toString() else "0" + currentTime[13].toString(),
                gisFromSite.mont[currentTime[2]],
                gisFromSite.weekdaySh[currentTime[Calendar.DAY_OF_WEEK]].toString(),
                gisFromSite.weekday[currentTime[Calendar.DAY_OF_WEEK]].toString()
            )
        } //public String[] getCurrData()

        /*
            функция - заглушка : верент дату надень раньше текущей
         */
        fun dateMinusDay(): Date {
            val date = Date()
            val c = Calendar.getInstance()
            c.setTime(date);
            c.add(Calendar.DATE, -1);
            return c.getTime()
        }//dateMinusDay

        /*
        вернет данные для виджета
         */
        fun myData(): MutableList<String>{
            val url ="http://93.181.225.204:1880/my";
            var a : MutableList<String> = ArrayList()

            try{

                var doc = Jsoup.connect(url).get()
                var created: Elements = doc.select("позиция")
                var date = ""
                created.forEach {
                    //val children: Elements = it.children()
                    date = it.attr("дата")
                    print(it.attr("показатель"))
                    print(":")
                    println(it.attr("значение"))
                    a.add(it.attr("показатель")+":"
                        + it.attr("значение"))
                }
                println("date = "+date.toString())
                a.add(""+date)
                println(a.size)

            }
            catch (e: IOException) {
                println("myData произошда хрень")
            }
            return a
        }//myData

        /*
        idChanell: String   ID канала thingspeak, например:"180657"
        numCh: String   номер поля в канале например: "1"
        fieldName:String    имя поля в канале, например: "field1"
        numPok:String : количество анализируемых показаний: 90 , это 90 показаний
        numForTrend:Int: количество показаний для вычислений тренда: 5, чем более статичный показатель , тем это число
                            должно быть больше
         */
        fun readThingSpeak(idChanell: String, numCh: String, fieldName:String, numPok:String, numForTrend:Int): List<String> {
            var tek: String
            var mn: String
            var mx: String
            println("$this_marker readMy()")
            var url = "https://api.thingspeak.com/channels/" + idChanell.toString() + "/fields/" + numCh.toString() + ".xml?results=" + numPok
            println("$this_marker URL $url")
            val testList: MutableList<Double> = ArrayList()
            try {
                var doc = Jsoup.connect(url).get()
                //***проверим последнюю дату показания с канала
                //***если показаний нет более часа то отбой!
                val created = doc.select("created-at")
                val format: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
                val curDate = Date()
                val date:Date = try {
                     format.parse(created[1].text())
                } catch (e: ParseException) {
                    dateMinusDay() //***если непонятки то примем дату как день назад и отбой
                }
                //val rr: Long = (curDate.getTime() - (date.getTime()+10800000))/1000
                val rr: Long = (curDate.getTime() - (date.getTime()+10800000))/1000
                //println(rr)
                if (rr>3600*3) { //**показаний нет больше часа. Вернем ошибку

                    Log.i(this_marker,"cur date "+curDate);
                    Log.i(this_marker,"updated-at "+date);
                    Log.i(this_marker,"NO DATA, OVER 1 HOUR "+curDate);
                    Log.i(this_marker,"rr "+rr);

                    return "-;-;-;-;-;-;u".split(";");
                }
                val metaElements = doc.select(fieldName)
                metaElements.forEach{

                    var dbl:Double = try {
                        java.lang.Double.valueOf(it.text());
                    } catch (e: NumberFormatException) {
                        java.lang.Double.valueOf("-99")
                    }
                    if (!it.text().equals("")
                        and (it.text() != null)
                        and (it.text().length < 15) and (dbl>-99)) {
                            testList.add(dbl)
                    }
                }
                tek = testList.get(testList.size-1).toString();
                println("$this_marker tek "+tek);
                val trendInterval = numForTrend

                val tr: List<Double> = testList.subList(
                    testList.size - if (testList.size < trendInterval) testList.size else trendInterval,
                    testList.size - 1
                ) //**список последних N показаний

                var av = 0.0 //**средняя температура по N показаниям
                for (i in 0 until tr.size) {
                    av += tr[i] //**суммируем показания
                }
                av = av / tr.size //**вычислим среднюю температуру
                var trendK = 0

                for (i in 0 until tr.size) {
                    if (av - tr[i] < 0) { //**если меьше средней то на понижение
                        trendK--
                    } else {
                        trendK++ //**на повышение
                    }
                }
                Collections.sort(testList);
                mx = testList.get(testList.size-1).toString();
                mn = testList.get(0).toString();

                var itg:String = (tek+"; ;"+mn+"; ;"+mx) +
                        if (trendK > 0) {
                            "; ;u";
                        }
                        else if (trendK < 0) {
                            "; ;d";
                        }
                        else {
                            "; ;n";
                        }

                println("$this_marker mn "+mn);
                println("$this_marker mx "+mx);
                println("$this_marker itg.split "+itg);
                return itg.split(";");
            }
            catch (e: IOException) {
                println("$this_marker ошибка подключениея к api.thingspeak.com , возможно нет связи ? ");
                return "-;-;-;-;-;-;n".split(";");
            }

            return "-;-;-;-;-;-;n".split(";");

        } //readThingSpeak

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        fun readThingSpeakTest(): List<String> {
            //**тестовая процедура - заглушка
            return "0;1;2;3;4;5;u".split(";");
        } //readThingSpeakTest

        ///////////////////////////////////////////////////////////////////////////////////
        fun getPokaz(doc: Document, prm: String?): ArrayList<String> {

            val x = ArrayList<String>()
            val tm = doc.select(prm)
            //for (v in tm) {
            //    x.add(v.text())
            //}
            tm.forEach {
                x.add(it.text())
                println("it - text:"+it.text())
            }
            return x
        } //static ArrayList<String> getPokaz(Document doc,String prm)

        ///////////////////////////////////////////////////////////////////////////////////
        fun getImg(doc: Document, prm: String): ArrayList<String> {
            val x = ArrayList<String>()
            val cloudness = doc.select(prm)
            val tm = cloudness.select("img")
            for (v in tm) {
                x.add("http:" + v.attr("src"))
            }
            return x
        } //static ArrayList<String> getImg(Document doc,String prm)

        @Throws(IOException::class)
        fun grabGismeteo(): ArrayList<ArrayList<String>> {
            val x = ArrayList<ArrayList<String>>()
            val doc = Jsoup.connect("https://www.gismeteo.ru/city/legacy/4298/").get()

            x.add(getPokaz(doc, "th.df,th.current"))
            x.add(getImg(doc, "tr.cloudness"))
            x.add(getImg(doc, "tr.persp"))
            val m1 = java.util.ArrayList<String>()
            val m2 = java.util.ArrayList<String>()
            val m = getPokaz(doc, "span.value.m_temp.c")
            for (i in m.indices) {
                if (i < 13) {
                    m1.add(m[i])
                } else if ((i >= 13) and (i <= 25)) {
                    m2.add(m[i])
                }
            }
            x.add(m1)
            x.add(getPokaz(doc, "span.value.m_press.torr"))
            x.add(getPokaz(doc, "dt.wicon"))
            x.add(getPokaz(doc, "span.value.m_wind.ms"))
            x.add(m2)
            return x
        } //static ArrayList<ArrayList<String>> grabGismeteo() throws IOException

        fun investing(): String? {
            val doc: Document
            try {
                doc = Jsoup.connect("https://www.investing.com/commodities/brent-oil")
                    .userAgent("Mozilla")
                    .get()
                val metaElements: Elements = doc.select("span.text-2xl")
                for (x in metaElements) {
                    return x.text()
                }
            } catch (e: IOException) {
                return ""
            }
            return ""
        } //public static String investing () throws IOException

        fun usd(): String? {
            return try {
                val doc = Jsoup.connect("https://www.finanz.ru/valyuty/usd-rub")
                    .userAgent("Mozilla")
                    .get()
                val metaElements = doc.select("th")
                metaElements.forEach {
                    if (it.toString().contains("<span>RUB</span>")) {
                        return (it.text().substring(0, 5))
                    }
                }
                metaElements.first().text()
            } catch (e: IOException) {
                ""
            } catch (e1: NullPointerException) {
                ""
            }
        } //public static String usd() throws IOException

        @JvmStatic
        fun main(args: Array<String>) {
            /*
            var a=readThingSpeak("180657", "2", "field2", "10", 5);
            a.forEach {
                println(it)
            }
            */
            //println(usd())
            //println(dateMinusDay())
            myData()
        }//main
    }

}//tst