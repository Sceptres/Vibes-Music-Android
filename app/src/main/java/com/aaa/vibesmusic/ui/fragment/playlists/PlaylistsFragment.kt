package com.aaa.vibesmusic.ui.fragment.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.databinding.FragmentPlaylistsBinding
import com.aaa.vibesmusic.monetization.Ads
import com.aaa.vibesmusic.ui.adapters.PlaylistGridAdapter
import com.aaa.vibesmusic.ui.popup.CreatePlaylistPopup

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

        this.binding.playlistGridView.setOnItemClickListener { _, _, position, _ ->
            val bundle: Bundle = Bundle()

            this.requireActivity().findNavController(R.id.nav_host_fragment)
                .navigate(R.id.playlistFragmentToPlaylistSongsFragment, bundle)
        }

        this.binding.createPlaylistBtn.setOnClickListener {
            val createPlaylistPopup: CreatePlaylistPopup = CreatePlaylistPopup(this.requireContext(), db)
            createPlaylistPopup.show(this.requireActivity().supportFragmentManager, "Create Playlist Popup")
        }

        this.requireActivity().onBackPressedDispatcher.addCallback(this.viewLifecycleOwner, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().findNavController(R.id.nav_host_fragment)
                    .navigate(R.id.playlistFragmentToSongLibraryFragment)
            }
        })

        Ads.loadBanner(this.binding.playlistBanner, this.requireContext())

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        this._binding = null
    }
}