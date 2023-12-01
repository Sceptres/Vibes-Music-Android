package com.aaa.vibesmusic.ui.fragment.library

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.databinding.FragmentSongLibraryBinding
import com.aaa.vibesmusic.monetization.Ads
import com.aaa.vibesmusic.player.MediaPlayerService
import com.aaa.vibesmusic.ui.adapters.SongsArrayAdapter
import com.aaa.vibesmusic.ui.viewgroup.PlaySongViewGroup

class SongLibraryFragment : Fragment(), ServiceConnection {
    private lateinit var viewModel: SongLibraryViewModel
    private lateinit var mediaPlayerService: MediaPlayerService

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

            if(this::mediaPlayerService.isInitialized)
                this.mediaPlayerService.updateSongs(songsAdapter.data)
        }

        binding.songsListView.setOnItemClickListener { parent, view, position, id ->
            val list: List<Song> = songsAdapter.data
            this.mediaPlayerService.setSongs(list, position)
            this.openSongPlayer()
        }

        binding.playingSongsActivityBtn.setOnClickListener {
            this.openSongPlayer()
        }

        Ads.loadBanner(binding.musicLibraryBanner, this.requireContext())

        return binding.root
    }

    /**
     * Opens the song player view
     */
    private fun openSongPlayer() {
        val animation: Animation = AnimationUtils.loadAnimation(this.requireContext(), R.anim.slide_up)
        val playSongsView = PlaySongViewGroup(this.context)
        playSongsView.setOnCloseListener{
            binding.root.visibility = View.VISIBLE
            binding.root.removeView(playSongsView)
        }
        playSongsView.startAnimation(animation)
        this.requireActivity().addContentView(
            playSongsView,
            RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
        )
        binding.root.postDelayed({ binding.root.visibility = View.GONE }, animation.duration)
    }

    override fun onStart() {
        super.onStart()
        val serviceIntent: Intent = Intent(this.requireContext(), MediaPlayerService::class.java)
        this.requireActivity().application.bindService(serviceIntent, this, AppCompatActivity.BIND_AUTO_CREATE)
        this.requireActivity().application.startService(serviceIntent)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MediaPlayerService.MediaPlayerServiceBinder
        this.mediaPlayerService = binder.mediaPlayerService
    }

    override fun onServiceDisconnected(name: ComponentName?) {}
}