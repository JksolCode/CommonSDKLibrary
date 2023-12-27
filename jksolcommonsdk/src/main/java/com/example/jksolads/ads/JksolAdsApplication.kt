package com.example.jksolads.ads

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication
import com.ads.control.ads.AperoAd
import com.ads.control.config.AdjustConfig
import com.ads.control.config.AperoAdConfig
import com.example.jksolads.BuildConfig
import com.example.jksolads.ads.AdMob.AppOpenAdManager
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.onesignal.OneSignal
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig

open class JksolAdsApplication : MultiDexApplication(){

    private var isShowSplashAppOpenAds: Boolean = false
    private var splashAppOpenAdId: String? = null

    override fun onCreate() {
        super.onCreate()

        val environment =
            if (BuildConfig.DEBUG) AperoAdConfig.ENVIRONMENT_DEVELOP else AperoAdConfig.ENVIRONMENT_PRODUCTION
        val aperoAdConfig = AperoAdConfig(this, AperoAdConfig.PROVIDER_ADMOB, environment);

        // Optional: setup Adjust event
        val adjustConfig = AdjustConfig(true)
        aperoAdConfig.setAdjustConfig(adjustConfig)
        aperoAdConfig.setVariant(true)
        AperoAd.getInstance().init(this, aperoAdConfig, false);
    }

}