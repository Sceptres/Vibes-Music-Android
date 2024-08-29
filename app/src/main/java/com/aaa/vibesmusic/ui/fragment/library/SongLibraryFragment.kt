package com.aaa.vibesmusic.ui.fragment.library

import android.Manifest
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.databinding.FragmentSongLibraryBinding
import com.aaa.vibesmusic.monetization.Ads
import com.aaa.vibesmusic.perms.PermissionsUtil
import com.aaa.vibesmusic.player.MediaPlayerService
import com.aaa.vibesmusic.player.ServiceUtil
import com.aaa.vibesmusic.ui.viewgroup.PlaySongViewGroup

class SongLibraryFragment : Fragment(), ServiceConnection {
    private lateinit var viewModel: SongLibraryViewModel
    private lateinit var playSongsView: PlaySongViewGroup
    private lateinit var mediaPlayerService: MediaPlayerService

    private var _binding: FragmentSongLibraryBinding? = null
    private val binding: FragmentSongLibraryBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSongLibraryBinding.inflate(inflater)

        this.playSongsView = PlaySongViewGroup(this.requireContext())

        val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(context)
        this.viewModel = SongLibraryViewModelFactory.getFactory(this)
            .get(requireActivity().application, db)

        // Add songs to the UI
        this.viewModel.songs.observe(viewLifecycleOwner) {}

        binding.songsListView.setOnItemClickListener { parent, view, position, id ->
            if(!PermissionsUtil.hasPermission(this.requireContext(), Manifest.permission.POST_NOTIFICATIONS))
                requireActivity().requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), PermissionsUtil.POST_NOTIF_CODE)
            val list: List<Song> = listOf()
            this.mediaPlayerService.setSongs(list, position)
        }

        binding.playingSongsActivityBtn.setOnClickListener {
        }

        Ads.loadBanner(binding.musicLibraryBanner, this.requireContext())

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(playSongsView.isShown) {
                    playSongsView.closeView()
                } else {
                    isEnabled = false
                    requireActivity().moveTaskToBack(true)
                }
            }
        })

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if(!this::mediaPlayerService.isInitialized) {
            ServiceUtil.connectMediaPlayerService(this.requireContext(), this.requireActivity().application, this)
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MediaPlayerService.MediaPlayerServiceBinder
        this.mediaPlayerService = binder.mediaPlayerService
    }

    override fun onServiceDisconnected(name: ComponentName?) {}
}