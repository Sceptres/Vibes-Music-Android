package com.aaa.vibesmusic.ui.fragment.playlists.songs

import android.Manifest
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.findNavController
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs
import com.aaa.vibesmusic.databinding.FragmentPlaylistSongsBinding
import com.aaa.vibesmusic.monetization.Ads
import com.aaa.vibesmusic.perms.PermissionsUtil
import com.aaa.vibesmusic.player.MediaPlayerService
import com.aaa.vibesmusic.player.ServiceUtil
import com.aaa.vibesmusic.ui.UIUtil
import com.aaa.vibesmusic.ui.adapters.PlaylistSongsAdapter
import com.aaa.vibesmusic.ui.viewgroup.PlaySongViewGroup


class PlaylistSongsFragment : Fragment(), ServiceConnection {
    private lateinit var db: VibesMusicDatabase
    private lateinit var playlistSongsViewModel: PlaylistSongsViewModel
    private lateinit var playlistSongs: LiveData<PlaylistSongs>
    private lateinit var mediaPlayerService: MediaPlayerService
    private lateinit var playSongsView: PlaySongViewGroup

    private var _binding: FragmentPlaylistSongsBinding? = null
    private val binding: FragmentPlaylistSongsBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.db = VibesMusicDatabase.getInstance(this.requireActivity().application)
        this.playlistSongsViewModel = PlaylistSongsViewModelFactory.getFactory(this)
            .get(this.requireActivity().application, this.db)

        arguments?.let {
            val playlistSongsId: Int = it.getInt(UIUtil.PLAYLISTSONGS_KEY)
            this.playlistSongs = this.playlistSongsViewModel.getPlaylistSongs(playlistSongsId)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        this._binding = FragmentPlaylistSongsBinding.inflate(inflater)
        this.playSongsView = PlaySongViewGroup(this.requireContext())

        val songsAdapter: PlaylistSongsAdapter = PlaylistSongsAdapter(requireContext(), null, ArrayList())
        this.binding.playlistSongsListView.adapter = songsAdapter

        this.playlistSongs.observe(viewLifecycleOwner){
            this.binding.playlistViewTitle.text = it.playlist.name

            songsAdapter.playlistSongs = it
            songsAdapter.data.clear()
            songsAdapter.data.addAll(it.songs)
            songsAdapter.notifyDataSetChanged()
        }

        this.binding.playlistSongsListView.setOnItemClickListener { _, _, position, _ ->
            if(!PermissionsUtil.hasPermission(this.requireContext(), Manifest.permission.POST_NOTIFICATIONS))
                requireActivity().requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), PermissionsUtil.POST_NOTIF_CODE)
            val list: List<Song> = songsAdapter.data
            this.mediaPlayerService.setSongs(list, position)
            UIUtil.openSongPlayer(this.requireContext(), this.playSongsView, this.binding, this.requireActivity())
        }

        this.binding.playingSongsPlaylistBtn.setOnClickListener {
            UIUtil.openSongPlayer(this.requireContext(), this.playSongsView, this.binding, this.requireActivity())
        }

        this.binding.playlistsBackBtn.setOnClickListener {
            this.requireActivity().findNavController(R.id.nav_host_fragment)
                .navigate(R.id.playlistSongsFragmentToPlaylistFragment)
        }

        Ads.loadBanner(this.binding.playlistLibraryBanner, this.requireContext())

        return this.binding.root
    }

    override fun onStart() {
        super.onStart()
        if(!this::mediaPlayerService.isInitialized) {
            ServiceUtil.connectMediaPlayerService(this.requireContext(), this.requireActivity().application, this)
        }
    }

    override fun onResume() {
        super.onResume()
        UIUtil.setStatusBarColor(this.requireActivity(), R.color.foreground_color)
    }

    override fun onPause() {
        super.onPause()
        UIUtil.setStatusBarColor(this.requireActivity(), R.color.background_color)
    }

    override fun onDestroy() {
        super.onDestroy()
        this._binding = null
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MediaPlayerService.MediaPlayerServiceBinder
        this.mediaPlayerService = binder.mediaPlayerService
    }

    override fun onServiceDisconnected(name: ComponentName?) {}
}