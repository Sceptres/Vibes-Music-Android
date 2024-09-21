package com.aaa.vibesmusic.database.util

import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Subscribes the completable on the io scheduler and observes on the main thread scheduler
 */
fun Completable.subscribeObserveOn(): Completable {
    return this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

fun Completable.subscribeTo(): Disposable {
    return this.subscribeObserveOn()
        .subscribe()
}

fun Completable.subscribeTo(onSuccess: () -> Unit): Disposable {
    return this.subscribeObserveOn()
        .subscribe(onSuccess)
}

fun Completable.subscribeTo(onSuccess: () -> Unit, onFail: (Throwable) -> Unit): Disposable {
    return this.subscribeObserveOn()
        .subscribe(onSuccess, onFail)
}