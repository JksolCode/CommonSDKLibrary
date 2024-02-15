package com.example.jksolads.ads.AdMob

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.OnHierarchyChangeListener
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.ads.control.ads.AperoAd
import com.ads.control.ads.bannerAds.AperoBannerAdView
import com.ads.control.ads.wrapper.ApInterstitialAd
//import com.ads.control.ads.AperoAd
//import com.ads.control.ads.bannerAds.AperoBannerAdView
//import com.ads.control.ads.wrapper.ApInterstitialAd
import com.example.jksolads.R
import com.example.jksolads.ads.AdsListener
import com.example.jksolads.ads.AdsLoadListener
import com.example.jksolads.ads.NativeAdLayoutType
import com.example.jksolads.ads.ScreenAds
import com.example.jksolads.ads.aperoAds.AperoAds
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoController.VideoLifecycleCallbacks
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.yandex.metrica.YandexMetrica


open class AdMob {

    var mInterstitialAd: InterstitialAd? = null
    var mNativeAd: NativeAd? = null
    var mLanguageNativeAd: NativeAd? = null
    var mExitNativeAd: NativeAd? = null
    var isLoading: Boolean = false
    var isLanguageLoading: Boolean = false
    lateinit var mProgressDialog: ProgressDialog
    var enableLoadBackUpAd: Boolean = false
    var interstitialAdId: String = ""

    companion object {
        var adMob: AdMob? = null

        fun getInstance(): AdMob {
            if (adMob == null) {
                adMob = AdMob()
            }
            return adMob!!
        }
    }

    fun setAperoLanguageNativeAd() {
        AperoAds().mLangNativeAd = null
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
        this.interstitialAdId = interstitialAdId
        val adRequest: AdRequest = AdRequest.Builder().build()
        InterstitialAd.load(activity!!, interstitialAdId, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    Log.e("InterstitialAd ---------->", "onAdLoaded")
                    isLoading = false
                    mInterstitialAd = interstitialAd
                    adsLoadListener!!.onAdsLoaded()
                    try {
                        YandexMetrica.reportEvent("Interstitial Ad Loaded");
                    } catch (e: Exception) {
                        e.printStackTrace();
                    }
                    enableLoadBackUpAd = enableLoadBackUpAds
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isLoading = false
                    adsLoadListener!!.onAdsLoadFail(loadAdError.code)
                    Log.e("onAdFailedToLoad ---------->", loadAdError.message)
                    // Handle the error
                    try {
                        YandexMetrica.reportEvent("Interstitial Ad Load Failed");
                    } catch (e: Exception) {
                        e.printStackTrace();
                    }
                    if (enableResetReload) {
                        loadInterstitialAds(
                            activity,
                            interstitialAdId,
                            screenAds,
                            screenType,
                            trackingScreen,
                            enableLoadBackUpAds, enableResetReload, adsLoadListener
                        )
                    }
                }
            })
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
        if (mInterstitialAd != null) {
            if (showLoading) {
                mProgressDialog = ProgressDialog(activity)
                mProgressDialog.setMessage(activity?.getString(R.string.showing_ads))
                mProgressDialog.show()

                Handler(Looper.getMainLooper()).postDelayed({
                    mProgressDialog.dismiss()
                    showInterstitialAds(activity, screen, trackingScreen, adsListener)
                }, 1000)
            } else {
                showInterstitialAds(activity, screen, trackingScreen, adsListener)
            }
        }

    }

    fun loadAndShowInterstitialAd(
        activity: Activity?,
        interstitialAdId: String, adsListener: AdsListener?
    ) {
        var interstitialAd1: InterstitialAd? = null
        val adRequest: AdRequest = AdRequest.Builder().build()
        InterstitialAd.load(activity!!, interstitialAdId, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    isLoading = false
                    interstitialAd1 = interstitialAd
                    interstitialAd.show(activity)
                    interstitialAd1?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent()
                                adsListener?.onAdsDismiss()
                                AppOpenAdManager.isShowingAd = false
                                interstitialAd1 = null
                            }

                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                super.onAdFailedToShowFullScreenContent(p0)
                                adsListener?.onAdsShowFail(p0.code)
                                AppOpenAdManager.isShowingAd = false
                                try {
                                    YandexMetrica.reportEvent("Failed to show Interstitial Ad");
                                } catch (e: Exception) {
                                    e.printStackTrace();
                                }
                                mInterstitialAd = null
                            }

                            override fun onAdImpression() {
                                super.onAdImpression()
                                try {
                                    YandexMetrica.reportEvent("Interstitial Ad Impression");
                                } catch (e: Exception) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    try {
                        YandexMetrica.reportEvent("Interstitial Ad Loaded");
                    } catch (e: Exception) {
                        e.printStackTrace();
                    }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isLoading = false
                    adsListener!!.onAdsShowFail(loadAdError.code)
                    // Handle the error
                    try {
                        YandexMetrica.reportEvent("Interstitial Ad Load Failed");
                    } catch (e: Exception) {
                        e.printStackTrace();
                    }
                }
            })


    }

    fun getAperoInterstitialAd(
        activity: Activity?,
        interstitialAdId: String, adsListener: AdsListener?
    ): ApInterstitialAd {
        return AperoAd.getInstance().getInterstitialAds(activity, interstitialAdId)
    }

    private fun showInterstitialAds(
        activity: Activity?,
        screen: String = "",
        trackingScreen: String = "",
        adsListener: AdsListener?
    ) {
        Log.e("InterstitialAd ---------->", "showInterstitialAds")
        Log.e("InterstitialAd ---------->", "mInterstitialAd")
        if (mInterstitialAd != null) {
            AppOpenAdManager.isShowingAd = true
            mInterstitialAd?.show(activity!!)
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    adsListener?.onAdsDismiss()
                    AppOpenAdManager.isShowingAd = false
                    mInterstitialAd = null
                    if (enableLoadBackUpAd) {
                        loadInterstitialAds(
                            activity,
                            interstitialAdId,
                            screen,
                            ScreenAds.INTERSTITIAL,
                            trackingScreen,
                            enableLoadBackUpAds = false,
                            enableResetReload = false,
                            adsLoadListener = null
                        )
                    }
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                    adsListener?.onAdsShowFail(p0.code)
                    AppOpenAdManager.isShowingAd = false
                    try {
                        YandexMetrica.reportEvent("Failed to show Interstitial Ad");
                    } catch (e: Exception) {
                        e.printStackTrace();
                    }
                    mInterstitialAd = null
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    try {
                        YandexMetrica.reportEvent("Interstitial Ad Impression");
                    } catch (e: Exception) {
                        e.printStackTrace();
                    }
                }

                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                }
            }
        }
    }

    fun preLoadNativeAds(
        activity: Activity?,
        nativeAdId: String,
        listenerAdapter: AdsLoadListener?
    ) {
        val adLoader = AdLoader.Builder(activity!!, nativeAdId)
            .forNativeAd {
                mNativeAd = it
                listenerAdapter!!.onAdsLoaded()
                try {
                    YandexMetrica.reportEvent("Pre Load Native Ad Loaded");
                } catch (e: Exception) {
                    e.printStackTrace();
                }
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // Handle the failure by logging, altering the UI, and so on.
                    listenerAdapter?.onAdsLoadFail(adError.code)
                    try {
                        YandexMetrica.reportEvent("Pre Load Native Ad Failed to load");
                    } catch (e: Exception) {
                        e.printStackTrace();
                    }
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    // Methods in the NativeAdOptions.Builder class can be
                    // used here to specify individual options settings.
                    .build()
            )
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun preLoadLanguageNativeAds(
        activity: Activity?,
        nativeAdId: String,
        listenerAdapter: AdsLoadListener?
    ) {
        isLanguageLoading = true
        val adLoader = AdLoader.Builder(activity!!, nativeAdId)
            .forNativeAd {
                Log.e("mLanguageNativeAd -------->", "Loaded")
                isLanguageLoading = false
                mLanguageNativeAd = it
                listenerAdapter!!.onAdsLoaded()
                try {
                    YandexMetrica.reportEvent("Pre Load Native Ad Loaded");
                } catch (e: Exception) {
                    e.printStackTrace();
                }
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // Handle the failure by logging, altering the UI, and so on.
                    listenerAdapter?.onAdsLoadFail(adError.code)
                    isLanguageLoading = false
                    try {
                        YandexMetrica.reportEvent("Pre Load Native Ad Failed to load");
                    } catch (e: Exception) {
                        e.printStackTrace();
                    }
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    // Methods in the NativeAdOptions.Builder class can be
                    // used here to specify individual options settings.
                    .build()
            )
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun preLoadExitNativeAds(
        activity: Activity?,
        nativeAdId: String,
        listenerAdapter: AdsLoadListener?
    ) {
        val adLoader = AdLoader.Builder(activity!!, nativeAdId)
            .forNativeAd {
                mExitNativeAd = it
                listenerAdapter!!.onAdsLoaded()
                try {
                    YandexMetrica.reportEvent("Pre Load Native Ad Loaded");
                } catch (e: Exception) {
                    e.printStackTrace();
                }
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // Handle the failure by logging, altering the UI, and so on.
                    listenerAdapter?.onAdsLoadFail(adError.code)
                    try {
                        YandexMetrica.reportEvent("Pre Load Native Ad Failed to load");
                    } catch (e: Exception) {
                        e.printStackTrace();
                    }
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    // Methods in the NativeAdOptions.Builder class can be
                    // used here to specify individual options settings.
                    .build()
            )
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun showPreLoadNativeAd(
        activity: Activity?,
        viewGroup: ViewGroup?,
        screen: String = "",
        trackingScreen: String = "",
        layout: Int,
        layoutType: NativeAdLayoutType
    ) {
        populateNativeAdView(activity, viewGroup, layout, layoutType, mNativeAd!!)
    }

    fun showPreLoadLanguageNativeAd(
        activity: Activity?,
        viewGroup: ViewGroup?,
        screen: String = "",
        trackingScreen: String = "",
        layout: Int,
        layoutType: NativeAdLayoutType
    ) {
        populateNativeAdView(activity, viewGroup, layout, layoutType, mLanguageNativeAd!!)
        mLanguageNativeAd = null
    }

    fun showPreLoadExitNativeAd(
        activity: Activity?,
        viewGroup: ViewGroup?,
        screen: String = "",
        trackingScreen: String = "",
        layout: Int,
        layoutType: NativeAdLayoutType
    ) {
        try {
            populateNativeAdView(activity, viewGroup, layout, layoutType, mExitNativeAd!!)
            mExitNativeAd = null
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

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

        if (mNativeAd != null) {
            mNativeAd = null
        }
        val adLoader = AdLoader.Builder(activity!!, nativeAdId)
            .forNativeAd {
                mNativeAd = it
                listenerAdapter!!.onAdsLoaded()
                try {
                    YandexMetrica.reportEvent("Load Native Ad Loaded");
                } catch (e: Exception) {
                    e.printStackTrace();
                }
                populateNativeAdView(activity, viewGroup, layout, layoutType, mNativeAd!!)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // Handle the failure by logging, altering the UI, and so on.
                    listenerAdapter?.onAdsLoadFail(adError.code)
                    try {
                        YandexMetrica.reportEvent("Load Native Ad Failed");
                    } catch (e: Exception) {
                        e.printStackTrace();
                    }
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    // Methods in the NativeAdOptions.Builder class can be
                    // used here to specify individual options settings.
                    .build()
            )
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun populateNativeAdView(
        activity: Context?, viewGroup: ViewGroup?, layout: Int,
        layoutType: NativeAdLayoutType, mNativeAd: NativeAd
    ) {
        val inflater = LayoutInflater.from(activity)
// Inflate the Ad view. The layout referenced should be the one you created in the last step.
        // Inflate the Ad view. The layout referenced should be the one you created in the last step.
        val adView = inflater.inflate(layout, null) as NativeAdView
        viewGroup!!.removeAllViews()
        viewGroup!!.addView(adView)

        try {
            if (layoutType == NativeAdLayoutType.CUSTOM_LAYOUT || layoutType == NativeAdLayoutType.MEDIUM_LAYOUT || layoutType == NativeAdLayoutType.DEFAULT_LAYOUT) {
                val mediaView = adView.findViewById<MediaView>(R.id.ad_media)
                mediaView.mediaContent = mNativeAd!!.mediaContent
                mediaView.setOnHierarchyChangeListener(object : OnHierarchyChangeListener {
                    override fun onChildViewAdded(parent: View, child: View) {
                        if (child is ImageView) {
                            child.adjustViewBounds = true
                        }
                    }

                    override fun onChildViewRemoved(parent: View, child: View) {}
                })
                adView.mediaView = mediaView
            } else if (adView.findViewById<View>(R.id.ad_media) != null) adView.findViewById<View>(R.id.ad_media).visibility =
                View.GONE
            adView.headlineView = adView.findViewById(R.id.ad_headline)
            adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
            adView.bodyView = adView.findViewById(R.id.ad_body)
            adView.iconView = adView.findViewById(R.id.ad_app_icon)
            adView.advertiserView = adView.findViewById(R.id.ad_advertiser)
            (adView.headlineView as TextView?)!!.text = mNativeAd!!.headline
            if (mNativeAd!!.body == null) {
                adView.bodyView!!.visibility = View.INVISIBLE
            } else if (adView.bodyView != null) {
                adView.bodyView!!.visibility = View.VISIBLE
                (adView.bodyView as TextView?)!!.text = mNativeAd!!.body
            }

            if (mNativeAd!!.callToAction == null) {
                adView.callToActionView!!.visibility = View.INVISIBLE
            } else if (adView.callToActionView != null) {
                adView.callToActionView!!.visibility = View.VISIBLE
                (adView.callToActionView as Button?)!!.text = mNativeAd!!.callToAction
            }

            if (adView.advertiserView != null) {
                if (mNativeAd!!.advertiser == null && adView.advertiserView != null) {
                    adView.advertiserView!!.visibility = View.GONE
                } else if (adView.advertiserView != null) {
                    adView.advertiserView!!.visibility = View.VISIBLE
                    (adView.advertiserView as TextView?)!!.text = mNativeAd!!.advertiser
                }
            }

            if (mNativeAd!!.icon == null) {
                adView.iconView!!.visibility = View.GONE
            } else if (adView.iconView != null) {
                (adView.iconView as ImageView?)!!.setImageDrawable(
                    mNativeAd!!.icon!!.drawable
                )
                adView.iconView!!.visibility = View.VISIBLE
            }
            if (layoutType == NativeAdLayoutType.CUSTOM_LAYOUT || layoutType == NativeAdLayoutType.MEDIUM_LAYOUT || layoutType == NativeAdLayoutType.DEFAULT_LAYOUT) {
                adView.mediaView!!.visibility = View.VISIBLE
            } else if (adView.mediaView != null) {
                adView.mediaView!!.visibility = View.GONE
            }
            adView.setNativeAd(mNativeAd!!)
            val vc = mNativeAd!!.mediaContent!!.videoController
            vc.mute(true)
            if (vc.hasVideoContent()) {
                vc.videoLifecycleCallbacks = object : VideoLifecycleCallbacks() {
                    override fun onVideoEnd() {
                        super.onVideoEnd()
                    }
                }
            }
            if (viewGroup != null) {
                viewGroup.visibility = View.VISIBLE
            }
            adView.setNativeAd(mNativeAd!!)
            try {
                YandexMetrica.reportEvent("show Native Ad Loaded");
            } catch (e: Exception) {
                e.printStackTrace();
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG", "populateUnifiedNativeAdView Exception: " + e.message)
        }
    }

    fun loadBannerAds(
        activity: Activity?,
        bannerAdId: String,
        viewGroup: ViewGroup?,
        screen: String = "",
        trackingScreen: String = "",
        listenerAdapter: AdsLoadListener?
    ) {
        val adView = AdView(activity?.applicationContext!!)
        viewGroup!!.addView(adView)
        adView.adUnitId = bannerAdId

        adView.setAdSize(getAdSize(activity, viewGroup))

        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        val adRequest = AdRequest.Builder().build()

        // Start loading the ad in the background.
        adView.loadAd(adRequest)
        adView.adListener = object : AdListener() {
            override fun onAdClicked() {
                try {
                    YandexMetrica.reportEvent("Banner Ad Click");
                } catch (e: Exception) {
                    e.printStackTrace();
                }
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                listenerAdapter!!.onAdsLoadFail(adError.code)
                Log.e("errorCode ------>0", adError.message)
                try {
                    YandexMetrica.reportEvent("Banner ad failed to load");
                } catch (e: Exception) {
                    e.printStackTrace();
                }
            }

            override fun onAdImpression() {
                try {
                    YandexMetrica.reportEvent("Banner ad impression");
                } catch (e: Exception) {
                    e.printStackTrace();
                }
            }

            override fun onAdLoaded() {
                listenerAdapter!!.onAdsLoaded()
                try {
                    YandexMetrica.reportEvent("Banner Ad loaded");
                } catch (e: Exception) {
                    e.printStackTrace();
                }
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        }
    }

    fun loadBannerAdsWithFixSize(
        activity: Activity?,
        bannerAdId: String,
        viewGroup: ViewGroup?,
        screen: String = "",
        trackingScreen: String = "",
        adSize: AdSize,
        listenerAdapter: AdsLoadListener?
    ) {
        val adView = AdView(activity?.applicationContext!!)
        viewGroup!!.addView(adView)
        adView.adUnitId = bannerAdId

        adView.setAdSize(adSize)

        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        val adRequest = AdRequest.Builder().build()

        // Start loading the ad in the background.
        adView.loadAd(adRequest)

        adView.adListener = object : AdListener() {
            override fun onAdClicked() {
                try {
                    YandexMetrica.reportEvent("Banner Ad Click");
                } catch (e: Exception) {
                    e.printStackTrace();
                }
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                try {
                    YandexMetrica.reportEvent("Banner ad failed to load");
                } catch (e: Exception) {
                    e.printStackTrace();
                }
            }

            override fun onAdImpression() {
                try {
                    YandexMetrica.reportEvent("Banner ad impression");
                } catch (e: Exception) {
                    e.printStackTrace();
                }
            }

            override fun onAdLoaded() {
                try {
                    YandexMetrica.reportEvent("Banner Ad loaded");
                } catch (e: Exception) {
                    e.printStackTrace();
                }
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        }
    }

    private fun getAdSize(activity: Activity?, viewGroup: ViewGroup?): AdSize {
        val display = activity?.windowManager?.defaultDisplay
        val outMetrics = DisplayMetrics()
        display?.getMetrics(outMetrics)

        val density = outMetrics.density

        var adWidthPixels = viewGroup?.width?.toFloat()
        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels.toFloat()
        }

        val adWidth = (adWidthPixels?.div(density))?.toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
            activity?.applicationContext!!,
            adWidth!!
        )
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