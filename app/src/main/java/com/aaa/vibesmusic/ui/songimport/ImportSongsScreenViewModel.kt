package com.aaa.vibesmusic.ui.songimport

import android.app.Activity
import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.exceptions.SongAlreadyExistsException
import com.aaa.vibesmusic.metadata.SongMetaData
import com.aaa.vibesmusic.metadata.retriever.SongMetadataRetriever
import com.aaa.vibesmusic.monetization.Ads
import com.aaa.vibesmusic.storage.StorageUtil
import com.aaa.vibesmusic.ui.UIUtil
import com.aaa.vibesmusic.ui.notification.ImportSongsNotification
import com.aaa.vibesmusic.ui.songimport.result.ImportSongsActivityResultContract
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Objects

class ImportSongsScreenViewModel(application: Application, private val globalScope: CoroutineScope) : AndroidViewModel(application) {
    companion object {
        fun getFactory(scope: CoroutineScope): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    ImportSongsScreenViewModel(this[APPLICATION_KEY] as Application, scope)
                }
            }
        }
    }

    private val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(this.getApplication())
    private val disposables: CompositeDisposable = CompositeDisposable()

    private var failedSongs: Int = 0
    private var existingSongs: Int = 0

    @Composable
    fun ActivityFilesLauncher(
        snackBarState: SnackbarHostState,
        snackBarScope: CoroutineScope
    ): ManagedActivityResultLauncher<Void?, List<Uri>> {
        val context: Context = LocalContext.current

        return rememberLauncherForActivityResult(contract = ImportSongsActivityResultContract()) { uris ->
            this.viewModelScope.launch(Dispatchers.Main) {
                if(uris.size > 0) {
                    val importSongsCallback: () -> Unit = {
                        importSongs(
                            uris,
                            snackBarState,
                            snackBarScope
                        )
                    }

                    showImportAd(
                        context = context,
                        onAdDismissed = importSongsCallback,
                        onAdFailedToShow = importSongsCallback,
                        onAdFailedToLoad = importSongsCallback
                    )
                }
            }
        }
    }

    private fun showImportAd(
        context: Context,
        onAdDismissed: () -> Unit,
        onAdFailedToShow: () -> Unit,
        onAdFailedToLoad: () -> Unit
    ) {
        Ads.loadInterstitial(context, Ads.IMPORT_MUSIC_AD_ID, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(p0: InterstitialAd) {
                super.onAdLoaded(p0)
                p0.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent()
                        onAdDismissed()
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        super.onAdFailedToShowFullScreenContent(p0)
                        onAdFailedToShow()
                    }
                }
                p0.show(context as Activity)
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                onAdFailedToLoad()
            }
        })
    }


    /******************************************IMPORT SONGS FROM LOCAL STORAGE LOGIC******************************************/
    private fun importSongs(
        uris: List<Uri>,
        snackBarState: SnackbarHostState,
        snackBarScope: CoroutineScope
    ) {
        this.globalScope.launch(Dispatchers.IO) {
            val importNotification: ImportSongsNotification =
                ImportSongsNotification(getApplication())
            importNotification.show()

            val importedSongs: List<Song> = importSongsFromUris(uris, importNotification)

            importNotification.update("Updating database......", 0)
            delay(1000)

            if(existingSongs > 0) {
                UIUtil.showSnackBar(
                    snackBarState = snackBarState,
                    snackBarScope = snackBarScope,
                    message = getExistingSongsMsg()
                )
                importNotification.update("Found $existingSongs songs that have already been imported. Ignoring...", 100)
            }

            delay(1000)

            if (failedSongs > 0) {
                UIUtil.showSnackBar(
                    snackBarState = snackBarState,
                    snackBarScope = snackBarScope,
                    message = getFailedSongsMsg()
                )
                importNotification.update("Failed to import $existingSongs songs. Please try again!", 100)
            }

            // Were there any songs imported to be added to the database?
            if (importedSongs.isNotEmpty()) {
                disposables.add(
                    db.songDao().insertSongs(importedSongs)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            val importedSongsCount: Int = uris.size - failedSongs - existingSongs

                            UIUtil.showSnackBar(
                                snackBarState = snackBarState,
                                snackBarScope = snackBarScope,
                                message = "Successfully imported $importedSongsCount songs."
                            )
                            failedSongs = 0
                            existingSongs = 0

                            importNotification.update("Imported $importedSongsCount songs successfully!", 100)
                            importNotification.close()
                        }
                )
            } else {
                delay(1000)
                importNotification.close()
            }
        }
    }

    /**
     *
     * @param contentUri The [List] of the song to extract
     * @return The [Song] instance that has been imported
     * @throws IOException If there was an error copying the song
     * @throws SongAlreadyExistsException If the song being imported already exists
     */
    @Throws(IOException::class, SongAlreadyExistsException::class)
    private fun parseUri(contentUri: Uri, metaData: SongMetaData): Song {
        val resolver: ContentResolver = this.getApplication<Application>().contentResolver
        val fileName = StorageUtil.getFileName(resolver, contentUri)
        val name = if (metaData.name == SongMetadataRetriever.SONG_NAME_DEFAULT) fileName else metaData.name
        val artist = metaData.artist
        val albumName = metaData.albumName
        val image = metaData.image
        val duration = metaData.duration
        val songDao = this.db.songDao()

        if (songDao.doesSongExist(name, artist, albumName))
            throw SongAlreadyExistsException()

        var songLocation: String?
        var songImageLocation: String? = null
        var wasSongSaved: Boolean

        resolver.openFileDescriptor(contentUri, "r").use { fd ->
            val fileDescriptor = fd!!.fileDescriptor
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
    private fun importSongsFromUris(uris: List<Uri>, importSongNotification: ImportSongsNotification): List<Song> {
        val songs: MutableList<Song> = ArrayList()
        for (i in uris.indices) {
            Log.d("INDEX", i.toString())
            val uri: Uri = uris[i]
            try {
                importSongNotification.update("Retrieving song ${i+1}/${uris.size}", 0)
                SongMetadataRetriever(this.getApplication(), uri).use { retriever ->
                    val metaData = retriever.getAllMetaData()
                    importSongNotification.update("Retrieving song ${i+1}/${uris.size}", 50)
                    val song = parseUri(uri, metaData)
                    importSongNotification.update("Importing song ${i+1}/${uris.size} file into library", 100)
                    songs.add(song)
                }
            } catch (e: IOException) {
                this.failedSongs += 1
            } catch (e: SongAlreadyExistsException) {
                this.existingSongs += 1
            }
        }
        return songs
    }

    private fun getFailedSongsMsg(): String {
        val resources: Resources = this.getApplication<Application>().resources

        return if (this.failedSongs > 1)
            resources.getString(R.string.failed_songs)
        else
            resources.getString(R.string.failed_song)
    }

    private fun getExistingSongsMsg(): String {
        val resources: Resources = this.getApplication<Application>().resources

        return if (this.existingSongs > 1)
            resources.getString(R.string.songs_exist)
        else
            resources.getString(R.string.song_exists)
    }

    override fun onCleared() {
        super.onCleared()
        this.disposables.clear()
    }
}