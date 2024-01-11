package com.example.jksolads.ads.aperoAds

import android.app.Activity
import android.util.Log
import android.view.View.GONE
import android.widget.FrameLayout
//import com.ads.control.ads.AperoAd
//import com.ads.control.ads.AperoAdCallback
//import com.ads.control.ads.bannerAds.AperoBannerAdView
//import com.ads.control.ads.wrapper.ApAdError
//import com.ads.control.ads.wrapper.ApNativeAd
import com.example.jksolads.ads.AdsLoadListener
import com.facebook.shimmer.ShimmerFrameLayout

class AperoAds {

//    var mLangNativeAd: ApNativeAd? = null
//    var isLanguageNativeAdLoading = false
//
//
//    companion object {
//        private var singleton: AperoAds? = null
//        fun getInstance(): AperoAds? {
//            if (singleton == null) {
//                singleton = AperoAds()
//            }
//            return singleton
//        }
//    }
//
//    fun loadLanguageNativeAd(
//        activity: Activity,
//        adId: String,
//        layout: Int,
//        adEventListener: AdsLoadListener
//    ) {
//        isLanguageNativeAdLoading = true
//        AperoAd.getInstance().loadNativeAdResultCallback(
//            activity,
//            adId,
//            layout,
//            object : AperoAdCallback() {
//                override fun onNativeAdLoaded(nativeAd: ApNativeAd) {
//                    super.onNativeAdLoaded(nativeAd)
//                    mLangNativeAd = nativeAd
//                    isLanguageNativeAdLoading = false
//                    adEventListener.onAdsLoaded()
//                }
//
//                override fun onAdFailedToLoad(adError: ApAdError?) {
//                    super.onAdFailedToLoad(adError)
//                    isLanguageNativeAdLoading = false
//                    adEventListener.onAdsLoadFail(adError!!.message.length)
//                }
//            })
//    }
//
//    fun showLanguageNativeAd(
//        activity: Activity,
//        adId: String,
//        layout: Int,
//        nativeAdView: FrameLayout,
//        shimmerFrameLayout: ShimmerFrameLayout,
//        adEventListener: AdsLoadListener
//    ) {
//        if (mLangNativeAd != null && !isLanguageNativeAdLoading) {
//            AperoAd.getInstance().populateNativeAdView(
//                activity,
//                mLangNativeAd,
//                nativeAdView,
//                shimmerFrameLayout
//            )
//            mLangNativeAd = null
//        } else if (mLangNativeAd == null && !isLanguageNativeAdLoading) {
//            shimmerFrameLayout.visibility = GONE
//            adEventListener.onAdsLoadFail(0)
//        } else {
//            loadLanguageNativeAd(activity, adId, layout, object : AdsLoadListener {
//                override fun onAdsLoadFail(errorCode: Int) {
//                    Log.e("errorCode ------>", errorCode.toString())
//                    shimmerFrameLayout.visibility = GONE
//                }
//
//                override fun onAdsLoaded() {
//                    showLanguageNativeAd(
//                        activity!!,
//                        adId,
//                        layout!!,
//                        nativeAdView,
//                        shimmerFrameLayout,
//                        adEventListener!!
//                    )
//                }
//
//            })
//        }
//    }
//
//    fun loadAndShowNativeAd(
//        activity: Activity,
//        adId: String,
//        layout: Int,
//        nativeAdView: FrameLayout,
//        shimmerFrameLayout: ShimmerFrameLayout,
//        adEventListener: AdsLoadListener
//    ) {
//        AperoAd.getInstance().loadNativeAdResultCallback(
//            activity,
//            adId,
//            layout,
//            object : AperoAdCallback() {
//                override fun onNativeAdLoaded(nativeAd: ApNativeAd) {
//                    super.onNativeAdLoaded(nativeAd)
//                    AperoAd.getInstance().populateNativeAdView(
//                        activity,
//                        nativeAd,
//                        nativeAdView,
//                        shimmerFrameLayout
//                    )
//                }
//
//                override fun onAdFailedToLoad(adError: ApAdError?) {
//                    super.onAdFailedToLoad(adError)
//                }
//            })
//    }
//
//
//    fun loadBanner(
//        activity: Activity?,
//        viewGroup: AperoBannerAdView,
//        adEventListener: AdsLoadListener?,
//        bannerAdId: String?
//    ) {
//        viewGroup.loadBanner(activity, bannerAdId)
//    }
}