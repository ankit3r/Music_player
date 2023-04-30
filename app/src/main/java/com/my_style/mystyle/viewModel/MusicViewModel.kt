package com.my_style.mystyle.viewModel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.my_style.mystyle.models.MusicModel
import com.my_style.mystyle.repository.MusicRepository

class MusicViewModel(application: Application) : AndroidViewModel(application){
    private val musicRepository = MusicRepository(application)

    private val _listOfMusic = MutableLiveData<List<MusicModel>>()
    val listOfMusic: LiveData<List<MusicModel>> = _listOfMusic

    private fun getMusic(){
       _listOfMusic.value = musicRepository.getAllMusic()
    }

    private val searchQueary = MutableLiveData<String>()
    val searchText: LiveData<String> = searchQueary

    fun setSearchQuearyText(s:String){
        searchQueary.value = s
    }

    init {
        getMusic()
    }
}