package com.jjpeso.oidar

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.station_list_row.view.*

data class Station(val title: String, val url: String, val location: String)

class StationsListAdapter(var items : ArrayList<Station>, val context: Context) : RecyclerView.Adapter<StationsListAdapter.ViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.station_list_row, parent, false))
    }

    override fun onBindViewHolder(holder: StationsListAdapter.ViewHolder, position: Int) {
        holder.stationTitle.text = items.get(position).title;
        holder.stationLocation.text = items.get(position).location;

        holder.stationLayout.setOnClickListener {
            Intent().also { intent ->
                intent.action = ACTION_PLAY
                intent.putExtra("url", items.get(position).url)
                intent.putExtra("title", items.get(position).title)
                intent.putExtra("location", items.get(position).location)
                context.sendBroadcast(intent)
            }
        }
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val stationTitle = view.station_title!!
        val stationLocation = view.station_location!!
        var stationLayout = view.station_layout!!
    }
}


