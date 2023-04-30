package com.my_style.mystyle.view.activity

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.my_style.mystyle.R
import com.my_style.mystyle.click.MusicClick
import com.my_style.mystyle.databinding.ActivityMainBinding
import com.my_style.mystyle.models.MusicModel
import com.my_style.mystyle.utils.service.MyMusicService
import com.my_style.mystyle.view.fragment.MusicFragment
import com.my_style.mystyle.viewModel.MusicViewModel

class MainActivity : AppCompatActivity(), MusicClick {
    private lateinit var binding: ActivityMainBinding
    private lateinit var musicViewModel: MusicViewModel
    private var player: MyMusicService? = null
    var serviceBound = false
    private lateinit var mediaPlayer: ExoPlayer
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var backPress : OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindService()
        musicViewModel = ViewModelProvider(this)[MusicViewModel::class.java]
        setFragment(MusicFragment())
        search()
        backPress = object :OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                binding.searchEditText.clearFocus()
                if (binding.searchEditText.text.toString().isEmpty()){
                    finish()
                }else{
                    binding.searchEditText.text = null
                    musicViewModel.setSearchQuearyText("")
                }

            }

        }
        onBackPressedDispatcher.addCallback(this,backPress)

    }

    private fun setFragment(fragment: Fragment) {
        val fragmentTransient = supportFragmentManager.beginTransaction()
        fragmentTransient.replace(R.id.frameHolder, fragment)
        fragmentTransient.commit()
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MyMusicService.MyMusicBinder
            player = binder.getService()
            mediaPlayer = binder.getService().mediaPlayer
            serviceBound = true
            if (mediaPlayer.isPlaying) {
                binding.btnPlay.setImageResource(R.drawable.ic_pause)
            }
            playMethode()
            Toast.makeText(this@MainActivity, "Service Bound", Toast.LENGTH_SHORT).show()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBound = false
        }
    }

    private fun bindService() {
        if (!serviceBound) {
            val playerIntent = Intent(this, MyMusicService::class.java)
            startService(playerIntent)
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState.putBoolean("ServiceState", serviceBound)
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        serviceBound = savedInstanceState!!.getBoolean("ServiceState")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (serviceBound) {
            unbindService(serviceConnection)
            player?.stopSelf()
        }
        backPress.remove()
    }

    private fun playMethode() {
        mediaPlayer.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                binding.txtTitle.text = mediaItem?.mediaMetadata?.title
                updatePosition()
                setMethode()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if (playbackState == ExoPlayer.STATE_READY) {
                    binding.txtTitle.text = mediaPlayer.currentMediaItem?.mediaMetadata?.title
                    binding.seekBar.max = mediaPlayer.duration.toInt()
                    binding.seekBar.progress = mediaPlayer.currentPosition.toInt()
                    binding.txtTimer.text = formatDuration(mediaPlayer.duration)
                    updatePosition()
                    setMethode()
                }
            }
        })
    }

    private fun updatePosition() {
        val updateUI = Runnable {
            if (mediaPlayer.isPlaying) {
                binding.seekBar.progress = mediaPlayer.currentPosition.toInt()
                binding.txtRtimer.text = formatDuration(mediaPlayer.currentPosition)
            }
        }
        handler.post(object : Runnable {
            override fun run() {
                updateUI.run()
                handler.postDelayed(this, 1000)
            }
        })
    }

    private fun setMethode() {
        binding.imgPoster.setAnimation("music_disk.json")
        binding.imgPoster.resumeAnimation()
        binding.btnPlay.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                binding.btnPlay.setImageResource(R.drawable.ic_play)
                binding.imgPoster.pauseAnimation()

            } else {
                mediaPlayer.play()
                binding.btnPlay.setImageResource(R.drawable.ic_pause)
                binding.imgPoster.resumeAnimation()
            }
        }
        binding.btnBack.setOnClickListener {
            if (mediaPlayer.hasPreviousMediaItem()) mediaPlayer.seekToPrevious()
        }
        binding.btnNext.setOnClickListener {
            if (mediaPlayer.hasNextMediaItem()) mediaPlayer.seekToNext()
        }
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var userTouch = false
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                userTouch = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                userTouch = true
            }
        })
    }

    fun formatDuration(duration: Long): String {
        val hours = duration / (1000 * 60 * 60)
        val minutes = duration % (1000 * 60 * 60) / (1000 * 60)
        val seconds = duration % (1000 * 60) / 1000
        return if (hours == 0L) String.format("%02d:%02d", minutes, seconds)
        else String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun setMediaItem(playList: List<MusicModel>): List<MediaItem> {
        val mediaItem: MutableList<MediaItem> = mutableListOf()
        for (item in playList) {
            val media = MediaItem.Builder().apply {
                setUri(item.data)
                setMediaMetadata(getMataData(item))
            }.build()
            mediaItem.add(media)

        }
        return mediaItem
    }

    private fun getMataData(item: MusicModel): MediaMetadata {
        return MediaMetadata.Builder().apply {
            setTitle(item.title)
            setSubtitle(item.artist)
            setArtworkUri(item.img.toUri())
        }.build()
    }

    override fun onMusicClick(position: Int, list: List<MusicModel>) {
        startService(Intent(this, MyMusicService::class.java))
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.setMediaItems(setMediaItem(list), position, 0)
        } else {
            mediaPlayer.pause()
            mediaPlayer.seekTo(position, 0)
        }
        mediaPlayer.prepare()
        mediaPlayer.play()
        binding.apply {
            btnPlay.setImageResource(R.drawable.ic_pause)
            txtTitle.setSingleLine()
            txtTitle.ellipsize = TextUtils.TruncateAt.MARQUEE
            txtTitle.marqueeRepeatLimit = 5
            txtTitle.isSelected = true
            binding.imgPoster.playAnimation()
        }

    }

    private fun search(){
        binding.btnSearch.setOnClickListener {
            hideKeyboard()
            binding.searchEditText.clearFocus()
          val seatchText = binding.searchEditText.text
            musicViewModel.setSearchQuearyText(seatchText.toString())
        }
        binding.searchEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.searchEditText.text.toString()
                musicViewModel.setSearchQuearyText(query)
                hideKeyboard()
                binding.searchEditText.clearFocus()
                true
            } else {
                false
            }

        }
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                musicViewModel.setSearchQuearyText(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

    private fun Activity.hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}