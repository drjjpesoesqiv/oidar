package com.jjpeso.oidar

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import android.view.View
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var stateBuilder: PlaybackStateCompat.Builder
    private lateinit var metaBuilder: MediaMetadataCompat.Builder

    private val audioPlayerManager = AudioPlayerManager()

    private val stations = ArrayList<Station>()

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                stations_view.visibility = View.VISIBLE
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                stations_view.visibility = View.INVISIBLE
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                stations_view.visibility = View.INVISIBLE
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mediaSession = MediaSessionCompat(this, "oidar")
        stateBuilder = PlaybackStateCompat.Builder()
            .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE)
            .setState(PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1f)
        metaBuilder = MediaMetadataCompat.Builder()
        mediaSession.setPlaybackState(stateBuilder.build())
        mediaSession.setMetadata(metaBuilder.build())
        mediaSession.setCallback(object: MediaSessionCompat.Callback() {
            override fun onPlay() {
            }

            override fun onPause() {
            }
        })

        audioPlayerManager.start(this, mediaSession.sessionToken)

        //

        val stationsView = findViewById<RecyclerView>(R.id.stations_list)

        val manager = LinearLayoutManager(this)
        stationsView.layoutManager = manager

        val stationsAdapter = StationsListAdapter(stations, this);
        stationsView.adapter = stationsAdapter

        RemoteData("remote json file", ::populateList).execute()

        //

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onDestroy() {
        Log.d("onDestroy: ", "destroying")
        audioPlayerManager.stop(this)
        unregisterReceiver(audioPlayerManager)
        super.onDestroy()
    }

    private fun populateList(result: JSONObject)
    {
        Log.d("Callback: ", "calling populateList")

        val stationsObject = result.getJSONArray("stations")

        Log.d("populateList", "Num station: " + stationsObject.length().toString())

        for (i in 0 until (stationsObject.length())) {
            val row = stationsObject.getJSONObject(i)
            stations.add(Station(
                row.getString("t"),
                row.getString("u"),
                row.getString("l")))
        }

        Log.d("populateList: ", stations.size.toString())

        val stationsView = findViewById<RecyclerView>(R.id.stations_list)
        stationsView.setHasFixedSize(true)

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        stationsView.setLayoutManager(linearLayoutManager)

        var stationsAdapter = (stationsView.adapter as StationsListAdapter)
        stationsAdapter.items = stations
        stationsAdapter.notifyDataSetChanged()
    }
}
