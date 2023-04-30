package com.my_style.mystyle.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.my_style.mystyle.R
import com.my_style.mystyle.databinding.FragmentSearchBinding
import com.my_style.mystyle.viewModel.MusicViewModel

class SearchFragment : Fragment() {
    private var _binding : FragmentSearchBinding? =null
    private val  binding get() = _binding!!
    private lateinit var musicViewModel: MusicViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(layoutInflater,container,false)
        musicViewModel = ViewModelProvider(requireActivity())[MusicViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}