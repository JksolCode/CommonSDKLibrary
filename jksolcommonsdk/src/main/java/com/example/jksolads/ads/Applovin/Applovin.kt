package com.example.jksolads.ads.Applovin

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.widget.FrameLayout
import com.ads.control.ads.AperoAd
import com.ads.control.ads.bannerAds.AperoBannerAdView
import com.ads.control.ads.wrapper.ApInterstitialAd
//import com.ads.control.ads.AperoAd
//import com.ads.control.ads.bannerAds.AperoBannerAdView
//import com.ads.control.ads.wrapper.ApInterstitialAd
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder
import com.example.jksolads.R
import com.example.jksolads.ads.AdsListener
import com.example.jksolads.ads.AdsLoadListener
import com.example.jksolads.ads.NativeAdLayoutType
import com.example.jksolads.ads.ScreenAds
import com.example.jksolads.ads.aperoAds.AperoAds
import com.facebook.shimmer.ShimmerFrameLayout
import com.yandex.metrica.YandexMetrica


class Applovin {

    var mLanguageNativeAd: MaxAd? = null
    var mExitNativeAd: MaxAd? = null
    var mNativeAd: MaxAd? = null
    var mLanguageNativeAdView: MaxNativeAdView? = null
    var mExitNativeAdView: MaxNativeAdView? = null
    var mNativeAdView: MaxNativeAdView? = null
    var mInterstitialAd: MaxInterstitialAd? = null
    lateinit var mProgressDialog: ProgressDialog
    var isLoading: Boolean = false
    var isLanguageLoading: Boolean = false

    companion object {
        var applovin: Applovin? = null

        fun getInstance(): Applovin {
            if (applovin == null) {
                applovin = Applovin()
            }
            return applovin!!
        }
    }

    fun loadInterstitialAds(
        activity: Activity?,
        interstitialAdId: String,
        screenAds: String = "",
        screenType: ScreenAds,
        trackingScreen: String = "",
        enableLoadBackUpAds: Boolean = false,
        enableResetReload: Boolean = false,
        adsLoadListener: AdsLoadListener?
    ) {
        isLoading = true
        mInterstitialAd = MaxInterstitialAd(interstitialAdId, activity)
        mInterstitialAd!!.setListener(object : MaxAdListener {
            override fun onAdLoaded(p0: MaxAd) {
                isLoading = false
                adsLoadListener!!.onAdsLoaded()
            }

            override fun onAdDisplayed(p0: MaxAd) {
            }

            override fun onAdHidden(p0: MaxAd) {
            }

            override fun onAdClicked(p0: MaxAd) {
            }

            override fun onAdLoadFailed(p0: String, p1: MaxError) {
                isLoading = false
                adsLoadListener!!.onAdsLoadFail(p1.code)
            }

            override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
            }

        })
        mInterstitialAd!!.loadAd()
    }

    fun showInterstitialAds(
        activity: Activity?,
        screen: String = "",
        trackingScreen: String = "",
        showLoading: Boolean = false,
        adsListener: AdsListener?
    ) {

        try {
            YandexMetrica.reportEvent("Show Interstitial Ad");
        } catch (e: Exception) {
            e.printStackTrace();
        }

        if (showLoading) {
            mProgressDialog = ProgressDialog(activity)
            mProgressDialog.setTitle(activity?.getString(R.string.showing_ads))
            mProgressDialog.show()

            Handler(Looper.getMainLooper()).postDelayed({
                mProgressDialog.dismiss()
                showInterstitialAds(activity, screen, trackingScreen, adsListener)
            }, 1000)
        } else {
            showInterstitialAds(activity, screen, trackingScreen, adsListener)
        }

    }

    private fun showInterstitialAds(
        activity: Activity?,
        screen: String = "",
        trackingScreen: String = "",
        adsListener: AdsListener?
    ) {
        if (mInterstitialAd != null) {
            mInterstitialAd!!.setListener(object : MaxAdListener {
                override fun onAdLoaded(p0: MaxAd) {

                }

                override fun onAdDisplayed(p0: MaxAd) {
                    TODO("Not yet implemented")
                }

                override fun onAdHidden(p0: MaxAd) {
                    adsListener!!.onAdsDismiss()
                }

                override fun onAdClicked(p0: MaxAd) {
                    TODO("Not yet implemented")
                }

                override fun onAdLoadFailed(p0: String, p1: MaxError) {
                    adsListener!!.onAdsShowFail(p1.code)
                }

                override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
                    adsListener!!.onAdsShowFail(p1.code)
                }

            })

            if (mInterstitialAd!!.isReady) {
                mInterstitialAd!!.showAd();
            }
        }
    }

    fun loadAndShowInterstitialAd(
        activity: Activity?,
        interstitialAdId: String, adsListener: AdsListener?
    ) {
        val interstitialAd = MaxInterstitialAd(interstitialAdId, activity)
        interstitialAd.setListener(object : MaxAdListener {
            override fun onAdLoaded(p0: MaxAd) {
                if (interstitialAd.isReady) {
                    interstitialAd.showAd();
                }
            }

            override fun onAdDisplayed(p0: MaxAd) {
                TODO("Not yet implemented")
            }

            override fun onAdHidden(p0: MaxAd) {
                adsListener!!.onAdsDismiss()
            }

            override fun onAdClicked(p0: MaxAd) {
                TODO("Not yet implemented")
            }

            override fun onAdLoadFailed(p0: String, p1: MaxError) {
                adsListener!!.onAdsShowFail(p1.code)
            }

            override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
                adsListener!!.onAdsShowFail(p1.code)
            }

        })
        interstitialAd.loadAd()
    }

    fun getAperoInterstitialAd(activity: Activity?,
                               interstitialAdId: String,  adsListener: AdsListener?) : ApInterstitialAd {
        return AperoAd.getInstance().getInterstitialAds(activity, interstitialAdId)
    }

    fun preLoadNativeAds(
        activity: Activity?,
        nativeAdId: String,
        layout: Int,
        listenerAdapter: AdsLoadListener?
    ) {
        val nativeAdLoader = MaxNativeAdLoader(nativeAdId, activity)
        nativeAdLoader.setNativeAdListener(object : MaxNativeAdListener() {
            override fun onNativeAdLoaded(p0: MaxNativeAdView?, p1: MaxAd) {
                super.onNativeAdLoaded(p0, p1)
                listenerAdapter!!.onAdsLoaded()
                mNativeAd = p1
                mNativeAdView = p0
            }

            override fun onNativeAdLoadFailed(p0: String, p1: MaxError) {
                super.onNativeAdLoadFailed(p0, p1)
                listenerAdapter!!.onAdsLoadFail(p1.code)
            }
        })

        val binder = MaxNativeAdViewBinder.Builder(layout)
            .build()
        nativeAdLoader.loadAd(MaxNativeAdView(binder, activity))
    }

    fun preLoadLanguageNativeAds(
        activity: Activity?,
        nativeAdId: String,
        layout: Int,
        listenerAdapter: AdsLoadListener?
    ) {
        isLanguageLoading = true
        val nativeAdLoader = MaxNativeAdLoader(nativeAdId, activity)
        nativeAdLoader.setNativeAdListener(object : MaxNativeAdListener() {
            override fun onNativeAdLoaded(p0: MaxNativeAdView?, p1: MaxAd) {
                super.onNativeAdLoaded(p0, p1)
                listenerAdapter!!.onAdsLoaded()
                isLanguageLoading = false
                mLanguageNativeAd = p1
                mLanguageNativeAdView = p0
            }

            override fun onNativeAdLoadFailed(p0: String, p1: MaxError) {
                super.onNativeAdLoadFailed(p0, p1)
                isLanguageLoading = false
                listenerAdapter!!.onAdsLoadFail(p1.code)
            }
        })

        val binder = MaxNativeAdViewBinder.Builder(layout)
            .build()
        nativeAdLoader.loadAd(MaxNativeAdView(binder, activity))
    }

    fun preLoadExitNativeAds(
        activity: Activity?,
        nativeAdId: String,
        layout: Int,
        listenerAdapter: AdsLoadListener?
    ) {
        val nativeAdLoader = MaxNativeAdLoader(nativeAdId, activity)
        nativeAdLoader.setNativeAdListener(object : MaxNativeAdListener() {
            override fun onNativeAdLoaded(p0: MaxNativeAdView?, p1: MaxAd) {
                super.onNativeAdLoaded(p0, p1)
                listenerAdapter!!.onAdsLoaded()
                mExitNativeAd = p1
                mExitNativeAdView = p0
            }

            override fun onNativeAdLoadFailed(p0: String, p1: MaxError) {
                super.onNativeAdLoadFailed(p0, p1)
                listenerAdapter!!.onAdsLoadFail(p1.code)
            }
        })

        val binder = MaxNativeAdViewBinder.Builder(layout)
            .build()
        nativeAdLoader.loadAd(MaxNativeAdView(binder, activity))
    }

    fun showPreLoadNativeAd(
        activity: Activity?,
        viewGroup: ViewGroup?,
        screen: String = "",
        trackingScreen: String = "",
        layout: Int,
        layoutType: NativeAdLayoutType
    ) {
        viewGroup!!.removeAllViews()
        viewGroup!!.addView(mNativeAdView)
        mNativeAd = null
    }

    fun showPreLoadLanguageNativeAd(
        activity: Activity?,
        viewGroup: ViewGroup?,
        screen: String = "",
        trackingScreen: String = "",
        layout: Int,
        layoutType: NativeAdLayoutType
    ) {
        viewGroup!!.removeAllViews()
        viewGroup!!.addView(mExitNativeAdView)
        mExitNativeAd = null
    }

    fun showPreLoadExitNativeAd(
        activity: Activity?,
        viewGroup: ViewGroup?,
        screen: String = "",
        trackingScreen: String = "",
        layout: Int,
        layoutType: NativeAdLayoutType
    ) {
        viewGroup!!.removeAllViews()
        viewGroup!!.addView(mLanguageNativeAdView)
        mLanguageNativeAd = null
    }

    fun loadNativeAds(
        activity: Context?,
        nativeAdId: String,
        viewGroup: ViewGroup?,
        screen: String = "",
        trackingScreen: String = "",
        layout: Int,
        layoutType: NativeAdLayoutType,
        listenerAdapter: AdsLoadListener?
    ) {
        val nativeAdLoader = MaxNativeAdLoader(nativeAdId, activity)
        nativeAdLoader.setNativeAdListener(object : MaxNativeAdListener() {
            override fun onNativeAdLoaded(p0: MaxNativeAdView?, p1: MaxAd) {
                super.onNativeAdLoaded(p0, p1)
                listenerAdapter!!.onAdsLoaded()
                viewGroup!!.removeAllViews()
                viewGroup!!.addView(p0)
            }

            override fun onNativeAdLoadFailed(p0: String, p1: MaxError) {
                super.onNativeAdLoadFailed(p0, p1)
                listenerAdapter!!.onAdsLoadFail(p1.code)
            }
        })

        val binder = MaxNativeAdViewBinder.Builder(layout)
            .build()
        nativeAdLoader.loadAd(MaxNativeAdView(binder, activity))
    }

    fun loadBannerAd(
        activity: Activity?,
        bannerAdId: String,
        viewGroup: ViewGroup?,
        adsLoadListener: AdsLoadListener
    ) {
        val adView = MaxAdView(bannerAdId, activity)
        adView.setListener(object : MaxAdViewAdListener {
            override fun onAdLoaded(p0: MaxAd) {
                adsLoadListener.onAdsLoaded()
            }

            override fun onAdDisplayed(p0: MaxAd) {
            }

            override fun onAdHidden(p0: MaxAd) {
            }

            override fun onAdClicked(p0: MaxAd) {
            }

            override fun onAdLoadFailed(p0: String, p1: MaxError) {
                adsLoadListener.onAdsLoadFail(p1.code)
            }

            override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
            }

            override fun onAdExpanded(p0: MaxAd) {
            }

            override fun onAdCollapsed(p0: MaxAd) {
            }
        })

        viewGroup!!.addView(adView);
        // Load the ad
        adView.loadAd();
    }

    fun loadAperoLanguageNativeAd(
        activity: Activity?,
        nativeId: String,
        layout: Int?,
        listenerAdapter: AdsLoadListener?
    ) {

        AperoAds.getInstance()!!
            .loadLanguageNativeAd(activity!!, nativeId, layout!!, listenerAdapter!!)
    }

    fun showAperoLanguageNativeAd(
        activity: Activity?,
        nativeId: String,
        layout: Int?,
        nativeAdView: FrameLayout,
        shimmerFrameLayout: ShimmerFrameLayout,
        listenerAdapter: AdsLoadListener?
    ) {

        AperoAds.getInstance()!!.showLanguageNativeAd(
            activity!!,
            nativeId,
            layout!!,
            nativeAdView,
            shimmerFrameLayout,
            listenerAdapter!!
        )
    }

    fun loadAndShowAperoNativeAd(
        activity: Activity?,
        nativeId: String,
        layout: Int?,
        nativeAdView: FrameLayout,
        shimmerFrameLayout: ShimmerFrameLayout,
        listenerAdapter: AdsLoadListener?
    ) {

        AperoAds.getInstance()!!.loadAndShowNativeAd(
            activity!!,
            nativeId,
            layout!!,
            nativeAdView,
            shimmerFrameLayout,
            listenerAdapter!!
        )
    }

    fun showAperoBannerAd(
        activity: Activity?,
        bannerAdId: String?,
        viewGroup: AperoBannerAdView?,
        adEventListener: AdsLoadListener?

    ) {
        AperoAds.getInstance()!!.loadBanner(activity!!, viewGroup!!, adEventListener, bannerAdId!!)
    }
}