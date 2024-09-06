package com.aaa.vibesmusic.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.monetization.Ads
import com.aaa.vibesmusic.ui.fragment.result.ImportSongsActivityResultContract
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import io.reactivex.disposables.CompositeDisposable
import java.util.Date

class ImportSongsFragment : Fragment() {
    private var importSongsLauncher: ActivityResultLauncher<Void?>? = null
    private val mDisposable: CompositeDisposable = CompositeDisposable()
    private var db: VibesMusicDatabase? = null
    private var lastShownAd: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.db = VibesMusicDatabase.getInstance(context)

        // Setup the import songs activity result launcher
        this.importSongsLauncher = registerForActivityResult(ImportSongsActivityResultContract()) {
            if(it.isNotEmpty()) {
                val currentTime = Date().time

                if(currentTime - lastShownAd >= Ads.IMPORT_AD_TIME_DIFF) {
                    this.showImportSongAd(it, currentTime)
                } else {
                    importSongs(it)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       return null
    }

    override fun onStop() {
        super.onStop()
        this.mDisposable.clear()
    }

    /**
     * @param songs The [List] of [Song]s to import into the app
     */
    private fun importSongs(songs: List<Uri>) {
        val importThread = ImportSongsThread(this.requireActivity().applicationContext, this.db, songs)
        importThread.start()
    }

    /**
     * @param it The [List] of [Uri]s selected by the user
     * @param currentTime The time at which the request to show this ad was made.
     */
    private fun showImportSongAd(it: List<Uri>, currentTime: Long) {
        Ads.loadInterstitial(this.requireActivity(), Ads.IMPORT_MUSIC_AD_ID, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(p0: InterstitialAd) {
                super.onAdLoaded(p0)
                lastShownAd = currentTime
                p0.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent()
                        importSongs(it)
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        super.onAdFailedToShowFullScreenContent(p0)
                        importSongs(it)
                    }
                }
                p0.show(requireActivity())
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                importSongs(it)
            }
        })
    }


}