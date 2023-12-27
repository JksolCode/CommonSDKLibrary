package com.example.jksolads.ads.Applovin

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAppOpenAd
import com.applovin.sdk.AppLovinSdk


class AppOpenApplovinAd(val context: Context, private val appOpenAdId: String) : LifecycleObserver,
    MaxAdListener {

    private var appOpenAd: MaxAppOpenAd? = null

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        appOpenAd = MaxAppOpenAd(appOpenAdId, context)
        appOpenAd!!.setListener(this)
        appOpenAd!!.loadAd()
    }

    private fun showAdIfAvailable() {
        if (appOpenAd == null || !AppLovinSdk.getInstance(context).isInitialized) return
        if (appOpenAd!!.isReady) {
            appOpenAd!!.showAd(appOpenAdId)
        } else {
            appOpenAd!!.loadAd()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        showAdIfAvailable()
    }

    override fun onAdLoaded(p0: MaxAd) {
    }

    override fun onAdDisplayed(p0: MaxAd) {
    }

    override fun onAdHidden(p0: MaxAd) {
        appOpenAd!!.loadAd();
    }

    override fun onAdClicked(p0: MaxAd) {
    }

    override fun onAdLoadFailed(p0: String, p1: MaxError) {
    }

    override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
    }
}