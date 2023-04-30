package com.my_style.mystyle.view.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.my_style.mystyle.adapters.MusicAdapter
import com.my_style.mystyle.click.MusicClick
import com.my_style.mystyle.databinding.FragmentMusicBinding
import com.my_style.mystyle.models.MusicModel
import com.my_style.mystyle.viewModel.MusicViewModel

class MusicFragment : Fragment(),MusicClick {
    private var _binding: FragmentMusicBinding? = null
    private val binding get() = _binding!!
    private lateinit var musicViewModel: MusicViewModel
    private lateinit var mListener: MusicClick

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = try {
            context as MusicClick
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement MyInterface")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicBinding.inflate(layoutInflater, container, false)
        musicViewModel = ViewModelProvider(requireActivity())[MusicViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = MusicAdapter(musicViewModel.listOfMusic,this,requireContext())
        binding.rcMusic.adapter = adapter
        musicViewModel.searchText.observe(viewLifecycleOwner){
           adapter.filter.filter(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onMusicClick(position: Int, list: List<MusicModel>) {
        mListener.onMusicClick(position,list)
    }
}
