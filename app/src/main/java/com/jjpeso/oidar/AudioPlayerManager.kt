package com.jjpeso.oidar

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.media.session.MediaSessionCompat
import android.content.IntentFilter

class AudioPlayerManager: BroadcastReceiver()
{
    private var notificationBuilder: NotificationCompat.Builder? = null
    fun start(context: Context, sessionToken: MediaSessionCompat.Token)
    {
        val intentFilter = IntentFilter(ACTION_PLAY_PAUSE)
        intentFilter.addAction(ACTION_PLAY)
        intentFilter.addAction(ACTION_STOP)
        intentFilter.addAction(UPDATE_BUFFER)
        context.registerReceiver(this, intentFilter)

        val playPauseIntent = Intent(ACTION_PLAY_PAUSE)
        val playPausePendingIntent = PendingIntent.getBroadcast(context, 11, playPauseIntent, 0)

        val stopIntent = Intent(ACTION_STOP)
        val stopPendingIntent = PendingIntent.getBroadcast(context, 11, stopIntent, 0)

        notificationBuilder = NotificationCompat.Builder(context, "oidar_11")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle("Station")
            .setContentText("Location")
            .setStyle(android.support.v4.media.app.NotificationCompat.MediaStyle().setMediaSession(sessionToken))
            .addAction(android.R.drawable.ic_media_pause, "PLAY/PAUSE", playPausePendingIntent)
            .addAction(android.R.drawable.ic_delete, "STOP", stopPendingIntent)
            .setAutoCancel(false)
            .setOngoing(true)
    }

    fun next(context: Context, url: String) {
        val svc = Intent(ACTION_PLAY, null, context, AudioPlayer::class.java)
        svc.putExtra("url", url)
        context.startService(svc)
    }

    fun stop(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(11)

        val svc = Intent(ACTION_STOP, null, context, AudioPlayer::class.java)
        context.startService(svc)
    }

    private var cachedLocation: String = "";

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent!!.action
        when (action) {
            ACTION_PLAY_PAUSE -> {
                val svc = Intent(ACTION_PLAY_PAUSE, null, context, AudioPlayer::class.java)
                context!!.startService(svc)
            }
            ACTION_STOP -> {
                stop(context as Context)
            }
            ACTION_PLAY -> {
                notificationBuilder?.setContentTitle(intent.getStringExtra("title"))
                notificationBuilder?.setContentText("Connecting...") // ("location"))
                val svc = Intent(ACTION_PLAY, null, context, AudioPlayer::class.java)
                svc.putExtra("url", intent.getStringExtra("url"))
                svc.putExtra("notification", notificationBuilder?.build())
                context!!.startService(svc)

                cachedLocation = intent.getStringExtra("location")
            }
            UPDATE_BUFFER -> {
                notificationBuilder?.setContentText(cachedLocation)
                val notificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(11, notificationBuilder?.build())
            }
        }

    }
}