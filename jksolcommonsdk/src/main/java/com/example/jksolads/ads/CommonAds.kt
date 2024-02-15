package com.example.jksolads.ads

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.multidex.MultiDexApplication
import com.ads.control.ads.AperoAd
import com.ads.control.ads.bannerAds.AperoBannerAdView
import com.ads.control.ads.wrapper.ApInterstitialAd
import com.ads.control.config.AdjustConfig
import com.ads.control.config.AperoAdConfig
//import com.ads.control.ads.AperoAd
//import com.ads.control.ads.bannerAds.AperoBannerAdView
//import com.ads.control.ads.wrapper.ApInterstitialAd
//import com.ads.control.config.AdjustConfig
//import com.ads.control.config.AperoAdConfig
import com.applovin.sdk.AppLovinSdk
import com.example.jksolads.BuildConfig
import com.example.jksolads.ads.AdMob.AdMob
import com.example.jksolads.ads.AdMob.AppOpenAdManager
import com.example.jksolads.ads.Applovin.AppOpenApplovinAd
import com.example.jksolads.ads.Applovin.Applovin
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.yandex.metrica.YandexMetrica

public class CommonAds {
    private var adMob: AdMob? = null
    private lateinit var adsConfig: AdsConfig
    private lateinit var context: Application
    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager

    companion object {
        private var INSTANCE: CommonAds? = null
        fun getInstance(): CommonAds {
            if (INSTANCE == null) {
                INSTANCE = CommonAds()
            }
            return INSTANCE!!
        }
    }

    fun init(adsConfig: AdsConfig) {
        this.adsConfig = adsConfig
        this.context = adsConfig.context
        var aperoAdConfig: AperoAdConfig? = null
        if (adsConfig.adsProvider == AdsProvider.PROVIDER_ADMOB) {
            if (adMob == null) {
                adMob = AdMob.getInstance()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val process = MultiDexApplication.getProcessName()
                if (context.packageName != process) WebView.setDataDirectorySuffix(process)
            } else {
                MobileAds.initialize(
                    context
                ) { _: InitializationStatus? -> }
            }

            System.setProperty("java.util.Arrays.useLegacyMergeSort", "true")
            val appOpenAdManager = AppOpenAdManager(context, adsConfig.resumeAdId!!)
            appOpenAdManager.mActivity = adsConfig.mActivity
            appOpenAdManager.isShowAppOpenAds = adsConfig.isShowAppOpenAds

            val environment =
                if (BuildConfig.DEBUG) AperoAdConfig.ENVIRONMENT_DEVELOP else AperoAdConfig.ENVIRONMENT_PRODUCTION
            aperoAdConfig = AperoAdConfig(context, AperoAdConfig.PROVIDER_ADMOB, environment)

        } else if (adsConfig.adsProvider == AdsProvider.PROVIDER_APPLOVIN) {
            AppLovinSdk.getInstance(context).mediationProvider = "max"
            AppLovinSdk.initializeSdk(context) {
                if (adsConfig.isShowAppOpenAds) {
                    AppOpenApplovinAd(adsConfig.context, adsConfig.resumeAdId!!)
                }
            }

            val environment =
                if (BuildConfig.DEBUG) AperoAdConfig.ENVIRONMENT_DEVELOP else AperoAdConfig.ENVIRONMENT_PRODUCTION
            aperoAdConfig = AperoAdConfig(context, AperoAdConfig.PROVIDER_MAX, environment);
        }

        val adjustConfig = AdjustConfig(true)
        aperoAdConfig!!.setAdjustConfig(adjustConfig)
        aperoAdConfig!!.setVariant(true)
        AperoAd.getInstance().init(context, aperoAdConfig, false)
    }

    fun isInterstitialAdLoading(): Boolean {
        return when (adsConfig.adsProvider) {
            AdsProvider.PROVIDER_ADMOB ->
                adMob!!.isLoading

            AdsProvider.PROVIDER_APPLOVIN -> Applovin.getInstance().isLoading

            else -> false
        }

    }

    fun isInterstitialAdEmpty(): Boolean {
        return when (adsConfig.adsProvider) {
            AdsProvider.PROVIDER_ADMOB ->
                adMob!!.mInterstitialAd != null

            AdsProvider.PROVIDER_APPLOVIN -> Applovin.getInstance().mInterstitialAd != null

            else -> false
        }
    }

    fun isLanguageNativeAdLoaded(): Boolean {
        return when (adsConfig.adsProvider) {
            AdsProvider.PROVIDER_ADMOB ->
                adMob!!.mLanguageNativeAd != null

            AdsProvider.PROVIDER_APPLOVIN -> Applovin.getInstance().mLanguageNativeAd != null
            else -> false
        }
    }

    fun isExitNativeAdLoaded(): Boolean {
        return when (adsConfig.adsProvider) {
            AdsProvider.PROVIDER_ADMOB ->
                adMob!!.mExitNativeAd != null

            AdsProvider.PROVIDER_APPLOVIN -> Applovin.getInstance().mExitNativeAd != null

            else -> false
        }
    }

    fun isLanguageNativeAdLoading(): Boolean {
        return when (adsConfig.adsProvider) {
            AdsProvider.PROVIDER_ADMOB ->
                adMob!!.isLanguageLoading

            AdsProvider.PROVIDER_APPLOVIN -> Applovin.getInstance().isLanguageLoading
            else -> false
        }
    }

    fun setAperoLanguageNativeAd() {
        return adMob!!.setAperoLanguageNativeAd()
    }

    fun loadInterstitialAds(
        activity: Activity?,
        interstitialAdId: String,
        screenAds: String = "",
        screenType: ScreenAds,
        trackingScreen: String = "",
        enableLoadBackUpAds: Boolean = false,
        enableResetReload: Boolean = false,
        adsLoadListener: AdsLoadListener
    ) {
        try {
            YandexMetrica.reportEvent("Request to load interstitial Ad")
        } catch (e: Exception) {
            YandexMetrica.reportEvent("ad_loaded_app_crash")
            e.printStackTrace()
        }

        when (adsConfig.adsProvider) {
            AdsProvider.PROVIDER_ADMOB -> {
                googleMobileAdsConsentManager = GoogleMobileAdsConsentManager(activity!!)
                googleMobileAdsConsentManager.gatherConsent { consentError ->
                    if (consentError != null) {
                        if (googleMobileAdsConsentManager.canRequestAds) {
                            adMob!!.loadInterstitialAds(
                                activity,
                                interstitialAdId,
                                screenAds,
                                screenType,
                                trackingScreen,
                                enableLoadBackUpAds,
                                enableResetReload, adsLoadListener
                            )
                        }
                    }
                }

//        if (googleMobileAdsConsentManager.canRequestAds) {
                adMob!!.loadInterstitialAds(
                    activity,
                    interstitialAdId,
                    screenAds,
                    screenType,
                    trackingScreen,
                    enableLoadBackUpAds,
                    enableResetReload, adsLoadListener
                )
            }

            AdsProvider.PROVIDER_APPLOVIN -> Applovin.getInstance().loadInterstitialAds(
                activity,
                interstitialAdId,
                screenAds,
                screenType,
                trackingScreen,
                enableLoadBackUpAds,
                enableResetReload, adsLoadListener
            )
        }
//        }

    }

    fun showInterstitialAds(
        activity: Activity?,
        screen: String = "",
        trackingScreen: String = "",
        showLoading: Boolean = false,
        adsListener: AdsListener?
    ) {
        try {
            YandexMetrica.reportEvent("Request to show interstitial Ad")
        } catch (e: Exception) {
            YandexMetrica.reportEvent("ad_loaded_app_crash")
            e.printStackTrace()
        }
        when (adsConfig.adsProvider) {
            AdsProvider.PROVIDER_ADMOB ->
                adMob!!.showInterstitialAds(
                    activity,
                    screen,
                    trackingScreen,
                    showLoading,
                    adsListener
                )

            AdsProvider.PROVIDER_APPLOVIN -> Applovin.getInstance()
                .showInterstitialAds(activity, screen, trackingScreen, showLoading, adsListener)
        }
    }

    fun loadAndShowInterstitialAd(
        activity: Activity?,
        interstitialAdId: String, adsLoadListener: AdsListener
    ) {
        when (adsConfig.adsProvider) {
            AdsProvider.PROVIDER_ADMOB ->
                adMob!!.loadAndShowInterstitialAd(
                    activity,
                    interstitialAdId,
                    adsLoadListener
                )

            AdsProvider.PROVIDER_APPLOVIN -> Applovin.getInstance().loadAndShowInterstitialAd(
                activity,
                interstitialAdId,
                adsLoadListener
            )
        }
    }

    fun getAperoInterstitialAd(
        activity: Activity?,
        interstitialAdId: String, adsLoadListener: AdsListener
    ) : ApInterstitialAd {
        return when (adsConfig.adsProvider) {
            AdsProvider.PROVIDER_ADMOB ->
                adMob!!.getAperoInterstitialAd(
                    activity,
                    interstitialAdId,
                    adsLoadListener
                )

            AdsProvider.PROVIDER_APPLOVIN ->
                Applovin.getInstance().getAperoInterstitialAd(
                    activity,
                    interstitialAdId,
                    adsLoadListener
                )
        }
    }

    fun preLoadNativeAds(
        activity: Activity?,
        nativeAdId: String,
        layout: Int,
        listenerAdapter: AdsLoadListener?
    ) {
        try {
            YandexMetrica.reportEvent("Request to load native Ad")
        } catch (e: Exception) {
            YandexMetrica.reportEvent("ad_loaded_app_crash")
            e.printStackTrace()
        }
        when (adsConfig.adsProvider) {
            AdsProvider.PROVIDER_ADMOB ->
                adMob!!.preLoadNativeAds(activity, nativeAdId, listenerAdapter)

            AdsProvider.PROVIDER_APPLOVIN -> Applovin.getInstance()
                .preLoadNativeAds(activity, nativeAdId, layout, listenerAdapter)
        }
    }

    fun preLoadLanguageNativeAds(
        activity: Activity?,
        nativeAdId: String,
        layout: Int,
        listenerAdapter: AdsLoadListener?
    ) {
        try {
            YandexMetrica.reportEvent("Request to load native Ad")
        } catch (e: Exception) {
            YandexMetrica.reportEvent("ad_loaded_app_crash")
            e.printStackTrace()
        }

        when (adsConfig.adsProvider) {
            AdsProvider.PROVIDER_ADMOB ->
                adMob!!.preLoadLanguageNativeAds(activity, nativeAdId, listenerAdapter)

            AdsProvider.PROVIDER_APPLOVIN -> Applovin.getInstance()
                .preLoadLanguageNativeAds(activity, nativeAdId, layout, listenerAdapter)
        }
    }

    fun preLoadExitNativeAds(
        activity: Activity?,
        nativeAdId: String,
        layout: Int,
        listenerAdapter: AdsLoadListener?
    ) {
        try {
            YandexMetrica.reportEvent("Request to load native Ad")
        } catch (e: Exception) {
            YandexMetrica.reportEvent("ad_loaded_app_crash")
            e.printStackTrace()
        }
        when (adsConfig.adsProvider) {
            AdsProvider.PROVIDER_ADMOB ->
                adMob!!.preLoadExitNativeAds(activity, nativeAdId, listenerAdapter)

            AdsProvider.PROVIDER_APPLOVIN -> Applovin.getInstance()
                .preLoadExitNativeAds(activity, nativeAdId, layout, listenerAdapter)
        }
    }

    fun showPreLoadNativeAd(
        activity: Activity?,
        viewGroup: ViewGroup?,
        screen: String = "",
        trackingScreen: String = "",
        layout: Int,
        layoutType: NativeAdLayoutType
    ) {
        try {
            YandexMetrica.reportEvent("Request to show native Ad")
        } catch (e: Exception) {
            YandexMetrica.reportEvent("ad_loaded_app_crash")
            e.printStackTrace()
        }
        when (adsConfig.adsProvider) {
            AdsProvider.PROVIDER_ADMOB ->
                adMob!!.showPreLoadNativeAd(
                    activity,
                    viewGroup,
                    screen,
                    trackingScreen,
                    layout,
                    layoutType
                )

            AdsProvider.PROVIDER_APPLOVIN -> Applovin.getInstance().showPreLoadNativeAd(
                activity,
                viewGroup,
                screen,
                trackingScreen,
                layout,
                layoutType
            )
        }
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
            YandexMetrica.reportEvent("Request to show native Ad")
        } catch (e: Exception) {
            YandexMetrica.reportEvent("ad_loaded_app_crash")
            e.printStackTrace()
        }
        when (adsConfig.adsProvider) {
            AdsProvider.PROVIDER_ADMOB ->
                adMob!!.showPreLoadExitNativeAd(
                    activity,
                    viewGroup,
                    screen,
                    trackingScreen,
                    layout,
                    layoutType
                )

            AdsProvider.PROVIDER_APPLOVIN -> Applovin.getInstance().showPreLoadExitNativeAd(
                activity,
                viewGroup,
                screen,
                trackingScreen,
                layout,
                layoutType
            )
        }
    }

    fun showPreLoadLanguageNativeAd(
        activity: Activity?,
        viewGroup: ViewGroup?,
        screen: String = "",
        trackingScreen: String = "",
        layout: Int,
        layoutType: NativeAdLayoutType
    ) {
        try {
            YandexMetrica.reportEvent("Request to show native Ad")
        } catch (e: Exception) {
            YandexMetrica.reportEvent("ad_loaded_app_crash")
            e.printStackTrace()
        }
        when (adsConfig.adsProvider) {
            AdsProvider.PROVIDER_ADMOB ->
                adMob!!.showPreLoadLanguageNativeAd(
                    activity,
                    viewGroup,
                    screen,
                    trackingScreen,
                    layout,
                    layoutType
                )

            AdsProvider.PROVIDER_APPLOVIN -> Applovin.getInstance().showPreLoadLanguageNativeAd(
                activity,
                viewGroup,
                screen,
                trackingScreen,
                layout,
                layoutType
            )
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
        try {
            YandexMetrica.reportEvent("Request to load native Ad")
        } catch (e: Exception) {
            YandexMetrica.reportEvent("ad_loaded_app_crash")
            e.printStackTrace()
        }
        when (adsConfig.adsProvider) {
            AdsProvider.PROVIDER_ADMOB ->
                adMob!!.loadNativeAds(
                    activity,
                    nativeAdId,
                    viewGroup,
                    screen,
                    trackingScreen,
                    layout,
                    layoutType,
                    listenerAdapter
                )

            AdsProvider.PROVIDER_APPLOVIN -> Applovin.getInstance().loadNativeAds(
                activity,
                nativeAdId,
                viewGroup,
                screen,
                trackingScreen,
                layout,
                layoutType,
                listenerAdapter
            )
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
        try {
            YandexMetrica.reportEvent("Request to load banner Ad")
        } catch (e: Exception) {
            YandexMetrica.reportEvent("ad_loaded_app_crash")
            e.printStackTrace()
        }

        when (adsConfig.adsProvider) {
            AdsProvider.PROVIDER_ADMOB ->
                adMob!!.loadBannerAds(
                    activity,
                    bannerAdId,
                    viewGroup,
                    screen,
                    trackingScreen,
                    listenerAdapter
                )

            AdsProvider.PROVIDER_APPLOVIN -> Applovin.getInstance().loadBannerAd(
                activity,
                bannerAdId,
                viewGroup, listenerAdapter!!
            )
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

        try {
            YandexMetrica.reportEvent("Request to load banner fix size Ad")
        } catch (e: Exception) {
            YandexMetrica.reportEvent("ad_loaded_app_crash")
            e.printStackTrace()
        }

        when (adsConfig.adsProvider) {
            AdsProvider.PROVIDER_ADMOB ->
                adMob!!.loadBannerAdsWithFixSize(
                    activity,
                    bannerAdId,
                    viewGroup,
                    screen,
                    trackingScreen,
                    adSize,
                    listenerAdapter
                )

            AdsProvider.PROVIDER_APPLOVIN -> Applovin.getInstance().loadBannerAd(
                activity,
                bannerAdId,
                viewGroup, listenerAdapter!!
            )
        }

    }

    fun loadAperoLanguageNativeAd(
        activity: Activity?,
        nativeId: String,
        layout: Int?,
        listenerAdapter: AdsLoadListener?
    ) {
        when (adsConfig.adsProvider) {
            AdsProvider.PROVIDER_ADMOB -> adMob!!.loadAperoLanguageNativeAd(
                activity,
                nativeId,
                layout,
                listenerAdapter
            )

            AdsProvider.PROVIDER_APPLOVIN -> Applovin.getInstance().loadAperoLanguageNativeAd(
                activity,
                nativeId,
                layout,
                listenerAdapter
            )
        }
    }

    fun showAperoLanguageNativeAd(
        activity: Activity?,
        nativeId: String,
        layout: Int?,
        nativeAdView: FrameLayout,
        shimmerFrameLayout: ShimmerFrameLayout,
        listenerAdapter: AdsLoadListener?
    ) {
        when (adsConfig.adsProvider) {
            AdsProvider.PROVIDER_ADMOB -> adMob!!.showAperoLanguageNativeAd(
                activity,
                nativeId,
                layout,
                nativeAdView, shimmerFrameLayout,
                listenerAdapter
            )

            AdsProvider.PROVIDER_APPLOVIN -> Applovin.getInstance().showAperoLanguageNativeAd(
                activity,
                nativeId,
                layout,
                nativeAdView, shimmerFrameLayout,
                listenerAdapter
            )
        }

    }

    fun loadAndShowAperoNativeAd(
        activity: Activity?,
        nativeId: String,
        layout: Int?,
        nativeAdView: FrameLayout,
        shimmerFrameLayout: ShimmerFrameLayout,
        listenerAdapter: AdsLoadListener?
    ) {
        when (adsConfig.adsProvider) {
            AdsProvider.PROVIDER_ADMOB -> adMob!!.loadAndShowAperoNativeAd(
                activity,
                nativeId,
                layout,
                nativeAdView, shimmerFrameLayout,
                listenerAdapter
            )

            AdsProvider.PROVIDER_APPLOVIN -> Applovin.getInstance().loadAndShowAperoNativeAd(
                activity,
                nativeId,
                layout,
                nativeAdView, shimmerFrameLayout,
                listenerAdapter
            )
        }
    }

    fun showAperoBannerAd(
        activity: Activity?,
        bannerAdId: String?,
        viewGroup: AperoBannerAdView?,
        adEventListener: AdsLoadListener?

    ) {
        when (adsConfig.adsProvider) {
            AdsProvider.PROVIDER_ADMOB ->
                adMob!!.showAperoBannerAd(activity!!, bannerAdId!!, viewGroup, adEventListener)

            AdsProvider.PROVIDER_APPLOVIN -> Applovin.getInstance()
                .showAperoBannerAd(activity!!, bannerAdId!!, viewGroup, adEventListener)
        }
    }
}