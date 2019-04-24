package com.jjpeso.oidar

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserServiceCompat

const val UPDATE_BUFFER: String = "com.jjpeso.oidar.update.BUFFER"
const val ACTION_PLAY: String = "com.jjpeso.oidar.action.PLAY"
const val ACTION_STOP: String = "com.jjpeso.oidar.action.STOP"
const val ACTION_PLAY_PAUSE: String = "com.jjpeso.oidar.action.PLAY_PAUSE"

class AudioPlayer: MediaBrowserServiceCompat(), MediaPlayer.OnPreparedListener
{
    private var mMediaPlayer: MediaPlayer? = null

    override fun onLoadChildren(p0: String, p1: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onGetRoot(p0: String, p1: Int, p2: Bundle?): BrowserRoot? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action: String? = intent!!.action
        when(action) {
            ACTION_PLAY -> {
                if (mMediaPlayer == null) {
                    mMediaPlayer = MediaPlayer()
                }

                if (mMediaPlayer!!.isPlaying) {
                    mMediaPlayer!!.stop()
                    mMediaPlayer!!.reset()
                    playUrl(intent.getStringExtra("url"))

                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(11, intent.extras.get("notification") as Notification)
                }
                else {
                    mMediaPlayer!!.stop()
                    playUrl(intent.getStringExtra("url"))
                    startForeground(11, intent.extras.get("notification") as Notification)
                }
            }
            ACTION_PLAY_PAUSE -> {
                if (mMediaPlayer!!.isPlaying) {
                    mMediaPlayer!!.pause()
                }
                else
                    mMediaPlayer!!.start()
            }
            ACTION_STOP -> {
                if (mMediaPlayer!!.isPlaying) {
                    mMediaPlayer!!.stop()
                }
                mMediaPlayer!!.reset()
                mMediaPlayer!!.release()
                mMediaPlayer = null

                stopForeground(true)
            }
        }

        return START_NOT_STICKY
    }

    private fun playUrl(url: String)
    {
        mMediaPlayer?.apply {
            setOnPreparedListener(this@AudioPlayer)
            setDataSource(url)
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build())
            prepareAsync()
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mMediaPlayer!!.start()

        Intent().also { intent ->
            intent.action = UPDATE_BUFFER
            sendBroadcast(intent)
        }
    }
}