package com.my_style.mystyle.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.my_style.mystyle.models.MusicModel

class MusicRepository(private val context: Context) {
    fun getAllMusic(): List<MusicModel> {
        val musicList = mutableListOf<MusicModel>()
        val mediaStoreUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        else MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
        )
        val sortOrder = MediaStore.Audio.Media.DATE_ADDED + " DESC"
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        try {
            val cursor = context.contentResolver.query(mediaStoreUri, projection, selection, null, sortOrder)
            if (cursor != null) {
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    var displayName = cursor.getString(titleColumn)
                    val albumId = cursor.getLong(albumIdColumn)
                    val artist = cursor.getString(artistColumn)
                    val duration = cursor.getLong(durationColumn)
                    val size = cursor.getLong(sizeColumn)
                    val data = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                    val img = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId)
                    displayName = displayName.substring(0,displayName.lastIndexOf("."))
                    if (size != 0L && duration != 0L){
                        val music = MusicModel(id, displayName, albumId, artist, duration, size, data.toString(), img.toString())
                        musicList.add(music)
                    }
                }
                cursor.close()
            }
        } catch (e: Exception) {
            Log.e("ANKIT", "error $e")
        }
        return musicList
    }
}