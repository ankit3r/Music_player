package com.my_style.mystyle.models

import android.net.Uri

data class MusicModel(
    val id: Long,
    val title: String,
    val albumId: Long,
    val artist: String,
    val duration: Long,
    val size: Long,
    val data: String,
    val img : String
    )