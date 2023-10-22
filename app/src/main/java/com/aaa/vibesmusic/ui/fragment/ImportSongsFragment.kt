package com.aaa.vibesmusic.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.databinding.FragmentImportSongsBinding
import com.aaa.vibesmusic.ui.UIUtil
import com.aaa.vibesmusic.ui.activity.result.ImportSongsActivityResultContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ImportSongsFragment : Fragment() {
    private var importSongsLauncher: ActivityResultLauncher<Void>? = null
    private val mDisposable: CompositeDisposable = CompositeDisposable()

    private var _binding: FragmentImportSongsBinding? = null
    private val binding: FragmentImportSongsBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.importSongsLauncher = registerForActivityResult(
            ImportSongsActivityResultContract(this.requireActivity())) {
                if(it.size > 0) {
                    val database: VibesMusicDatabase = VibesMusicDatabase.getInstance(context)
                    mDisposable.add(
                        database.songDao().insertSongs(it)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                UIUtil.showLongToast(
                                    requireContext(),
                                    "Music imported successfully"
                                )
                            }
                    )
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
}