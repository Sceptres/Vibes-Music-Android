package com.aaa.vibesmusic.ui.fragment.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.databinding.FragmentSongLibraryBinding
import com.aaa.vibesmusic.ui.adapters.SongsArrayAdapter

class SongLibraryFragment : Fragment() {
    private lateinit var viewModel: SongLibraryViewModel

    private var _binding: FragmentSongLibraryBinding? = null
    private val binding: FragmentSongLibraryBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSongLibraryBinding.inflate(inflater)

        val songsAdapter: SongsArrayAdapter = SongsArrayAdapter(requireContext(), ArrayList())
        binding.songsListView.adapter = songsAdapter

        val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(context)
        this.viewModel = SongLibraryViewModelFactory.getFactory(this)
            .get(requireActivity().application, db)

        // Add songs to the UI
        this.viewModel.songs.observe(viewLifecycleOwner) {
            songsAdapter.data.clear()
            songsAdapter.data.addAll(it)
            songsAdapter.notifyDataSetChanged()
        }

        return binding.root
    }
}