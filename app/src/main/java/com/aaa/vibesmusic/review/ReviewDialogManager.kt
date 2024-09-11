package com.aaa.vibesmusic.review

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import com.aaa.vibesmusic.preferences.PreferencesManager
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory

class ReviewDialogManager(private val context: Context) {
    companion object {
        private const val REVIEW_SESSION_COUNT: Int = 7
    }

    private val reviewManager: ReviewManager = ReviewManagerFactory.create(this.context)
    private val activity: Activity = this.context as Activity
    private val app: Application = this.activity.application
    private val preferencesManager: PreferencesManager = PreferencesManager(this.app)

    fun launchReviewDialog() {
        if(this.preferencesManager.numSessions > REVIEW_SESSION_COUNT) {
            this.reviewManager.requestReviewFlow().addOnCompleteListener {
                if (it.isSuccessful) {
                    this.reviewManager.launchReviewFlow(this.activity, it.result)
                    Log.d("SESSION", "SUCCESS")
                }
            }
        }
    }
}