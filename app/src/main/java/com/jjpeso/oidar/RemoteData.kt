package com.jjpeso.oidar

import android.os.AsyncTask
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import org.json.JSONException
import org.json.JSONObject
import java.io.InputStreamReader
import java.net.URL

class RemoteData(private val remoteUrl: String, val callback: (result: JSONObject) -> Unit) : AsyncTask<Void, Void, String>()
{
    override fun doInBackground(vararg params: Void?): String? {
        var connection: HttpURLConnection? = null
        var reader: BufferedReader? = null

        try {
            val url = URL(remoteUrl)
            connection = url.openConnection() as HttpURLConnection
            connection.connect()

            val stream = connection.getInputStream()

            reader = BufferedReader(InputStreamReader(stream))

            val buffer = StringBuffer()
            var line = reader.readLine()

            while (line != null) {
                buffer.append(line + "\n")
                Log.d("Response: ", "> $line")
                line = reader!!.readLine()
            }

            return buffer.toString()

        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (connection != null) {
                connection!!.disconnect()
            }
            try {
                if (reader != null) {
                    reader!!.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    override fun onPostExecute(result: String?) {
        try {
            val mainObject = JSONObject(result)
            callback(mainObject)
        } catch (e: JSONException) {
            //
        }

    }
}
