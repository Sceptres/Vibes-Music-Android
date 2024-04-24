package com.aaa.vibesmusic.ui.fragment.playlists

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.playlist.Playlist
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs
import com.aaa.vibesmusic.database.util.DatabaseUtil
import com.aaa.vibesmusic.databinding.FragmentPlaylistsBinding
import com.aaa.vibesmusic.ui.adapters.PlaylistGridAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PlaylistsFragment : Fragment() {
    private lateinit var viewModel: PlaylistViewModel

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding: FragmentPlaylistsBinding
        get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater)

        val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(this.context)
        this.viewModel = PlaylistViewModelFactory.getFactory(this)
            .get(this.requireActivity().application, db)

        val playlistAdapter: PlaylistGridAdapter = PlaylistGridAdapter(this.requireContext(), ArrayList())
        this.binding.playlistGridView.adapter = playlistAdapter

        // Update UI with playlist data
        this.viewModel.playlistSongs.observe(viewLifecycleOwner) {
            playlistAdapter.data.clear()
            playlistAdapter.data.addAll(it)
            playlistAdapter.notifyDataSetChanged()
        }

        return binding.root
    }
}