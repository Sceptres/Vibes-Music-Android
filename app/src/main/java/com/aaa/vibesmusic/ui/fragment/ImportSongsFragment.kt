package com.aaa.vibesmusic.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.database.data.music.SongDao
import com.aaa.vibesmusic.databinding.FragmentImportSongsBinding
import com.aaa.vibesmusic.exceptions.SongAlreadyExistsException
import com.aaa.vibesmusic.metadata.SongMetaData
import com.aaa.vibesmusic.metadata.retriever.SongMetadataRetriever
import com.aaa.vibesmusic.storage.StorageUtil
import com.aaa.vibesmusic.ui.UIUtil
import com.aaa.vibesmusic.ui.fragment.result.ImportSongsActivityResultContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.FileDescriptor
import java.io.IOException
import java.util.Objects

class ImportSongsFragment : Fragment() {
    private var importSongsLauncher: ActivityResultLauncher<Void>? = null
    private val mDisposable: CompositeDisposable = CompositeDisposable()
    private var db: VibesMusicDatabase? = null

    private var _binding: FragmentImportSongsBinding? = null
    private val binding: FragmentImportSongsBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.db = VibesMusicDatabase.getInstance(context)

        // Setup the import songs activity result launcher
        this.importSongsLauncher = registerForActivityResult(ImportSongsActivityResultContract()) {
            if(it.isNotEmpty()) {
                val importedSongs: List<Song> = this.importSongsFromUris(it)

                // Were there any songs imported to be added to the database?
                if (importedSongs.isNotEmpty()) {
                    mDisposable.add(
                        this.db!!.songDao().insertSongs(importedSongs)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                UIUtil.showLongSnackBar(
                                    requireView(),
                                    resources.getString(R.string.songs_imported_successfully),
                                    resources.getColor(R.color.foreground_color, null)
                                )
                            }
                    )
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImportSongsBinding.inflate(inflater)

        binding.importLocalFiles.setOnClickListener {
            this.importSongsLauncher?.launch(null)
        }

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        this.mDisposable.clear()
    }

    /**
     *
     * @param contentUri The [Uri] of the song to extract
     * @return The [Song] instance that has been imported
     * @throws IOException If there was an error copying the song
     * @throws SongAlreadyExistsException If the song being imported already exists
     */
    @Throws(IOException::class, SongAlreadyExistsException::class)
    private fun parseUri(contentUri: Uri, metaData: SongMetaData): Song {
        val name = metaData.name
        val artist = metaData.artist
        val albumName = metaData.albumName
        val image = metaData.image
        val duration = metaData.duration

        val songDao: SongDao = this.db!!.songDao()
        if (songDao.doesSongExist(name, artist, albumName)) {
            Log.d("SONG NAME", name)
            throw SongAlreadyExistsException()
        }

        var songLocation: String?
        var songImageLocation: String? = null
        var wasSongSaved: Boolean

        val resolver = this.requireActivity().contentResolver
        resolver.openFileDescriptor(contentUri, "r").use { fd ->
            val fileDescriptor: FileDescriptor = fd!!.fileDescriptor
            val fileName: String = StorageUtil.getFileName(resolver, contentUri)
            wasSongSaved = StorageUtil.saveSong(fileDescriptor, fileName)
            songLocation = StorageUtil.getSongPath(fileName)
        }

        if (!wasSongSaved)
            throw IOException("Unable to save the song!")

        if (Objects.nonNull(image)) {
            StorageUtil.saveSongImage(image, name)
            songImageLocation = StorageUtil.getSongImagePath(name)
        }
        return Song(
            songLocation!!,
            name,
            artist,
            albumName,
            songImageLocation,
            duration
        )
    }

    /**
     *
     * @param uris The [List] of [Uri]s that have been chosen by the user
     * @return The [List] of [Song]s that were imported to the application
     */
    private fun importSongsFromUris(uris: List<Uri>): List<Song> {
        val songs: MutableList<Song> = ArrayList()
        val context = this.requireActivity().applicationContext

        var currentSong = ""
        val failedSongs: MutableList<String> = ArrayList()
        val existingSongs: MutableList<String> = ArrayList()

        for (uri in uris) {
            try {
                SongMetadataRetriever(context, uri).use { retriever ->
                    val metaData = retriever.allMetaData
                    currentSong = metaData.name
                    val song = this.parseUri(uri, metaData)
                    songs.add(song)
                }
            } catch (e: IOException) {
                failedSongs.add(currentSong)
            } catch (e: SongAlreadyExistsException) {
                existingSongs.add(currentSong)
            }
        }

        this.handleFailedSongs(failedSongs)
        this.handleExistingSongs(existingSongs)

        return songs
    }

    /**
     * @param failedSongs The [List] of songs that were failed to be imported
     */
    private fun handleFailedSongs(failedSongs: List<String>) {
        if (failedSongs.isNotEmpty()) {
            val msg: String = if(failedSongs.size > 1)
                resources.getString(R.string.failed_songs, failedSongs.joinToString(", "))
            else
                resources.getString(R.string.failed_song, failedSongs.joinToString(", "))

            UIUtil.showLongSnackBar(
                this.requireView(),
                msg,
                resources.getColor(R.color.foreground_color, null)
            )
        }
    }

    /**
     * @param existingSongs The [List] of songs by their names that were deemed to have already been
     * imported
     */
    private fun handleExistingSongs(existingSongs: List<String>) {
        if (existingSongs.isNotEmpty()) {
            val msg: String = if(existingSongs.size > 1) // Has more than one element?
                resources.getString(R.string.songs_exist, existingSongs.joinToString(", "))
            else
                resources.getString(R.string.song_exists, existingSongs.joinToString(", "))

            UIUtil.showLongSnackBar(
                this.requireView(),
                msg,
                resources.getColor(R.color.foreground_color, null)
            )
        }
    }
}