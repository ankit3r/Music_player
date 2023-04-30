package com.my_style.mystyle.click

import com.my_style.mystyle.models.MusicModel

interface MusicClick {
    fun onMusicClick(position: Int, list: List<MusicModel>)
}