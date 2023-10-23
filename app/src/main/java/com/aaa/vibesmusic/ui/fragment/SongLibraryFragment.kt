package com.aaa.vibesmusic.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aaa.vibesmusic.databinding.FragmentSongLibraryBinding

class SongLibraryFragment : Fragment() {
    private var _binding: FragmentSongLibraryBinding? = null
    private val binding: FragmentSongLibraryBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSongLibraryBinding.inflate(inflater)

        return binding.root
    }
}