package com.my_style.mystyle.utils.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer

class MyMusicService : Service() {
    private val iBinder: Binder = MyMusicBinder()
    lateinit var mediaPlayer: ExoPlayer

    override fun onBind(intent: Intent): IBinder = iBinder

    inner class MyMusicBinder : Binder() {
        fun getService(): MyMusicService {
            return this@MyMusicService
        }
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = ExoPlayer.Builder(applicationContext).build()
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()
        mediaPlayer.setAudioAttributes(audioAttributes, true)

    }




}